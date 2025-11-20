package com.metalSpring.services;

import com.metalSpring.model.entity.Administrador;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.model.entity.Peca;
import com.metalSpring.repository.AdministradorRepository;
import com.metalSpring.repository.UsuarioRepository;
import com.metalSpring.repository.RevendedorRepository;
import com.metalSpring.repository.PecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RevendedorRepository revendedorRepository;

    @Autowired
    private PecaRepository pecaRepository;

    public Optional<Administrador> buscarPorId(String id) {
        return administradorRepository.findById(id);
    }

    @Transactional
    public void bloquearUsuario(String usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        // Implementar lógica de bloqueio
        // Por exemplo: adicionar campo 'bloqueado' na entidade Usuario
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desbloquearUsuario(String usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        // Implementar lógica de desbloqueio
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void removerPeca(String pecaId) {
        if (!pecaRepository.existsById(pecaId)) {
            throw new RuntimeException("Peça não encontrada");
        }

        pecaRepository.deleteById(pecaId);
    }

    @Transactional
    public void aprovarRevendedor(String revendedorId) {
        Optional<Revendedor> revendedorOpt = revendedorRepository.findById(revendedorId);

        if (revendedorOpt.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        Revendedor revendedor = revendedorOpt.get();
        // Implementar lógica de aprovação
        // Por exemplo: adicionar campo 'aprovado' na entidade Revendedor
        revendedorRepository.save(revendedor);
    }

    @Transactional
    public void reprovarRevendedor(String revendedorId) {
        Optional<Revendedor> revendedorOpt = revendedorRepository.findById(revendedorId);

        if (revendedorOpt.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        Revendedor revendedor = revendedorOpt.get();
        // Implementar lógica de reprovação
        revendedorRepository.save(revendedor);
    }

    public Map<String, Object> visualizarRelatorios() {
        Map<String, Object> relatorios = new HashMap<>();

        // Relatório de usuários
        long totalUsuarios = usuarioRepository.count();
        relatorios.put("totalUsuarios", totalUsuarios);

        // Relatório de revendedores
        long totalRevendedores = revendedorRepository.count();
        relatorios.put("totalRevendedores", totalRevendedores);

        // Relatório de peças
        long totalPecas = pecaRepository.count();
        relatorios.put("totalPecas", totalPecas);

        // Peças disponíveis
        long pecasDisponiveis = pecaRepository.findByEstoqueGreaterThan(0).size();
        relatorios.put("pecasDisponiveis", pecasDisponiveis);

        // Valor total do estoque
        double valorTotalEstoque = pecaRepository.findAll().stream()
                .mapToDouble(p -> p.getPreco() * p.getEstoque())
                .sum();
        relatorios.put("valorTotalEstoque", valorTotalEstoque);

        return relatorios;
    }

    public Map<String, Object> relatorioDetalhado() {
        Map<String, Object> relatorio = visualizarRelatorios();

        // Adicionar estatísticas adicionais
        // Peças por categoria
        Map<String, Long> pecasPorCategoria = new HashMap<>();
        pecaRepository.findAll().forEach(peca -> {
            pecasPorCategoria.merge(peca.getCategoria(), 1L, Long::sum);
        });
        relatorio.put("pecasPorCategoria", pecasPorCategoria);

        // Peças por marca
        Map<String, Long> pecasPorMarca = new HashMap<>();
        pecaRepository.findAll().forEach(peca -> {
            pecasPorMarca.merge(peca.getMarca(), 1L, Long::sum);
        });
        relatorio.put("pecasPorMarca", pecasPorMarca);

        // Revendedores mais bem avaliados
        relatorio.put("topRevendedores",
                revendedorRepository.findTop10ByOrderByAvaliacaoMediaDesc());

        return relatorio;
    }

    @Transactional
    public void removerUsuario(String usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuário não encontrado");
        }

        usuarioRepository.deleteById(usuarioId);
    }

    @Transactional
    public void removerRevendedor(String revendedorId) {
        if (!revendedorRepository.existsById(revendedorId)) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        revendedorRepository.deleteById(revendedorId);
    }
}