package com.metalSpring.controller;

import com.metalSpring.model.entity.Administrador;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.services.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    // --- ENDPOINTS DE LEITURA ---

    @GetMapping
    public ResponseEntity<List<Administrador>> listarTodos() {
        return ResponseEntity.ok(administradorService.listarTodos());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(administradorService.listarTodosUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(administradorService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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
            return ResponseEntity.ok(Map.of("message", "Usuário bloqueado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/usuarios/{usuarioId}/desbloquear")
    public ResponseEntity<Map<String, String>> desbloquearUsuario(@PathVariable String usuarioId) {
        try {
            administradorService.desbloquearUsuario(usuarioId);
            return ResponseEntity.ok(Map.of("message", "Usuário desbloqueado com sucesso"));
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
            return ResponseEntity.ok(Map.of("message", "Peça removida com sucesso"));
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
}