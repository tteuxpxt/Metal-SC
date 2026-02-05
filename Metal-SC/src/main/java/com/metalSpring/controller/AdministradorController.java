package com.metalSpring.controller;

import com.metalSpring.model.dto.EnderecoDTO;
import com.metalSpring.model.dto.RevendedorAdminDTO;
import com.metalSpring.model.dto.UsuarioDTO;
import com.metalSpring.model.embeddable.Endereco;
import com.metalSpring.model.entity.Administrador;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.services.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @Value("${app.premium.dias:30}")
    private int diasPremiumPadrao;

    // --- ENDPOINTS DE LEITURA ---

    @GetMapping
    public ResponseEntity<List<Administrador>> listarTodos() {
        return ResponseEntity.ok(administradorService.listarTodos());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = administradorService.listarTodosUsuarios().stream()
                .map(this::toUsuarioDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/revendedores")
    public ResponseEntity<List<RevendedorAdminDTO>> listarRevendedores() {
        List<RevendedorAdminDTO> revendedores = administradorService.listarRevendedores().stream()
                .map(this::toRevendedorDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(revendedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(administradorService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private UsuarioDTO toUsuarioDTO(Usuario usuario) {
        EnderecoDTO enderecoDTO = null;
        Endereco endereco = usuario.getEndereco();
        if (endereco != null) {
            enderecoDTO = new EnderecoDTO();
            enderecoDTO.setRua(endereco.getRua());
            enderecoDTO.setNumero(endereco.getNumero());
            enderecoDTO.setComplemento(endereco.getComplemento());
            enderecoDTO.setBairro(endereco.getBairro());
            enderecoDTO.setCidade(endereco.getCidade());
            enderecoDTO.setEstado(endereco.getEstado());
            enderecoDTO.setCep(endereco.getCep());
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());
        dto.setTipo(usuario.getTipo());
        dto.setAtivo(usuario.isAtivo());
        dto.setDataCadastro(usuario.getDataCadastro());
        dto.setEndereco(enderecoDTO);
        return dto;
    }

    private RevendedorAdminDTO toRevendedorDTO(Revendedor revendedor) {
        RevendedorAdminDTO dto = new RevendedorAdminDTO();
        dto.setId(revendedor.getId());
        dto.setNome(revendedor.getNome());
        dto.setEmail(revendedor.getEmail());
        dto.setTipo(revendedor.getTipo());
        dto.setAtivo(revendedor.isAtivo());
        dto.setSaldoTaxas(revendedor.getSaldoTaxas());
        dto.setPremiumAtivo(revendedor.getPremiumAtivo());
        dto.setPremiumAte(revendedor.getPremiumAte());
        dto.setDataCadastro(revendedor.getDataCadastro());
        return dto;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(administradorService.gerarDashboard());
    }

    // --- ENDPOINTS DE ESCRITA ---

    @PostMapping
    public ResponseEntity<Administrador> criar(@RequestBody Administrador administrador) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(administradorService.criar(administrador));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/usuarios/{usuarioId}/bloquear")
    public ResponseEntity<Map<String, String>> bloquearUsuario(
            @PathVariable String usuarioId,
            @RequestParam(required = false) String motivo) {
        try {
            administradorService.bloquearUsuario(usuarioId, motivo);
            return ResponseEntity.ok(Map.of("message", "Usuario bloqueado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/usuarios/{usuarioId}/desbloquear")
    public ResponseEntity<Map<String, String>> desbloquearUsuario(@PathVariable String usuarioId) {
        try {
            administradorService.desbloquearUsuario(usuarioId);
            return ResponseEntity.ok(Map.of("message", "Usuario desbloqueado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Map<String, String>> removerUsuario(
            @PathVariable String usuarioId,
            @RequestParam(required = false) String motivo) {
        try {
            administradorService.removerUsuario(usuarioId, motivo);
            return ResponseEntity.ok(Map.of("message", "Usuario removido com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/revendedores/{revendedorId}")
    public ResponseEntity<Map<String, String>> removerRevendedor(
            @PathVariable String revendedorId,
            @RequestParam(required = false) String motivo) {
        try {
            administradorService.removerRevendedor(revendedorId, motivo);
            return ResponseEntity.ok(Map.of("message", "Revendedor removido com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/pecas/{pecaId}")
    public ResponseEntity<Map<String, String>> removerPeca(
            @PathVariable String pecaId,
            @RequestParam(required = false) String motivo) {
        try {
            administradorService.removerPeca(pecaId, motivo);
            return ResponseEntity.ok(Map.of("message", "Peca removida com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/revendedores/{revendedorId}/aprovar")
    public ResponseEntity<Map<String, String>> aprovarRevendedor(@PathVariable String revendedorId) {
        try {
            administradorService.aprovarRevendedor(revendedorId);
            return ResponseEntity.ok(Map.of("message", "Revendedor aprovado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/revendedores/{revendedorId}/taxas/baixar")
    public ResponseEntity<Map<String, String>> baixarTaxasRevendedor(
            @PathVariable String revendedorId,
            @RequestParam(required = false) Double valor) {
        try {
            administradorService.baixarTaxas(revendedorId, valor);
            return ResponseEntity.ok(Map.of("message", "Taxas atualizadas com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/revendedores/{revendedorId}/premium")
    public ResponseEntity<Map<String, String>> ativarPremium(
            @PathVariable String revendedorId,
            @RequestParam(required = false) Integer dias) {
        try {
            int diasAplicar = (dias == null || dias <= 0) ? diasPremiumPadrao : dias;
            administradorService.ativarPremium(revendedorId, diasAplicar);
            return ResponseEntity.ok(Map.of("message", "Premium ativado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/revendedores/{revendedorId}/premium")
    public ResponseEntity<Map<String, String>> desativarPremium(@PathVariable String revendedorId) {
        try {
            administradorService.desativarPremium(revendedorId);
            return ResponseEntity.ok(Map.of("message", "Premium desativado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
