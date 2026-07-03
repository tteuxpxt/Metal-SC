package com.metalSpring.services;

import com.metalSpring.model.dto.AlertaModeracaoDTO;
import com.metalSpring.model.dto.NegociacaoConversaDTO;
import com.metalSpring.model.dto.NegociacaoMensagemDTO;
import com.metalSpring.model.entity.AlertaModeracao;
import com.metalSpring.model.entity.Cliente;
import com.metalSpring.model.entity.ConversaNegociacao;
import com.metalSpring.model.entity.MensagemNegociacao;
import com.metalSpring.model.entity.Pedido;
import com.metalSpring.model.entity.Peca;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.model.enums.AlertaModeracaoStatus;
import com.metalSpring.model.enums.NegociacaoStatus;
import com.metalSpring.model.enums.NivelRiscoModeracao;
import com.metalSpring.model.enums.PagamentoStatus;
import com.metalSpring.model.enums.PedidoStatus;
import com.metalSpring.model.enums.TipoMensagemNegociacao;
import com.metalSpring.repository.AlertaModeracaoRepository;
import com.metalSpring.repository.ClienteRepository;
import com.metalSpring.repository.ConversaNegociacaoRepository;
import com.metalSpring.repository.MensagemNegociacaoRepository;
import com.metalSpring.repository.PedidoRepository;
import com.metalSpring.repository.PecaRepository;
import com.metalSpring.repository.UsuarioRepository;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NegociacaoService {

    @Autowired
    private ConversaNegociacaoRepository conversaRepository;

    @Autowired
    private MensagemNegociacaoRepository mensagemRepository;

    @Autowired
    private AlertaModeracaoRepository alertaRepository;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final List<RegraModeracao> REGRAS = List.of(
            new RegraModeracao("whatsapp", "aplicativo externo", NivelRiscoModeracao.ALTO),
            new RegraModeracao("wa.me", "link externo", NivelRiscoModeracao.ALTO),
            new RegraModeracao("telegram", "aplicativo externo", NivelRiscoModeracao.ALTO),
            new RegraModeracao("instagram", "rede social", NivelRiscoModeracao.ALTO),
            new RegraModeracao("direct", "contato externo", NivelRiscoModeracao.MEDIO),
            new RegraModeracao("me chama", "contato externo", NivelRiscoModeracao.MEDIO),
            new RegraModeracao("chama no zap", "contato externo", NivelRiscoModeracao.ALTO),
            new RegraModeracao("meu numero e", "telefone", NivelRiscoModeracao.ALTO),
            new RegraModeracao("telefone", "telefone", NivelRiscoModeracao.MEDIO),
            new RegraModeracao("gmail", "email", NivelRiscoModeracao.MEDIO),
            new RegraModeracao("hotmail", "email", NivelRiscoModeracao.MEDIO),
            new RegraModeracao("outlook", "email", NivelRiscoModeracao.MEDIO),
            new RegraModeracao("discord", "aplicativo externo", NivelRiscoModeracao.ALTO),
            new RegraModeracao("pix", "pagamento externo", NivelRiscoModeracao.ALTO),
            new RegraModeracao("qr code", "qr code textual", NivelRiscoModeracao.ALTO),
            new RegraModeracao("qrcode", "qr code textual", NivelRiscoModeracao.ALTO)
    );

    private static final Pattern EMAIL = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern URL = Pattern.compile("(https?://\\S+|www\\.\\S+|\\S+\\.com\\S*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern USUARIO_EXTERNO = Pattern.compile("(^|\\s)@[\\w._-]{3,}");
    private static final Pattern TELEFONE = Pattern.compile("(?<!\\d)(?:\\+?55\\s*)?(?:\\(?\\d{2}\\)?\\s*)?9?\\d{4}[-\\s.]?\\d{4}(?!\\d)");

    @Transactional
    public NegociacaoConversaDTO iniciar(String pecaId, String clienteId, String conteudo, Double valorProposto) {
        ConversaNegociacao conversa = conversaRepository.findByPecaIdAndClienteId(pecaId, clienteId)
                .orElseGet(() -> criarConversa(pecaId, clienteId));

        if ((conteudo != null && !conteudo.isBlank()) || valorProposto != null) {
            enviarMensagem(conversa.getId(), clienteId, conteudo, valorProposto, TipoMensagemNegociacao.PROPOSTA);
        }

        return toConversaDTO(buscarConversa(conversa.getId()), clienteId);
    }

    @Transactional
    public NegociacaoConversaDTO iniciarPorPedido(Pedido pedido) {
        if (pedido == null || pedido.getId() == null) {
            throw new RuntimeException("Pedido invalido para negociacao");
        }
        ConversaNegociacao existente = conversaRepository.findByPedidoId(pedido.getId()).orElse(null);
        if (existente != null) {
            return toConversaDTO(existente, pedido.getCliente().getId());
        }
        Peca peca = pedido.getItens().stream()
                .findFirst()
                .map(item -> item.getPeca())
                .orElseThrow(() -> new RuntimeException("Pedido sem itens para negociacao"));

        ConversaNegociacao conversa = new ConversaNegociacao();
        conversa.setPedido(pedido);
        conversa.setPeca(peca);
        conversa.setCliente(clienteRepository.findById(pedido.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente nao encontrado")));
        conversa.setRevendedor(pedido.getVendedor());
        conversa.setValorOriginal(pedido.getValorTotal());
        conversa.setValorNegociado(pedido.getValorTotal());
        conversa.setStatus(NegociacaoStatus.EM_NEGOCIACAO);
        ConversaNegociacao salva = conversaRepository.save(conversa);
        pedido.registrarEvento("Chat de negociacao aberto: " + salva.getId());
        pedidoRepository.save(pedido);

        Usuario cliente = pedido.getCliente();
        Usuario revendedor = pedido.getVendedor();
        mensagemRepository.save(criarMensagem(
                salva,
                cliente,
                revendedor,
                "Pedido criado. Negociacao aberta para definir preco, frete e condicoes.",
                pedido.getValorTotal(),
                TipoMensagemNegociacao.SISTEMA
        ));
        return toConversaDTO(salva, pedido.getCliente().getId());
    }

    private ConversaNegociacao criarConversa(String pecaId, String clienteId) {
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RuntimeException("Peca nao encontrada"));
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"));
        Revendedor revendedor = peca.getVendedor();
        if (revendedor == null) {
            throw new RuntimeException("Revendedor nao encontrado para a peca");
        }
        if (revendedor.getId().equals(cliente.getId())) {
            throw new RuntimeException("Cliente e revendedor nao podem ser o mesmo usuario");
        }

        ConversaNegociacao conversa = new ConversaNegociacao();
        conversa.setPeca(peca);
        conversa.setCliente(cliente);
        conversa.setRevendedor(revendedor);
        conversa.setValorOriginal(peca.getPreco());
        return conversaRepository.save(conversa);
    }

    public ConversaNegociacao buscarConversa(String conversaId) {
        return conversaRepository.findById(conversaId)
                .orElseThrow(() -> new RuntimeException("Conversa nao encontrada"));
    }

    public List<NegociacaoConversaDTO> listarPorCliente(String clienteId) {
        return conversaRepository.findByClienteIdOrderByAtualizadaEmDesc(clienteId).stream()
                .map(conversa -> toConversaDTO(conversa, clienteId))
                .toList();
    }

    public List<NegociacaoConversaDTO> listarPorRevendedor(String revendedorId) {
        return conversaRepository.findByRevendedorIdOrderByAtualizadaEmDesc(revendedorId).stream()
                .map(conversa -> toConversaDTO(conversa, revendedorId))
                .toList();
    }

    public NegociacaoConversaDTO obter(String conversaId, String visualizadorId) {
        return toConversaDTO(buscarConversa(conversaId), visualizadorId);
    }

    @Transactional
    public NegociacaoMensagemDTO enviarMensagem(
            String conversaId,
            String remetenteId,
            String conteudo,
            Double valorProposto,
            TipoMensagemNegociacao tipo
    ) {
        ConversaNegociacao conversa = buscarConversa(conversaId);
        Usuario remetente = usuarioRepository.findById(remetenteId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
        Usuario destinatario = obterDestinatario(conversa, remetenteId);

        if (conversa.getStatus() == NegociacaoStatus.FECHADO || conversa.getStatus() == NegociacaoStatus.CANCELADO) {
            throw new RuntimeException("Negociacao encerrada");
        }

        TipoMensagemNegociacao tipoAplicado = tipo != null ? tipo : TipoMensagemNegociacao.TEXTO;
        if (valorProposto != null && tipoAplicado == TipoMensagemNegociacao.TEXTO) {
            tipoAplicado = remetenteId.equals(conversa.getRevendedor().getId())
                    ? TipoMensagemNegociacao.CONTRAPROPOSTA
                    : TipoMensagemNegociacao.PROPOSTA;
        }

        MensagemNegociacao mensagem = criarMensagem(conversa, remetente, destinatario, conteudo, valorProposto, tipoAplicado);
        if (valorProposto != null) {
            conversa.setValorNegociado(valorProposto);
            conversa.setAprovacaoCliente(false);
            conversa.setAprovacaoRevendedor(false);
            conversa.setDataAprovacaoCliente(null);
            conversa.setDataAprovacaoRevendedor(null);
            if (conversa.getPedido() != null) {
                Pedido pedido = conversa.getPedido();
                pedido.setAprovacaoCliente(false);
                pedido.setAprovacaoRevendedor(false);
                pedido.setDataAprovacaoCliente(null);
                pedido.setDataAprovacaoRevendedor(null);
                pedido.setStatus(PedidoStatus.AGUARDANDO_NEGOCIACAO);
                pedido.setStatusPagamento(PagamentoStatus.BLOQUEADO_AGUARDANDO_NEGOCIACAO);
                pedido.registrarEvento("Nova proposta enviada por " + remetenteId + " no valor " + valorProposto);
                pedidoRepository.save(pedido);
            }
        }
        conversa.setStatus(NegociacaoStatus.AGUARDANDO_RESPOSTA);
        mensagem.setStatusNegociacao(conversa.getStatus());
        conversa.tocar();
        conversaRepository.save(conversa);

        MensagemNegociacao salva = mensagemRepository.save(mensagem);
        verificarModeracao(salva);
        return toMensagemDTO(salva);
    }

    @Transactional
    public NegociacaoConversaDTO aprovar(String conversaId, String usuarioId) {
        ConversaNegociacao conversa = buscarConversa(conversaId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
        Usuario destinatario = obterDestinatario(conversa, usuarioId);
        LocalDateTime agora = LocalDateTime.now();

        if (conversa.getStatus() == NegociacaoStatus.CANCELADO || conversa.getStatus() == NegociacaoStatus.FECHADO) {
            throw new RuntimeException("Negociacao encerrada");
        }
        if (conversa.getCliente().getId().equals(usuarioId)) {
            conversa.setAprovacaoCliente(true);
            conversa.setDataAprovacaoCliente(agora);
        } else if (conversa.getRevendedor().getId().equals(usuarioId)) {
            conversa.setAprovacaoRevendedor(true);
            conversa.setDataAprovacaoRevendedor(agora);
        } else {
            throw new RuntimeException("Usuario nao participa desta negociacao");
        }

        boolean aprovado = Boolean.TRUE.equals(conversa.getAprovacaoCliente())
                && Boolean.TRUE.equals(conversa.getAprovacaoRevendedor());
        conversa.setStatus(aprovado ? NegociacaoStatus.APROVADA : NegociacaoStatus.AGUARDANDO_APROVACAO_DUPLA);
        if (aprovado) {
            conversa.setValorFinalAcordado(conversa.getValorNegociado() != null ? conversa.getValorNegociado() : conversa.getValorOriginal());
        }
        conversa.tocar();
        conversaRepository.save(conversa);

        if (conversa.getPedido() != null) {
            Pedido pedido = conversa.getPedido();
            pedido.setAprovacaoCliente(conversa.getAprovacaoCliente());
            pedido.setAprovacaoRevendedor(conversa.getAprovacaoRevendedor());
            pedido.setDataAprovacaoCliente(conversa.getDataAprovacaoCliente());
            pedido.setDataAprovacaoRevendedor(conversa.getDataAprovacaoRevendedor());
            pedido.setValorFinalNegociado(conversa.getValorFinalAcordado());
            pedido.registrarEvento("Negociacao aprovada por " + usuarioId + ". Valor atual: "
                    + (conversa.getValorNegociado() != null ? conversa.getValorNegociado() : conversa.getValorOriginal()));
            if (aprovado) {
                pedido.setStatus(PedidoStatus.PAGAMENTO_LIBERADO);
                pedido.setStatusPagamento(PagamentoStatus.PAGAMENTO_PENDENTE);
                pedido.registrarEvento("Aprovacao dupla concluida. Pagamento liberado no valor "
                        + pedido.getValorFinalNegociado());
            }
            pedidoRepository.save(pedido);
        }

        mensagemRepository.save(criarMensagem(
                conversa,
                usuario,
                destinatario,
                aprovado ? "Termos aprovados pelos dois lados. Pagamento liberado." : "Termos aprovados. Aguardando aprovacao da outra parte.",
                conversa.getValorNegociado(),
                TipoMensagemNegociacao.APROVACAO
        ));
        return toConversaDTO(conversa, usuarioId);
    }

    @Transactional
    public NegociacaoMensagemDTO aceitar(String conversaId, String remetenteId) {
        ConversaNegociacao conversa = buscarConversa(conversaId);
        Usuario remetente = usuarioRepository.findById(remetenteId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
        Usuario destinatario = obterDestinatario(conversa, remetenteId);
        conversa.setStatus(NegociacaoStatus.FECHADO);
        conversa.tocar();
        conversaRepository.save(conversa);
        return toMensagemDTO(mensagemRepository.save(criarMensagem(
                conversa,
                remetente,
                destinatario,
                "Proposta aceita. Negociacao fechada.",
                conversa.getValorNegociado(),
                TipoMensagemNegociacao.ACEITE
        )));
    }

    @Transactional
    public NegociacaoMensagemDTO recusar(String conversaId, String remetenteId) {
        ConversaNegociacao conversa = buscarConversa(conversaId);
        Usuario remetente = usuarioRepository.findById(remetenteId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
        Usuario destinatario = obterDestinatario(conversa, remetenteId);
        conversa.setStatus(NegociacaoStatus.EM_NEGOCIACAO);
        conversa.tocar();
        conversaRepository.save(conversa);
        return toMensagemDTO(mensagemRepository.save(criarMensagem(
                conversa,
                remetente,
                destinatario,
                "Proposta recusada.",
                conversa.getValorNegociado(),
                TipoMensagemNegociacao.RECUSA
        )));
    }

    @Transactional
    public NegociacaoConversaDTO encerrar(String conversaId, String usuarioId, NegociacaoStatus status) {
        ConversaNegociacao conversa = buscarConversa(conversaId);
        if (status != NegociacaoStatus.FECHADO && status != NegociacaoStatus.CANCELADO) {
            throw new RuntimeException("Status de encerramento invalido");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
        Usuario destinatario = obterDestinatario(conversa, usuarioId);
        conversa.setStatus(status);
        conversa.tocar();
        conversaRepository.save(conversa);
        mensagemRepository.save(criarMensagem(
                conversa,
                usuario,
                destinatario,
                status == NegociacaoStatus.FECHADO ? "Negociacao fechada." : "Negociacao cancelada.",
                conversa.getValorNegociado(),
                TipoMensagemNegociacao.SISTEMA
        ));
        return toConversaDTO(conversa, usuarioId);
    }

    @Transactional
    public void marcarComoLidas(String conversaId, String usuarioId) {
        List<MensagemNegociacao> mensagens = mensagemRepository.findByConversaIdAndDestinatarioIdAndLidaFalse(conversaId, usuarioId);
        mensagens.forEach(mensagem -> mensagem.setLida(true));
        mensagemRepository.saveAll(mensagens);
    }

    public List<AlertaModeracaoDTO> listarAlertas(String usuarioId, String data, String tipo, AlertaModeracaoStatus status) {
        List<AlertaModeracao> alertas;
        if (usuarioId != null && !usuarioId.isBlank()) {
            alertas = alertaRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId);
        } else if (data != null && !data.isBlank()) {
            LocalDate parsed = LocalDate.parse(data);
            alertas = alertaRepository.findByDataHoraBetweenOrderByDataHoraDesc(parsed.atStartOfDay(), parsed.plusDays(1).atStartOfDay());
        } else if (tipo != null && !tipo.isBlank()) {
            alertas = alertaRepository.findByTipoInfracaoContainingIgnoreCaseOrderByDataHoraDesc(tipo);
        } else if (status != null) {
            alertas = alertaRepository.findByStatusOrderByDataHoraDesc(status);
        } else {
            alertas = alertaRepository.findAllByOrderByDataHoraDesc();
        }
        return alertas.stream().map(this::toAlertaDTO).toList();
    }

    public Map<String, Object> estatisticasAlertas() {
        return Map.of(
                "total", alertaRepository.count(),
                "pendentes", alertaRepository.countByStatus(AlertaModeracaoStatus.PENDENTE),
                "analisados", alertaRepository.countByStatus(AlertaModeracaoStatus.ANALISADO),
                "resolvidos", alertaRepository.countByStatus(AlertaModeracaoStatus.RESOLVIDO)
        );
    }

    @Transactional
    public AlertaModeracaoDTO atualizarAlerta(String alertaId, AlertaModeracaoStatus status) {
        AlertaModeracao alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new RuntimeException("Alerta nao encontrado"));
        alerta.setStatus(status);
        return toAlertaDTO(alertaRepository.save(alerta));
    }

    @Transactional
    public void removerMensagem(String mensagemId) {
        MensagemNegociacao mensagem = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new RuntimeException("Mensagem nao encontrada"));
        mensagem.setConteudo("Mensagem removida pela administracao.");
        mensagem.setRemovida(true);
        mensagemRepository.save(mensagem);
    }

    private MensagemNegociacao criarMensagem(
            ConversaNegociacao conversa,
            Usuario remetente,
            Usuario destinatario,
            String conteudo,
            Double valorProposto,
            TipoMensagemNegociacao tipo
    ) {
        MensagemNegociacao mensagem = new MensagemNegociacao();
        mensagem.setConversa(conversa);
        mensagem.setRemetente(remetente);
        mensagem.setDestinatario(destinatario);
        mensagem.setConteudo(conteudo == null || conteudo.isBlank() ? gerarConteudo(tipo, valorProposto) : conteudo.trim());
        mensagem.setValorProposto(valorProposto);
        mensagem.setTipo(tipo);
        mensagem.setStatusNegociacao(conversa.getStatus());
        return mensagem;
    }

    private String gerarConteudo(TipoMensagemNegociacao tipo, Double valor) {
        if (tipo == TipoMensagemNegociacao.PROPOSTA) return "Cliente enviou uma proposta.";
        if (tipo == TipoMensagemNegociacao.CONTRAPROPOSTA) return "Revendedor enviou uma contraproposta.";
        if (valor != null) return "Valor negociado informado.";
        return "Mensagem enviada.";
    }

    private Usuario obterDestinatario(ConversaNegociacao conversa, String remetenteId) {
        if (conversa.getCliente().getId().equals(remetenteId)) {
            return conversa.getRevendedor();
        }
        if (conversa.getRevendedor().getId().equals(remetenteId)) {
            return conversa.getCliente();
        }
        throw new RuntimeException("Usuario nao participa desta negociacao");
    }

    private void verificarModeracao(MensagemNegociacao mensagem) {
        for (DeteccaoModeracao deteccao : detectar(mensagem.getConteudo())) {
            AlertaModeracao alerta = new AlertaModeracao();
            alerta.setMensagem(mensagem);
            alerta.setUsuario(mensagem.getRemetente());
            alerta.setUsuarioNome(mensagem.getRemetente().getNome());
            alerta.setUsuarioTipo(mensagem.getRemetente().getTipo());
            alerta.setMensagemEnviada(mensagem.getConteudo());
            alerta.setPalavraDetectada(deteccao.palavra());
            alerta.setTipoInfracao(deteccao.tipo());
            alerta.setNivelRisco(deteccao.risco());
            alertaRepository.save(alerta);
        }
    }

    private List<DeteccaoModeracao> detectar(String conteudo) {
        List<DeteccaoModeracao> deteccoes = new ArrayList<>();
        String normalizado = normalizar(conteudo);
        for (RegraModeracao regra : REGRAS) {
            if (normalizado.contains(regra.termo())) {
                deteccoes.add(new DeteccaoModeracao(regra.termo(), regra.tipo(), regra.risco()));
            }
        }
        adicionarRegex(deteccoes, EMAIL, conteudo, "email", NivelRiscoModeracao.ALTO);
        adicionarRegex(deteccoes, URL, conteudo, "url", NivelRiscoModeracao.ALTO);
        adicionarRegex(deteccoes, USUARIO_EXTERNO, conteudo, "usuario externo", NivelRiscoModeracao.MEDIO);
        adicionarRegex(deteccoes, TELEFONE, conteudo, "telefone", NivelRiscoModeracao.ALTO);
        return deteccoes;
    }

    private void adicionarRegex(List<DeteccaoModeracao> deteccoes, Pattern pattern, String conteudo, String tipo, NivelRiscoModeracao risco) {
        Matcher matcher = pattern.matcher(conteudo == null ? "" : conteudo);
        while (matcher.find()) {
            deteccoes.add(new DeteccaoModeracao(matcher.group().trim(), tipo, risco));
        }
    }

    private String normalizar(String texto) {
        String base = texto == null ? "" : texto.toLowerCase(Locale.ROOT);
        return Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private NegociacaoConversaDTO toConversaDTO(ConversaNegociacao conversa, String visualizadorId) {
        NegociacaoConversaDTO dto = new NegociacaoConversaDTO();
        dto.setId(conversa.getId());
        if (conversa.getPedido() != null) {
            dto.setPedidoId(conversa.getPedido().getId());
        }
        dto.setPecaId(conversa.getPeca().getId());
        dto.setPecaNome(conversa.getPeca().getNome());
        dto.setClienteId(conversa.getCliente().getId());
        dto.setClienteNome(conversa.getCliente().getNome());
        dto.setRevendedorId(conversa.getRevendedor().getId());
        dto.setRevendedorNome(Optional.ofNullable(conversa.getRevendedor().getNomeLoja()).orElse(conversa.getRevendedor().getNome()));
        dto.setValorOriginal(conversa.getValorOriginal());
        dto.setValorNegociado(conversa.getValorNegociado());
        dto.setValorFinalAcordado(conversa.getValorFinalAcordado());
        dto.setAprovacaoCliente(conversa.getAprovacaoCliente());
        dto.setAprovacaoRevendedor(conversa.getAprovacaoRevendedor());
        dto.setDataAprovacaoCliente(conversa.getDataAprovacaoCliente());
        dto.setDataAprovacaoRevendedor(conversa.getDataAprovacaoRevendedor());
        dto.setStatus(conversa.getStatus());
        dto.setCriadaEm(conversa.getCriadaEm());
        dto.setAtualizadaEm(conversa.getAtualizadaEm());
        if (visualizadorId != null) {
            dto.setNaoLidas(mensagemRepository.countByConversaIdAndDestinatarioIdAndLidaFalseAndRemovidaFalse(conversa.getId(), visualizadorId));
        }
        dto.setMensagens(mensagemRepository.findByConversaIdOrderByDataEnvioAsc(conversa.getId()).stream()
                .map(this::toMensagemDTO)
                .toList());
        return dto;
    }

    private NegociacaoMensagemDTO toMensagemDTO(MensagemNegociacao mensagem) {
        NegociacaoMensagemDTO dto = new NegociacaoMensagemDTO();
        dto.setId(mensagem.getId());
        dto.setConversaId(mensagem.getConversa().getId());
        dto.setRemetenteId(mensagem.getRemetente().getId());
        dto.setRemetenteNome(mensagem.getRemetente().getNome());
        dto.setDestinatarioId(mensagem.getDestinatario().getId());
        dto.setDestinatarioNome(mensagem.getDestinatario().getNome());
        dto.setConteudo(mensagem.getConteudo());
        dto.setDataEnvio(mensagem.getDataEnvio());
        dto.setLida(mensagem.getLida());
        dto.setStatusNegociacao(mensagem.getStatusNegociacao());
        dto.setTipo(mensagem.getTipo());
        dto.setValorProposto(mensagem.getValorProposto());
        dto.setRemovida(mensagem.getRemovida());
        return dto;
    }

    private AlertaModeracaoDTO toAlertaDTO(AlertaModeracao alerta) {
        AlertaModeracaoDTO dto = new AlertaModeracaoDTO();
        dto.setId(alerta.getId());
        if (alerta.getMensagem() != null) {
            dto.setConversaId(alerta.getMensagem().getConversa().getId());
            dto.setMensagemId(alerta.getMensagem().getId());
        }
        if (alerta.getPeca() != null) {
            dto.setPecaId(alerta.getPeca().getId());
        }
        dto.setPecaNome(alerta.getPecaNome());
        dto.setImagemUrl(alerta.getImagemUrl());
        dto.setUsuarioId(alerta.getUsuario().getId());
        dto.setUsuarioNome(alerta.getUsuarioNome());
        dto.setUsuarioTipo(alerta.getUsuarioTipo());
        dto.setMensagemEnviada(alerta.getMensagemEnviada());
        dto.setDataHora(alerta.getDataHora());
        dto.setPalavraDetectada(alerta.getPalavraDetectada());
        dto.setTipoInfracao(alerta.getTipoInfracao());
        dto.setNivelRisco(alerta.getNivelRisco());
        dto.setStatus(alerta.getStatus());
        dto.setContadorInfracoesUsuario(alertaRepository.countByUsuarioId(alerta.getUsuario().getId()));
        return dto;
    }

    private record RegraModeracao(String termo, String tipo, NivelRiscoModeracao risco) {}
    private record DeteccaoModeracao(String palavra, String tipo, NivelRiscoModeracao risco) {}
}
