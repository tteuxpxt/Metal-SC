package com.metalSpring.services;

import com.metalSpring.model.entity.Administrador;
import com.metalSpring.model.entity.Peca;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.model.enums.UsuarioTipo;
import com.metalSpring.repository.AdministradorRepository;
import com.metalSpring.repository.PecaRepository;
import com.metalSpring.repository.PedidoRepository;
import com.metalSpring.repository.RevendedorRepository;
import com.metalSpring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    @Autowired
    private RevendedorRepository revendedorRepository;

    
    public List<Administrador> listarTodos() {
        return adminRepository.findAll();
    }

    public Administrador criar(Administrador admin) {
        return adminRepository.save(admin);
    }

    public Administrador buscarPorId(String id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador nao encontrado"));
    }

    

    public void bloquearUsuario(String usuarioId, String motivo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public void desbloquearUsuario(String usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    public void removerUsuario(String usuarioId, String motivo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        if (usuario.getTipo() == UsuarioTipo.ADMINISTRADOR) {
            throw new RuntimeException("Nao e possivel remover um administrador");
        }

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public void removerRevendedor(String revendedorId, String motivo) {
        Revendedor revendedor = revendedorRepository.findById(revendedorId)
                .orElseThrow(() -> new RuntimeException("Revendedor nao encontrado"));

        if (revendedor.getTipo() == UsuarioTipo.ADMINISTRADOR) {
            throw new RuntimeException("Nao e possivel remover um administrador");
        }

        revendedor.setAtivo(false);
        revendedorRepository.save(revendedor);
    }

    public void removerPeca(String pecaId, String motivo) {
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RuntimeException("Peca nao encontrada"));

        peca.setDisponivel(false);
        pecaRepository.save(peca);
    }

    public void aprovarRevendedor(String revendedorId) {
        Usuario revendedor = usuarioRepository.findById(revendedorId)
                .orElseThrow(() -> new RuntimeException("Revendedor nao encontrado"));

        usuarioRepository.save(revendedor);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Revendedor> listarRevendedores() {
        return revendedorRepository.findAll();
    }

    public void rejeitarRevendedor(String revendedorId, String motivo) {
        Usuario revendedor = usuarioRepository.findById(revendedorId)
                .orElseThrow(() -> new RuntimeException("Revendedor nao encontrado"));

        usuarioRepository.save(revendedor);
    }

    public void baixarTaxas(String revendedorId, Double valor) {
        Revendedor revendedor = revendedorRepository.findById(revendedorId)
                .orElseThrow(() -> new RuntimeException("Revendedor nao encontrado"));

        double saldoAtual = revendedor.getSaldoTaxas() != null ? revendedor.getSaldoTaxas() : 0.0;
        double novoSaldo;
        if (valor == null) {
            novoSaldo = 0.0;
        } else {
            novoSaldo = Math.max(0.0, saldoAtual - valor);
        }
        revendedor.setSaldoTaxas(novoSaldo);
        revendedorRepository.save(revendedor);
    }

    public void ativarPremium(String revendedorId, int dias) {
        Revendedor revendedor = revendedorRepository.findById(revendedorId)
                .orElseThrow(() -> new RuntimeException("Revendedor nao encontrado"));

        if (dias <= 0) {
            throw new RuntimeException("Dias deve ser maior que zero");
        }

        revendedor.setPremiumAtivo(true);
        revendedor.setPremiumAte(LocalDateTime.now().plusDays(dias));
        revendedorRepository.save(revendedor);
    }

    public void desativarPremium(String revendedorId) {
        Revendedor revendedor = revendedorRepository.findById(revendedorId)
                .orElseThrow(() -> new RuntimeException("Revendedor nao encontrado"));

        revendedor.setPremiumAtivo(false);
        revendedor.setPremiumAte(null);
        revendedorRepository.save(revendedor);
    }

    

    public Map<String, Object> gerarDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        Map<String, Object> vendas = new HashMap<>();
        vendas.put("total", pedidoRepository.count());
        dashboard.put("vendas", vendas);

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("total", usuarioRepository.count());
        dashboard.put("usuarios", usuarios);

        Map<String, Object> pecas = new HashMap<>();
        pecas.put("total", pecaRepository.count());
        dashboard.put("pecas", pecas);

        return dashboard;
    }
}
