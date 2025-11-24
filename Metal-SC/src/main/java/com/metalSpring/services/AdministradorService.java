package com.metalSpring.services;

import com.metalSpring.model.entity.Administrador;
import com.metalSpring.model.entity.Usuario; // Supondo que exista
import com.metalSpring.model.entity.Peca;    // Supondo que exista
import com.metalSpring.repository.AdministradorRepository;
import com.metalSpring.repository.PecaRepository;
import com.metalSpring.repository.PedidoRepository;
import com.metalSpring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository adminRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PecaRepository pecaRepository;
    @Autowired
    private PedidoRepository pedidoRepository;

    // --- CRUD BÁSICO ---
    public List<Administrador> listarTodos() {
        return adminRepository.findAll();
    }

    public Administrador criar(Administrador admin) {
        return adminRepository.save(admin);
    }

    public Administrador buscarPorId(String id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));
    }

    // --- AÇÕES ADMINISTRATIVAS ---

    public void bloquearUsuario(String usuarioId, String motivo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Supondo que Usuario tenha um campo boolean 'ativo' ou Enum status
        usuario.setAtivo(false);
        // usuario.setObservacaoBloqueio(motivo); // Opcional
        usuarioRepository.save(usuario);
    }

    public void desbloquearUsuario(String usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    public void removerPeca(String pecaId, String motivo) {
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RuntimeException("Peça não encontrada"));

        // Geralmente não deletamos fisicamente para manter histórico, apenas inativamos
        // pecaRepository.delete(peca); 
        peca.setDisponivel(false); // Ou algo similar
        pecaRepository.save(peca);
    }

    public void aprovarRevendedor(String revendedorId) {
        Usuario revendedor = usuarioRepository.findById(revendedorId)
                .orElseThrow(() -> new RuntimeException("Revendedor não encontrado"));

        // Lógica de aprovação
        // revendedor.setStatusaprovacao("APROVADO");
        usuarioRepository.save(revendedor);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public void rejeitarRevendedor(String revendedorId, String motivo) {
        Usuario revendedor = usuarioRepository.findById(revendedorId)
                .orElseThrow(() -> new RuntimeException("Revendedor não encontrado"));

        // revendedor.setStatusaprovacao("REJEITADO");
        usuarioRepository.save(revendedor);
    }

    // --- RELATÓRIOS E DASHBOARD ---

    public Map<String, Object> gerarDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // Vendas (Exemplos - precisa ter métodos no repository para somar)
        Map<String, Object> vendas = new HashMap<>();
        vendas.put("total", pedidoRepository.count());
        // vendas.put("valorTotal", pedidoRepository.somaValorTotal()); // Precisa criar query no repo
        dashboard.put("vendas", vendas);

        // Usuários
        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("total", usuarioRepository.count());
        // usuarios.put("revendedores", usuarioRepository.countByTipo("REVENDEDOR"));
        dashboard.put("usuarios", usuarios);

        // Peças
        Map<String, Object> pecas = new HashMap<>();
        pecas.put("total", pecaRepository.count());
        dashboard.put("pecas", pecas);

        return dashboard;
    }
}