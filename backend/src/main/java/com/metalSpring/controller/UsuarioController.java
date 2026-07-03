package com.metalSpring.controller;

import com.metalSpring.model.dto.UsuarioDTO;
import com.metalSpring.model.dto.UsuarioCadastroDTO;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.model.enums.UsuarioTipo;
import com.metalSpring.services.UsuarioService;
import com.metalSpring.services.RevendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RevendedorService revendedorService;

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
    public ResponseEntity<?> criar(@RequestBody UsuarioCadastroDTO dto) {
        try {
            
            if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Nome é obrigatório");
            }
            if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email é obrigatório");
            }
            if (dto.getSenha() == null || dto.getSenha().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Senha é obrigatória");
            }
            if (dto.getTipo() == null) {
                return ResponseEntity.badRequest().body("Tipo de usuário é obrigatório");
            }

            
            if (usuarioService.emailExiste(dto.getEmail())) {
                return ResponseEntity.badRequest().body("Email já cadastrado");
            }

            Usuario usuario;

            
            if (dto.getTipo() == UsuarioTipo.REVENDEDOR) {
                if (dto.getCnpj() == null || dto.getCnpj().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("CNPJ é obrigatório para revendedor");
                }
                if (dto.getNomeLoja() == null || dto.getNomeLoja().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Nome da loja é obrigatório para revendedor");
                }

                Revendedor revendedor = new Revendedor();
                revendedor.setNome(dto.getNome());
                revendedor.setEmail(dto.getEmail());
                revendedor.setSenhaHash(dto.getSenha()); 
                revendedor.setTelefone(dto.getTelefone());
                revendedor.setTipo(UsuarioTipo.REVENDEDOR);
                revendedor.setCnpj(dto.getCnpj());
                revendedor.setNomeLoja(dto.getNomeLoja());

                usuario = revendedorService.criar(revendedor);
            } else {
                
                usuario = new Usuario();
                usuario.setNome(dto.getNome());
                usuario.setEmail(dto.getEmail());
                usuario.setSenhaHash(dto.getSenha()); 
                usuario.setTelefone(dto.getTelefone());
                usuario.setTipo(dto.getTipo());

                if (dto.getEndereco() != null) {
                    com.metalSpring.model.embeddable.Endereco endereco = new com.metalSpring.model.embeddable.Endereco();
                    endereco.setRua(dto.getEndereco().getRua());
                    endereco.setNumero(dto.getEndereco().getNumero());
                    endereco.setComplemento(dto.getEndereco().getComplemento());
                    endereco.setBairro(dto.getEndereco().getBairro());
                    endereco.setCidade(dto.getEndereco().getCidade());
                    endereco.setEstado(dto.getEndereco().getEstado());
                    endereco.setCep(dto.getEndereco().getCep());
                    usuario.setEndereco(endereco);
                }

                usuario = usuarioService.criar(usuario);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao criar usuário: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<?> alterarSenha(
            @PathVariable String id,
            @RequestParam String senhaAtual,
            @RequestParam String novaSenha) {
        try {
            usuarioService.alterarSenha(id, senhaAtual, novaSenha);
            return ResponseEntity.ok().body("Senha alterada com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<?> uploadFotoPerfil(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Arquivo de imagem vazio"));
        }

        try {
            usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.'));
            }

            String filename = UUID.randomUUID() + extension;
            Path dir = Paths.get("uploads", "usuarios", id);
            Files.createDirectories(dir);
            Path destination = dir.resolve(filename);
            Files.copy(file.getInputStream(), destination);

            String url = "/uploads/usuarios/" + id + "/" + filename;
            usuarioService.atualizarFoto(id, url);

            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao salvar imagem"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
