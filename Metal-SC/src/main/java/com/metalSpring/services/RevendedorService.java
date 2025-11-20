package com.metalSpring.services;

import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.model.entity.Peca;
import com.metalSpring.repository.RevendedorRepository;
import com.metalSpring.repository.PecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RevendedorService {

    @Autowired
    private RevendedorRepository revendedorRepository;

    @Autowired
    private PecaRepository pecaRepository;

    public List<Revendedor> listarTodos() {
        return revendedorRepository.findAll();
    }

    public Optional<Revendedor> buscarPorId(String id) {
        return revendedorRepository.findById(id);
    }

    public Optional<Revendedor> buscarPorCnpj(String cnpj) {
        return revendedorRepository.findByCnpj(cnpj);
    }

    public List<Revendedor> buscarPorNomeLoja(String nomeLoja) {
        return revendedorRepository.findByNomeLojaContainingIgnoreCase(nomeLoja);
    }

    public List<Revendedor> buscarPorAvaliacaoMinima(double avaliacao) {
        return revendedorRepository.findByAvaliacaoMediaGreaterThanEqual(avaliacao);
    }

    @Transactional
    public Revendedor criar(Revendedor revendedor) {
        if (revendedorRepository.findByCnpj(revendedor.getCnpj()).isPresent()) {
            throw new RuntimeException("CNPJ já cadastrado");
        }

        revendedor.setAvaliacaoMedia(0.0);
        return revendedorRepository.save(revendedor);
    }

    @Transactional
    public Revendedor atualizar(String id, Revendedor revendedorAtualizado) {
        Optional<Revendedor> revendedorExistente = revendedorRepository.findById(id);

        if (revendedorExistente.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        Revendedor revendedor = revendedorExistente.get();
        revendedor.setNomeLoja(revendedorAtualizado.getNomeLoja());
        revendedor.setNome(revendedorAtualizado.getNome());
        revendedor.setTelefone(revendedorAtualizado.getTelefone());
        revendedor.setEndereco(revendedorAtualizado.getEndereco());

        return revendedorRepository.save(revendedor);
    }

    @Transactional
    public void adicionarPeca(String revendedorId, Peca peca) {
        Optional<Revendedor> revendedorOpt = revendedorRepository.findById(revendedorId);

        if (revendedorOpt.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        Revendedor revendedor = revendedorOpt.get();
        peca.setVendedor(revendedor);

        Peca pecaSalva = pecaRepository.save(peca);
        revendedor.adicionarPeca(pecaSalva);

        revendedorRepository.save(revendedor);
    }

    @Transactional
    public void removerPeca(String revendedorId, String pecaId) {
        Optional<Revendedor> revendedorOpt = revendedorRepository.findById(revendedorId);
        Optional<Peca> pecaOpt = pecaRepository.findById(pecaId);

        if (revendedorOpt.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        if (pecaOpt.isEmpty()) {
            throw new RuntimeException("Peça não encontrada");
        }

        Revendedor revendedor = revendedorOpt.get();
        revendedor.removerPeca(pecaId);

        pecaRepository.deleteById(pecaId);
        revendedorRepository.save(revendedor);
    }

    @Transactional
    public void atualizarEstoque(String revendedorId, String pecaId, int quantidade) {
        Optional<Revendedor> revendedorOpt = revendedorRepository.findById(revendedorId);
        Optional<Peca> pecaOpt = pecaRepository.findById(pecaId);

        if (revendedorOpt.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        if (pecaOpt.isEmpty()) {
            throw new RuntimeException("Peça não encontrada");
        }

        Revendedor revendedor = revendedorOpt.get();
        revendedor.atualizarEstoque(pecaId, quantidade);

        Peca peca = pecaOpt.get();
        peca.setEstoque(quantidade);

        pecaRepository.save(peca);
        revendedorRepository.save(revendedor);
    }

    @Transactional
    public double calcularAvaliacaoMedia(String revendedorId) {
        Optional<Revendedor> revendedorOpt = revendedorRepository.findById(revendedorId);

        if (revendedorOpt.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        Revendedor revendedor = revendedorOpt.get();
        double avaliacaoMedia = revendedor.calcularAvaliacaoMedia();

        revendedor.setAvaliacaoMedia(avaliacaoMedia);
        revendedorRepository.save(revendedor);

        return avaliacaoMedia;
    }

    public List<Peca> listarPecas(String revendedorId) {
        Optional<Revendedor> revendedor = revendedorRepository.findById(revendedorId);

        if (revendedor.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        return revendedor.get().getPecas();
    }

    @Transactional
    public void deletar(String id) {
        if (!revendedorRepository.existsById(id)) {
            throw new RuntimeException("Revendedor não encontrado");
        }
        revendedorRepository.deleteById(id);
    }

    public boolean cnpjExiste(String cnpj) {
        return revendedorRepository.findByCnpj(cnpj).isPresent();
    }
}
