package com.metalSpring.controller;

import com.metalSpring.model.dto.EnderecoDTO;
import com.metalSpring.model.dto.PerfilDTO;
import com.metalSpring.model.embeddable.Endereco;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/perfis")
@CrossOrigin(origins = "*")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/{id}")
    public ResponseEntity<PerfilDTO> buscarPorId(@PathVariable String id) {
        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.ok(toPerfilDTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<PerfilDTO> atualizarPerfil(
            @PathVariable String id,
            @RequestBody PerfilDTO payload) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    if (payload.getNome() != null) {
                        usuario.setNome(payload.getNome());
                    }
                    if (payload.getTelefone() != null) {
                        usuario.setTelefone(payload.getTelefone());
                    }
                    if (payload.getEndereco() != null) {
                        if (usuario.getEndereco() == null) {
                            usuario.setEndereco(toEndereco(payload.getEndereco()));
                        } else {
                            usuario.getEndereco().atualizar(toEndereco(payload.getEndereco()));
                        }
                    }

                    if (usuario instanceof Revendedor revendedor && payload.getNomeLoja() != null) {
                        revendedor.setNomeLoja(payload.getNomeLoja());
                    }

                    Usuario atualizado = usuarioRepository.save(usuario);
                    return ResponseEntity.ok(toPerfilDTO(atualizado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private PerfilDTO toPerfilDTO(Usuario usuario) {
        PerfilDTO dto = new PerfilDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());
        dto.setFotoUrl(usuario.getFotoUrl());
        dto.setTipo(usuario.getTipo());
        dto.setDataCadastro(usuario.getDataCadastro());
        dto.setEndereco(toEnderecoDTO(usuario.getEndereco()));

        if (usuario instanceof Revendedor revendedor) {
            dto.setNomeLoja(revendedor.getNomeLoja());
            dto.setCnpj(revendedor.getCnpj());
        }

        return dto;
    }

    private EnderecoDTO toEnderecoDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        EnderecoDTO dto = new EnderecoDTO();
        dto.setRua(endereco.getRua());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());
        dto.setCep(endereco.getCep());
        return dto;
    }

    private Endereco toEndereco(EnderecoDTO dto) {
        Endereco endereco = new Endereco();
        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setCep(dto.getCep());
        return endereco;
    }
}
