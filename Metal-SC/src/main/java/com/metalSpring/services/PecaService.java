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

    // ==================== MÉTODOS DE CONSULTA ====================

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

    public List<Peca> listarDisponiveis() {
        return buscarDisponiveis();
    }

    public boolean estaDisponivel(String id) {
        Optional<Peca> peca = pecaRepository.findById(id);
        return peca.isPresent() && peca.get().estaDisponivel();
    }

    // ==================== MÉTODOS DE MODIFICAÇÃO ====================

    @Transactional
    public Peca salvar(Peca peca) {
        return pecaRepository.save(peca);
    }

    /**
     * ✅ MÉTODO PRINCIPAL PARA CRIAR PEÇAS
     * Este é o ÚNICO método criar() - remove qualquer outro
     *
     * @param peca - Entidade Peca com os dados
     * @param revendedorId - ID do revendedor que está anunciando
     * @return Peca criada e salva no banco
     * @throws RuntimeException se o revendedor não for encontrado
     */
    @Transactional
    public Peca criar(Peca peca, String revendedorId) {
        System.out.println("🔍 [PecaService] Buscando revendedor com ID: " + revendedorId);

        // Busca o revendedor no banco
        Optional<Revendedor> revendedorOpt = revendedorRepository.findById(revendedorId);

        if (revendedorOpt.isEmpty()) {
            System.err.println("❌ [PecaService] Revendedor não encontrado: " + revendedorId);
            throw new RuntimeException("Revendedor não encontrado com ID: " + revendedorId);
        }

        Revendedor revendedor = revendedorOpt.get();
        System.out.println("✅ [PecaService] Revendedor encontrado: " + revendedor.getNome());

        // Associa o revendedor à peça
        peca.setVendedor(revendedor);

        // Salva a peça no banco
        System.out.println("💾 [PecaService] Salvando peça: " + peca.getNome());
        Peca pecaSalva = pecaRepository.save(peca);
        System.out.println("✅ [PecaService] Peça salva com ID: " + pecaSalva.getId());

        // Adiciona a peça à lista do revendedor
        revendedor.adicionarPeca(pecaSalva);
        revendedorRepository.save(revendedor);
        System.out.println("✅ [PecaService] Peça associada ao revendedor");

        return pecaSalva;
    }

    @Transactional
    public Peca atualizar(String id, Peca pecaAtualizada) {
        System.out.println("🔄 [PecaService] Atualizando peça com ID: " + id);

        Optional<Peca> pecaExistente = pecaRepository.findById(id);

        if (pecaExistente.isEmpty()) {
            System.err.println("❌ [PecaService] Peça não encontrada: " + id);
            throw new RuntimeException("Peça não encontrada com ID: " + id);
        }

        Peca peca = pecaExistente.get();

        // Atualiza os campos
        if (pecaAtualizada.getNome() != null) {
            peca.setNome(pecaAtualizada.getNome());
        }
        if (pecaAtualizada.getDescricao() != null) {
            peca.setDescricao(pecaAtualizada.getDescricao());
        }
        if (pecaAtualizada.getCategoria() != null) {
            peca.setCategoria(pecaAtualizada.getCategoria());
        }
        if (pecaAtualizada.getPreco() != null) {
            peca.setPreco(pecaAtualizada.getPreco());
        }
        if (pecaAtualizada.getEstado() != null) {
            peca.setEstado(pecaAtualizada.getEstado());
        }
        if (pecaAtualizada.getAno() != null) {
            peca.setAno(pecaAtualizada.getAno());
        }
        if (pecaAtualizada.getMarca() != null) {
            peca.setMarca(pecaAtualizada.getMarca());
        }
        if (pecaAtualizada.getModeloVeiculo() != null) {
            peca.setModeloVeiculo(pecaAtualizada.getModeloVeiculo());
        }
        if (pecaAtualizada.getEstoque() != null) {
            peca.setEstoque(pecaAtualizada.getEstoque());
        }

        Peca pecaSalva = pecaRepository.save(peca);
        System.out.println("✅ [PecaService] Peça atualizada com sucesso");

        return pecaSalva;
    }

    @Transactional
    public void alterarEstoque(String id, int quantidade) {
        System.out.println("📦 [PecaService] Alterando estoque da peça: " + id);

        Optional<Peca> pecaOpt = pecaRepository.findById(id);

        if (pecaOpt.isEmpty()) {
            System.err.println("❌ [PecaService] Peça não encontrada: " + id);
            throw new RuntimeException("Peça não encontrada com ID: " + id);
        }

        Peca peca = pecaOpt.get();
        peca.alterarEstoque(quantidade);
        pecaRepository.save(peca);

        System.out.println("✅ [PecaService] Estoque atualizado: " + peca.getEstoque());
    }

    @Transactional
    public void adicionarImagem(String id, String urlImagem) {
        System.out.println("🖼️ [PecaService] Adicionando imagem à peça: " + id);

        Optional<Peca> pecaOpt = pecaRepository.findById(id);

        if (pecaOpt.isEmpty()) {
            System.err.println("❌ [PecaService] Peça não encontrada: " + id);
            throw new RuntimeException("Peça não encontrada com ID: " + id);
        }

        Peca peca = pecaOpt.get();
        peca.adicionarImagem(urlImagem);
        pecaRepository.save(peca);

        System.out.println("✅ [PecaService] Imagem adicionada com sucesso");
    }

    @Transactional
    public void removerImagem(String id, String urlImagem) {
        System.out.println("🗑️ [PecaService] Removendo imagem da peça: " + id);

        Optional<Peca> pecaOpt = pecaRepository.findById(id);

        if (pecaOpt.isEmpty()) {
            System.err.println("❌ [PecaService] Peça não encontrada: " + id);
            throw new RuntimeException("Peça não encontrada com ID: " + id);
        }

        Peca peca = pecaOpt.get();
        peca.removerImagem(urlImagem);
        pecaRepository.save(peca);

        System.out.println("✅ [PecaService] Imagem removida com sucesso");
    }

    @Transactional
    public void deletar(String id) {
        System.out.println("🗑️ [PecaService] Deletando peça: " + id);

        if (!pecaRepository.existsById(id)) {
            System.err.println("❌ [PecaService] Peça não encontrada: " + id);
            throw new RuntimeException("Peça não encontrada com ID: " + id);
        }

        pecaRepository.deleteById(id);
        System.out.println("✅ [PecaService] Peça deletada com sucesso");
    }
}