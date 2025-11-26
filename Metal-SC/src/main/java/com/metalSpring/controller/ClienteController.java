package com.metalSpring.controller;

import com.metalSpring.model.dto.UsuarioCadastroDTO;
import com.metalSpring.model.entity.Cliente;
import com.metalSpring.model.embeddable.Endereco;
import com.metalSpring.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable String id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody UsuarioCadastroDTO dto) {
        try {
            Cliente cliente = new Cliente(
                    dto.getNome(),
                    dto.getEmail(),
                    dto.getSenha(), // Será criptografada no service
                    dto.getTelefone()
            );

            if (dto.getEndereco() != null) {
                Endereco endereco = new Endereco(
                        dto.getEndereco().getRua(),
                        dto.getEndereco().getNumero(),
                        dto.getEndereco().getComplemento(),
                        dto.getEndereco().getBairro(),
                        dto.getEndereco().getCidade(),
                        dto.getEndereco().getEstado(),
                        dto.getEndereco().getCep()
                );
                cliente.setEndereco(endereco);
            }

            Cliente criado = clienteService.criar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(criado);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable String id,
            @RequestBody Cliente cliente) {
        try {
            Cliente atualizado = clienteService.atualizar(id, cliente);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        try {
            clienteService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}