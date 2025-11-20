package com.metalSpring.services;

import com.metalSpring.model.entity.Avaliacao;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.model.entity.Peca;
import com.metalSpring.repository.AvaliacaoRepository;
import com.metalSpring.repository.UsuarioRepository;
import com.metalSpring.repository.RevendedorRepository;
import com.metalSpring.repository.PecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RevendedorRepository revendedorRepository;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private RevendedorService revendedorService;

    public List<Avaliacao> listarTodas() {
        return avaliacaoRepository.findAll();
    }

    public Optional<Avaliacao> buscarPorId(String id) {
        return avaliacaoRepository.findById(id);
    }

    public List<Avaliacao> buscarPorRevendedor(String revendedorId) {
        return avaliacaoRepository.findByVendedorId(revendedorId);
    }

    public List<Avaliacao> buscarPorCliente(String clienteId) {
        return avaliacaoRepository.findByClienteId(clienteId);
    }

    public List<Avaliacao> buscarPorPeca(String pecaId) {
        return avaliacaoRepository.findByPecaId(pecaId);
    }

    public List<Avaliacao> buscarPorNota(int nota) {
        return avaliacaoRepository.findByNota(nota);
    }

    public List<Avaliacao> buscarPorNotaMinima(int notaMinima) {
        return avaliacaoRepository.findByNotaGreaterThanEqual(notaMinima);
    }

    @Transactional
    public Avaliacao criar(String clienteId, String revendedorId, String pecaId, int nota, String comentario) {
        Optional<Usuario> cliente = usuarioRepository.findById(clienteId);
        Optional<Revendedor> revendedor = revendedorRepository.findById(revendedorId);
        Optional<Peca> peca = pecaRepository.findById(pecaId);

        if (cliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado");
        }

        if (revendedor.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        if (peca.isEmpty()) {
            throw new RuntimeException("Peça não encontrada");
        }

        if (nota < 1 || nota > 5) {
            throw new RuntimeException("Nota deve estar entre 1 e 5");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setCliente(cliente.get());
        avaliacao.setVendedor(revendedor.get());
        avaliacao.setPeca(peca.get());
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentario);
        avaliacao.setData(LocalDateTime.now());

        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);

        revendedorService.calcularAvaliacaoMedia(revendedorId);

        return avaliacaoSalva;
    }

    @Transactional
    public Avaliacao editarAvaliacao(String id, int novaNota, String novoComentario) {
        Optional<Avaliacao> avaliacaoOpt = avaliacaoRepository.findById(id);

        if (avaliacaoOpt.isEmpty()) {
            throw new RuntimeException("Avaliação não encontrada");
        }

        if (novaNota < 1 || novaNota > 5) {
            throw new RuntimeException("Nota deve estar entre 1 e 5");
        }

        Avaliacao avaliacao = avaliacaoOpt.get();
        avaliacao.editarAvaliacao(novaNota, novoComentario);

        Avaliacao avaliacaoAtualizada = avaliacaoRepository.save(avaliacao);

        revendedorService.calcularAvaliacaoMedia(avaliacao.getVendedor().getId());

        return avaliacaoAtualizada;
    }

    @Transactional
    public void excluir(String id) {
        Optional<Avaliacao> avaliacaoOpt = avaliacaoRepository.findById(id);

        if (avaliacaoOpt.isEmpty()) {
            throw new RuntimeException("Avaliação não encontrada");
        }

        Avaliacao avaliacao = avaliacaoOpt.get();
        String revendedorId = avaliacao.getVendedor().getId();

        avaliacaoRepository.deleteById(id);

        revendedorService.calcularAvaliacaoMedia(revendedorId);
    }

    public double calcularMediaPorRevendedor(String revendedorId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByVendedorId(revendedorId);

        if (avaliacoes.isEmpty()) {
            return 0.0;
        }

        double soma = avaliacoes.stream()
                .mapToDouble(Avaliacao::getNota)
                .sum();

        return soma / avaliacoes.size();
    }

    public double calcularMediaPorPeca(String pecaId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByPecaId(pecaId);

        if (avaliacoes.isEmpty()) {
            return 0.0;
        }

        double soma = avaliacoes.stream()
                .mapToDouble(Avaliacao::getNota)
                .sum();

        return soma / avaliacoes.size();
    }
}