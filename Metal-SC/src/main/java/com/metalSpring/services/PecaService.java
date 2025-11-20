package com.metalSpring.services;

import com.metalSpring.model.entity.Peca;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.repository.PecaRepository;
import com.metalSpring.repository.RevendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PecaService {

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private RevendedorRepository revendedorRepository;

    public List<Peca> listarTodas() {
        return pecaRepository.findAll();
    }

    public Optional<Peca> buscarPorId(String id) {
        return pecaRepository.findById(id);
    }

    public List<Peca> buscarPorNome(String nome) {
        return pecaRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Peca> buscarPorMarca(String marca) {
        return pecaRepository.findByMarcaIgnoreCase(marca);
    }

    public List<Peca> buscarPorCategoria(String categoria) {
        return pecaRepository.findByCategoriaIgnoreCase(categoria);
    }

    public List<Peca> buscarPorRevendedor(String revendedorId) {
        return pecaRepository.findByVendedorId(revendedorId);
    }

    public List<Peca> buscarDisponiveis() {
        return pecaRepository.findByEstoqueGreaterThan(0);
    }

    @Transactional
    public Peca salvar(Peca peca) {
        return pecaRepository.save(peca);
    }

    @Transactional
    public Peca criar(Peca peca, String revendedorId) {
        Optional<Revendedor> revendedor = revendedorRepository.findById(revendedorId);
        if (revendedor.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        peca.setVendedor(revendedor.get());
        Peca pecaSalva = pecaRepository.save(peca);

        // Adiciona a peça à lista do revendedor
        revendedor.get().adicionarPeca(pecaSalva);
        revendedorRepository.save(revendedor.get());

        return pecaSalva;
    }

    @Transactional
    public Peca atualizar(String id, Peca pecaAtualizada) {
        Optional<Peca> pecaExistente = pecaRepository.findById(id);

        if (pecaExistente.isEmpty()) {
            throw new RuntimeException("Peça não encontrada");
        }

        Peca peca = pecaExistente.get();
        peca.setNome(pecaAtualizada.getNome());
        peca.setDescricao(pecaAtualizada.getDescricao());
        peca.setCategoria(pecaAtualizada.getCategoria());
        peca.setPreco(pecaAtualizada.getPreco());
        peca.setEstado(pecaAtualizada.getEstado());
        peca.setAno(pecaAtualizada.getAno());
        peca.setMarca(pecaAtualizada.getMarca());
        peca.setModeloVeiculo(pecaAtualizada.getModeloVeiculo());

        return pecaRepository.save(peca);
    }

    @Transactional
    public void alterarEstoque(String id, int quantidade) {
        Optional<Peca> peca = pecaRepository.findById(id);

        if (peca.isEmpty()) {
            throw new RuntimeException("Peça não encontrada");
        }

        peca.get().alterarEstoque(quantidade);
        pecaRepository.save(peca.get());
    }

    @Transactional
    public void adicionarImagem(String id, String urlImagem) {
        Optional<Peca> peca = pecaRepository.findById(id);

        if (peca.isEmpty()) {
            throw new RuntimeException("Peça não encontrada");
        }

        peca.get().adicionarImagem(urlImagem);
        pecaRepository.save(peca.get());
    }

    @Transactional
    public void removerImagem(String id, String urlImagem) {
        Optional<Peca> peca = pecaRepository.findById(id);

        if (peca.isEmpty()) {
            throw new RuntimeException("Peça não encontrada");
        }

        peca.get().removerImagem(urlImagem);
        pecaRepository.save(peca.get());
    }

    @Transactional
    public void deletar(String id) {
        if (!pecaRepository.existsById(id)) {
            throw new RuntimeException("Peça não encontrada");
        }
        pecaRepository.deleteById(id);
    }

    public boolean estaDisponivel(String id) {
        Optional<Peca> peca = pecaRepository.findById(id);
        return peca.isPresent() && peca.get().estaDisponivel();
    }

    public List<Peca> listarDisponiveis() {
        return List.of();
    }

    public Peca criar(Peca peca) {
        return peca;
    }
}