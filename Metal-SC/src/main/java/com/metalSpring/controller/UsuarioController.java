package com.metalSpring.controller;

import com.metalSpring.model.dto.UsuarioDTO;
import com.metalSpring.model.dto.UsuarioCadastroDTO;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable String id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Usuario>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(usuarioService.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody UsuarioCadastroDTO dto) {
        try {
            Usuario usuario = usuarioService.criar(converterParaEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable String id,
            @RequestParam String senhaAtual,
            @RequestParam String novaSenha) {
        try {
            usuarioService.buscarPorId(id).ifPresent(usuario -> {
                // Implementar lógica de alteração de senha no service
            });
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        usuarioService.buscarPorId(id).ifPresent(usuario -> {
            // Implementar lógica de deleção
        });
        return ResponseEntity.noContent().build();
    }

    private Usuario converterParaEntity(UsuarioCadastroDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        usuario.setTipo(dto.getTipo());
        return usuario;
    }
}