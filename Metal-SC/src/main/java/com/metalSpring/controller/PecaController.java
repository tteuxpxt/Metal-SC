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

    /**
     * ✅ ENDPOINT CORRIGIDO PARA CRIAR PEÇAS
     * Agora recebe o revendedorId do DTO e passa para o service
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody PecaDTO dto) {
        try {
            // Valida se o revendedorId foi enviado
            if (dto.getRevendedorId() == null || dto.getRevendedorId().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("O campo 'revendedorId' é obrigatório");
            }

            // Converte DTO para Entity
            Peca peca = converterParaEntity(dto);

            // Cria a peça com o revendedor
            Peca pecaCriada = pecaService.criar(peca, dto.getRevendedorId());

            return ResponseEntity.status(HttpStatus.CREATED).body(pecaCriada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao criar peça: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody PecaDTO dto) {
        try {
            Peca pecaAtualizada = converterParaEntity(dto);
            Peca peca = pecaService.atualizar(id, pecaAtualizada);
            return ResponseEntity.ok(peca);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao atualizar peça: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/estoque")
    public ResponseEntity<?> alterarEstoque(
            @PathVariable String id,
            @RequestParam Integer quantidade) {
        try {
            pecaService.alterarEstoque(id, quantidade);
            return pecaService.buscarPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao alterar estoque: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/imagens")
    public ResponseEntity<?> adicionarImagem(
            @PathVariable String id,
            @RequestParam String url) {
        try {
            pecaService.adicionarImagem(id, url);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao adicionar imagem: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/imagens")
    public ResponseEntity<?> removerImagem(
            @PathVariable String id,
            @RequestParam String url) {
        try {
            pecaService.removerImagem(id, url);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao remover imagem: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) {
        try {
            pecaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Erro ao deletar peça: " + e.getMessage());
        }
    }

    /**
     * Converte PecaDTO para Entity Peca
     */
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
        // NÃO setamos o vendedor aqui - isso é feito no service
        return peca;
    }
}