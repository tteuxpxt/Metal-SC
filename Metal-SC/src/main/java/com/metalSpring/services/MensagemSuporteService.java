package com.metalSpring.services;

import com.metalSpring.model.entity.MensagemSuporte;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.model.enums.CanalSuporte;
import com.metalSpring.repository.MensagemSuporteRepository;
import com.metalSpring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MensagemSuporteService {

    @Autowired
    private MensagemSuporteRepository mensagemSuporteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<MensagemSuporte> listarTodas() {
        return mensagemSuporteRepository.findAll();
    }

    public Optional<MensagemSuporte> buscarPorId(String id) {
        return mensagemSuporteRepository.findById(id);
    }

    public List<MensagemSuporte> buscarPorUsuario(String usuarioId) {
        return mensagemSuporteRepository.findByUsuarioId(usuarioId);
    }

    public List<MensagemSuporte> buscarNaoLidas() {
        return mensagemSuporteRepository.findByLida(false);
    }

    public List<MensagemSuporte> buscarPorCanal(CanalSuporte canal) {
        return mensagemSuporteRepository.findByCanal(canal);
    }

    public List<MensagemSuporte> buscarPorAtendente(String atendenteId) {
        return mensagemSuporteRepository.findByAtendenteResponsavel(atendenteId);
    }

    @Transactional
    public MensagemSuporte criar(String usuarioId, String conteudo, CanalSuporte canal) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        MensagemSuporte mensagem = new MensagemSuporte();
        mensagem.setUsuario(usuario);
        mensagem.setConteudo(conteudo);
        mensagem.setCanal(canal);
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagem.setLida(false);

        return mensagemSuporteRepository.save(mensagem);
    }

    @Transactional
    public void marcarComoLida(String id) {
        Optional<MensagemSuporte> mensagemOpt = mensagemSuporteRepository.findById(id);

        if (mensagemOpt.isEmpty()) {
            throw new RuntimeException("Mensagem não encontrada");
        }

        MensagemSuporte mensagem = mensagemOpt.get();
        mensagem.marcarComoLida();

        mensagemSuporteRepository.save(mensagem);
    }

    @Transactional
    public void marcarComoNaoLida(String id) {
        Optional<MensagemSuporte> mensagemOpt = mensagemSuporteRepository.findById(id);

        if (mensagemOpt.isEmpty()) {
            throw new RuntimeException("Mensagem não encontrada");
        }

        MensagemSuporte mensagem = mensagemOpt.get();
        mensagem.setLida(false);

        mensagemSuporteRepository.save(mensagem);
    }

    @Transactional
    public MensagemSuporte responder(String id, String texto, String idAtendente) {
        Optional<MensagemSuporte> mensagemOpt = mensagemSuporteRepository.findById(id);

        if (mensagemOpt.isEmpty()) {
            throw new RuntimeException("Mensagem não encontrada");
        }

        MensagemSuporte mensagem = mensagemOpt.get();
        mensagem.responder(texto, idAtendente);

        return mensagemSuporteRepository.save(mensagem);
    }

    @Transactional
    public MensagemSuporte encaminharParaAtendente(String id, String idAtendente) {
        Optional<MensagemSuporte> mensagemOpt = mensagemSuporteRepository.findById(id);

        if (mensagemOpt.isEmpty()) {
            throw new RuntimeException("Mensagem não encontrada");
        }

        MensagemSuporte mensagem = mensagemOpt.get();
        mensagem.encaminharParaAtendente(idAtendente);

        return mensagemSuporteRepository.save(mensagem);
    }

    @Transactional
    public void deletar(String id) {
        if (!mensagemSuporteRepository.existsById(id)) {
            throw new RuntimeException("Mensagem não encontrada");
        }

        mensagemSuporteRepository.deleteById(id);
    }

    public long contarNaoLidas() {
        return mensagemSuporteRepository.countByLida(false);
    }

    public List<MensagemSuporte> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return mensagemSuporteRepository.findByDataEnvioBetween(inicio, fim);
    }

    public List<MensagemSuporte> buscarRecentes(int limite) {
        return mensagemSuporteRepository.findTop10ByOrderByDataEnvioDesc();
    }

    public Map<String, Object> obterEstatisticas() {
        long total = mensagemSuporteRepository.count();
        long naoLidas = mensagemSuporteRepository.countByLida(false);
        long resolvidas = mensagemSuporteRepository.countByLida(true);
        long emAndamento = mensagemSuporteRepository.findMensagensComAtendente().stream()
                .filter(mensagem -> Boolean.FALSE.equals(mensagem.getLida()))
                .count();

        return Map.of(
                "total", total,
                "naoLidas", naoLidas,
                "emAndamento", emAndamento,
                "resolvidas", resolvidas
        );
    }
}
