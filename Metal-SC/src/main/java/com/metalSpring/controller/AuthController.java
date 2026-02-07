package com.metalSpring.controller;

import com.metalSpring.model.dto.EnderecoDTO;
import com.metalSpring.model.dto.UsuarioDTO;
import com.metalSpring.model.embeddable.Endereco;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String senha = payload.get("senha");

        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email e senha sao obrigatorios"));
        }

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais invalidas"));
        }

        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais invalidas"));
        }

        if (!usuario.isAtivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Usuario desativado"));
        }

        return ResponseEntity.ok(toDTO(usuario));
    }

    private UsuarioDTO toDTO(Usuario usuario) {
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
        dto.setFotoUrl(usuario.getFotoUrl());
        dto.setTipo(usuario.getTipo());
        dto.setAtivo(usuario.isAtivo());
        dto.setDataCadastro(usuario.getDataCadastro());
        dto.setEndereco(enderecoDTO);
        return dto;
    }
}
