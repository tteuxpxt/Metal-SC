package com.metalSpring.controller;

import com.metalSpring.model.dto.AvaliacaoDTO;
import com.metalSpring.model.entity.Avaliacao;
import com.metalSpring.services.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "*")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @GetMapping
    public ResponseEntity<List<Avaliacao>> listarTodas() {
        return ResponseEntity.ok(avaliacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> buscarPorId(@PathVariable String id) {
        return avaliacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/revendedor/{revendedorId}")
    public ResponseEntity<List<Avaliacao>> buscarPorRevendedor(@PathVariable String revendedorId) {
        return ResponseEntity.ok(avaliacaoService.buscarPorRevendedor(revendedorId));
    }

    @GetMapping("/peca/{pecaId}")
    public ResponseEntity<List<Avaliacao>> buscarPorPeca(@PathVariable String pecaId) {
        return ResponseEntity.ok(avaliacaoService.buscarPorPeca(pecaId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Avaliacao>> buscarPorCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(avaliacaoService.buscarPorCliente(clienteId));
    }

    @GetMapping("/revendedor/{revendedorId}/media")
    public ResponseEntity<Double> calcularMediaRevendedor(@PathVariable String revendedorId) {
        Double media = avaliacaoService.calcularMediaPorRevendedor(revendedorId);
        return ResponseEntity.ok(media);
    }

    @GetMapping("/peca/{pecaId}/media")
    public ResponseEntity<Double> calcularMediaPeca(@PathVariable String pecaId) {
        Double media = avaliacaoService.calcularMediaPorPeca(pecaId);
        return ResponseEntity.ok(media);
    }

    @PostMapping
    public ResponseEntity<Avaliacao> criar(@RequestBody AvaliacaoDTO dto) {
        try {
            Avaliacao avaliacao = avaliacaoService.criar(
                    dto.getClienteId(),
                    dto.getRevendedorId(),
                    dto.getPecaId(),
                    dto.getNota(),
                    dto.getComentario()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> editar(
            @PathVariable String id,
            @RequestBody AvaliacaoDTO dto) {
        try {
            Avaliacao avaliacao = avaliacaoService.editarAvaliacao(id, dto.getNota(), dto.getComentario());
            return ResponseEntity.ok(avaliacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        try {
            avaliacaoService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/denunciar")
    public ResponseEntity<Void> denunciar(
            @PathVariable String id,
            @RequestParam String motivo) {
        try {
            avaliacaoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Avaliacao nao encontrada"));
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/nota/{nota}")
    public ResponseEntity<List<Avaliacao>> buscarPorNota(@PathVariable Integer nota) {
        if (nota < 1 || nota > 5) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(avaliacaoService.buscarPorNota(nota));
    }
}
