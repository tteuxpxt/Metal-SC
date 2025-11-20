package com.metalSpring.controller;

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
        // Implementar no service
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Revendedor> buscarPorId(@PathVariable String id) {
        // Implementar no service
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Revendedor> buscarPorCnpj(@PathVariable String cnpj) {
        // Implementar no service
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Revendedor> criar(@RequestBody Revendedor revendedor) {
        // Implementar no service
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/avaliacao-media")
    public ResponseEntity<Double> calcularAvaliacaoMedia(@PathVariable String id) {
        // Implementar no service
        return ResponseEntity.ok(0.0);
    }
}