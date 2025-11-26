package com.metalSpring.controller;

import com.metalSpring.model.dto.UsuarioCadastroDTO;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.services.RevendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/revendedores")
@CrossOrigin(origins = "*")
public class RevendedorController {

    @Autowired
    private RevendedorService revendedorService;

    @GetMapping
    public ResponseEntity<List<Revendedor>> listarTodos() {
        return ResponseEntity.ok(revendedorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Revendedor> buscarPorId(@PathVariable String id) {
        return revendedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Revendedor> buscarPorCnpj(@PathVariable String cnpj) {
        return revendedorService.buscarPorCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody UsuarioCadastroDTO dto) {
        try {
            // Valida campos obrigatórios para revendedor
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
                    dto.getSenha(), // Será criptografada no service
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
}