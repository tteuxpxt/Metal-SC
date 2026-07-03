package com.metalSpring.controller;

import com.metalSpring.model.dto.EnderecoDTO;
import com.metalSpring.model.dto.PerfilDTO;
import com.metalSpring.model.dto.UsuarioCadastroDTO;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.services.RevendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/revendedores")
@CrossOrigin(origins = "*")
public class RevendedorController {

    @Autowired
    private RevendedorService revendedorService;

    @GetMapping
    public ResponseEntity<List<PerfilDTO>> listarTodos() {
        List<PerfilDTO> revendedores = revendedorService.listarTodos().stream()
                .map(this::toPerfilDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(revendedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilDTO> buscarPorId(@PathVariable String id) {
        return revendedorService.buscarPorId(id)
                .map(revendedor -> ResponseEntity.ok(toPerfilDTO(revendedor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<PerfilDTO> buscarPorCnpj(@PathVariable String cnpj) {
        return revendedorService.buscarPorCnpj(cnpj)
                .map(revendedor -> ResponseEntity.ok(toPerfilDTO(revendedor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody Revendedor revendedor) {
        try {
            Revendedor atualizado = revendedorService.atualizar(id, revendedor);
            return ResponseEntity.ok(toPerfilDTO(atualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody UsuarioCadastroDTO dto) {
        try {
            
            if (dto.getCnpj() == null || dto.getCnpj().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(java.util.Map.of("error", "CNPJ é obrigatório"));
            }
            if (dto.getNomeLoja() == null || dto.getNomeLoja().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(java.util.Map.of("error", "Nome da Loja é obrigatório"));
            }

            Revendedor revendedor = new Revendedor(
                    dto.getNome(),
                    dto.getEmail(),
                    dto.getSenha(), 
                    dto.getTelefone(),
                    dto.getCnpj(),
                    dto.getNomeLoja()
            );

            Revendedor criado = revendedorService.criar(revendedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(criado);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/avaliacao-media")
    public ResponseEntity<Double> calcularAvaliacaoMedia(@PathVariable String id) {
        try {
            double media = revendedorService.calcularAvaliacaoMedia(id);
            return ResponseEntity.ok(media);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private PerfilDTO toPerfilDTO(Revendedor revendedor) {
        PerfilDTO dto = new PerfilDTO();
        dto.setId(revendedor.getId());
        dto.setNome(revendedor.getNome());
        dto.setEmail(revendedor.getEmail());
        dto.setTelefone(revendedor.getTelefone());
        dto.setTipo(revendedor.getTipo());
        dto.setDataCadastro(revendedor.getDataCadastro());
        dto.setFotoUrl(revendedor.getFotoUrl());
        dto.setNomeLoja(revendedor.getNomeLoja());
        dto.setCnpj(revendedor.getCnpj());
        dto.setEndereco(toEnderecoDTO(revendedor.getEndereco()));
        return dto;
    }

    private EnderecoDTO toEnderecoDTO(com.metalSpring.model.embeddable.Endereco endereco) {
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
}
