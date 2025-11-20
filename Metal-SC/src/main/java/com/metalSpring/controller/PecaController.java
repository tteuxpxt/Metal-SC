package com.metalSpring.controller;

import com.metalSpring.model.dto.PecaDTO;
import com.metalSpring.model.entity.Peca;
import com.metalSpring.services.PecaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pecas")
@CrossOrigin(origins = "*")
public class PecaController {

    @Autowired
    private PecaService pecaService;

    @GetMapping
    public ResponseEntity<List<Peca>> listarTodas() {
        return ResponseEntity.ok(pecaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Peca> buscarPorId(@PathVariable String id) {
        return pecaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Peca>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(pecaService.buscarPorCategoria(categoria));
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Peca>> buscarPorMarca(@PathVariable String marca) {
        return ResponseEntity.ok(pecaService.buscarPorMarca(marca));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Peca>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(pecaService.buscarPorNome(nome));
    }

    @GetMapping("/revendedor/{revendedorId}")
    public ResponseEntity<List<Peca>> buscarPorRevendedor(@PathVariable String revendedorId) {
        return ResponseEntity.ok(pecaService.buscarPorRevendedor(revendedorId));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Peca>> listarDisponiveis() {
        return ResponseEntity.ok(pecaService.listarDisponiveis());
    }

    @PostMapping
    public ResponseEntity<Peca> criar(@RequestBody PecaDTO dto) {
        try {
            Peca peca = pecaService.criar(converterParaEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(peca);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Peca> atualizar(@PathVariable String id, @RequestBody PecaDTO dto) {
        return pecaService.buscarPorId(id)
                .map(pecaExistente -> {
                    atualizarDados(pecaExistente, dto);
                    return ResponseEntity.ok(pecaExistente);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estoque")
    public ResponseEntity<Peca> alterarEstoque(
            @PathVariable String id,
            @RequestParam Integer quantidade) {
        return pecaService.buscarPorId(id)
                .map(peca -> {
                    peca.setEstoque(quantidade);
                    return ResponseEntity.ok(peca);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/imagens")
    public ResponseEntity<Void> adicionarImagem(
            @PathVariable String id,
            @RequestParam String url) {
        return pecaService.buscarPorId(id)
                .map(peca -> {
                    peca.getImagens().add(url);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/imagens")
    public ResponseEntity<Void> removerImagem(
            @PathVariable String id,
            @RequestParam String url) {
        return pecaService.buscarPorId(id)
                .map(peca -> {
                    peca.getImagens().remove(url);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (pecaService.buscarPorId(id).isPresent()) {
            // Implementar lógica de deleção no service
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Peca converterParaEntity(PecaDTO dto) {
        Peca peca = new Peca();
        peca.setNome(dto.getNome());
        peca.setDescricao(dto.getDescricao());
        peca.setCategoria(dto.getCategoria());
        peca.setPreco(dto.getPreco());
        peca.setEstado(dto.getEstado());
        peca.setAno(dto.getAno());
        peca.setMarca(dto.getMarca());
        peca.setModeloVeiculo(dto.getModeloVeiculo());
        peca.setEstoque(dto.getEstoque());
        return peca;
    }

    private void atualizarDados(Peca peca, PecaDTO dto) {
        if (dto.getNome() != null) peca.setNome(dto.getNome());
        if (dto.getDescricao() != null) peca.setDescricao(dto.getDescricao());
        if (dto.getCategoria() != null) peca.setCategoria(dto.getCategoria());
        if (dto.getPreco() != null) peca.setPreco(dto.getPreco());
        if (dto.getEstado() != null) peca.setEstado(dto.getEstado());
        if (dto.getEstoque() != null) peca.setEstoque(dto.getEstoque());
    }
}