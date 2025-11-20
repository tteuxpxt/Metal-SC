package com.metalSpring.services;

import com.metalSpring.model.entity.Transacao;
import com.metalSpring.model.entity.Pedido;
import com.metalSpring.model.enums.MetodoPagamento;
import com.metalSpring.model.enums.PedidoStatus;
import com.metalSpring.model.enums.TransacaoStatus;
import com.metalSpring.repository.TransacaoRepository;
import com.metalSpring.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoService pedidoService;

    // ========== CONSULTAS ==========

    public List<Transacao> listarTodas() {
        return transacaoRepository.findAll();
    }

    public Optional<Transacao> buscarPorId(String id) {
        return transacaoRepository.findById(id);
    }

    public Optional<Transacao> buscarPorPedido(String pedidoId) {
        return transacaoRepository.findByPedidoId(pedidoId);
    }

    public List<Transacao> buscarPorData(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return transacaoRepository.findByDataBetween(dataInicio, dataFim);
    }

    public List<Transacao> buscarPorStatus(TransacaoStatus status) {
        return transacaoRepository.findByStatus(status);
    }

    public List<Transacao> buscarPorMetodo(MetodoPagamento metodo) {
        return transacaoRepository.findByMetodo(metodo);
    }

    // ========== CRIAÇÃO E PROCESSAMENTO ==========

    @Transactional
    public Transacao criarTransacao(String pedidoId, MetodoPagamento metodo, String referencia) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

        if (pedido.getStatus() != PedidoStatus.PENDENTE) {
            throw new RuntimeException("Pedido já foi processado");
        }

        Transacao transacao = new Transacao();
        transacao.setId(UUID.randomUUID().toString());
        transacao.setPedido(pedido);
        transacao.setData(LocalDateTime.now());
        transacao.setMetodo(metodo);
        transacao.setStatus(TransacaoStatus.PENDENTE);
        transacao.setReferencia(referencia);

        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao processarPagamento(String transacaoId) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + transacaoId));

        if (transacao.getStatus() != TransacaoStatus.PENDENTE) {
            throw new RuntimeException("Transação já foi processada");
        }

        transacao.setStatus(TransacaoStatus.PROCESSANDO);
        transacaoRepository.save(transacao);

        // Aqui você integraria com gateway de pagamento
        // Simulando processamento bem-sucedido
        try {
            // Lógica de integração com pagamento
            boolean pagamentoAprovado = simularProcessamentoPagamento(transacao);

            if (pagamentoAprovado) {
                return confirmar(transacaoId);
            } else {
                return recusar(transacaoId, "Pagamento recusado pela operadora");
            }
        } catch (Exception e) {
            transacao.setStatus(TransacaoStatus.RECUSADA);
            transacaoRepository.save(transacao);
            throw new RuntimeException("Erro ao processar pagamento: " + e.getMessage());
        }
    }

    private boolean simularProcessamentoPagamento(Transacao transacao) {
        // Simula 90% de aprovação
        return Math.random() > 0.1;
    }

    // ========== CONFIRMAÇÃO E RECUSA ==========

    @Transactional
    public Transacao confirmar(String transacaoId) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + transacaoId));

        transacao.setStatus(TransacaoStatus.CONFIRMADA);
        transacaoRepository.save(transacao);

        // Atualiza status do pedido
        pedidoService.confirmarPagamento(transacao.getPedido().getId());

        return transacao;
    }

    @Transactional
    public Transacao recusar(String transacaoId, String motivo) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + transacaoId));

        transacao.setStatus(TransacaoStatus.RECUSADA);
        transacaoRepository.save(transacao);

        // Você pode adicionar um campo 'motivo' na entidade Transacao
        // transacao.setMotivoRecusa(motivo);

        return transacao;
    }

    // ========== ESTORNO ==========

    @Transactional
    public Transacao estornar(String transacaoId) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + transacaoId));

        if (transacao.getStatus() != TransacaoStatus.CONFIRMADA) {
            throw new RuntimeException("Apenas transações confirmadas podem ser estornadas");
        }

        // Verifica se o pedido pode ser estornado (ex: dentro de 30 dias)
        if (!podeSerEstornada(transacao)) {
            throw new RuntimeException("Transação fora do prazo de estorno");
        }

        transacao.setStatus(TransacaoStatus.ESTORNADA);
        transacaoRepository.save(transacao);

        // Cancela o pedido associado
        pedidoService.cancelarPedido(transacao.getPedido().getId());

        return transacao;
    }

    // ========== VALIDAÇÕES ==========

    public boolean podeSerEstornada(Transacao transacao) {
        if (transacao.getStatus() != TransacaoStatus.CONFIRMADA) {
            return false;
        }

        // Verifica se está dentro de 30 dias
        LocalDateTime prazoLimite = transacao.getData().plusDays(30);
        return LocalDateTime.now().isBefore(prazoLimite);
    }

    public boolean estaConfirmada(String transacaoId) {
        return transacaoRepository.findById(transacaoId)
                .map(t -> t.getStatus() == TransacaoStatus.CONFIRMADA)
                .orElse(false);
    }

    public boolean estaPendente(String transacaoId) {
        return transacaoRepository.findById(transacaoId)
                .map(t -> t.getStatus() == TransacaoStatus.PENDENTE)
                .orElse(false);
    }

    // ========== CANCELAMENTO ==========

    @Transactional
    public Transacao cancelar(String transacaoId) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada: " + transacaoId));

        if (transacao.getStatus() == TransacaoStatus.CONFIRMADA) {
            throw new RuntimeException("Use estornar() para transações confirmadas");
        }

        transacao.setStatus(TransacaoStatus.RECUSADA);
        return transacaoRepository.save(transacao);
    }

    // ========== RELATÓRIOS ==========

    public Double calcularTotalPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Transacao> transacoes = buscarPorData(dataInicio, dataFim);
        return transacoes.stream()
                .filter(t -> t.getStatus() == TransacaoStatus.CONFIRMADA)
                .mapToDouble(t -> t.getPedido().getValorTotal())
                .sum();
    }

    public Long contarPorStatus(TransacaoStatus status) {
        return transacaoRepository.findByStatus(status).stream().count();
    }
}