package com.metalSpring.controller;

import com.metalSpring.model.dto.PecaDTO;
import com.metalSpring.model.entity.Peca;
import com.metalSpring.services.PecaService;
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

    @GetMapping("/localizacao")
    public ResponseEntity<List<Peca>> buscarPorLocalizacao(
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado) {
        if ((cidade == null || cidade.isBlank()) && (estado == null || estado.isBlank())) {
            return ResponseEntity.badRequest().build();
        }
        if (cidade != null && !cidade.isBlank() && estado != null && !estado.isBlank()) {
            return ResponseEntity.ok(pecaService.buscarPorCidadeEEstado(cidade, estado));
        }
        if (cidade != null && !cidade.isBlank()) {
            return ResponseEntity.ok(pecaService.buscarPorCidade(cidade));
        }
        return ResponseEntity.ok(pecaService.buscarPorEstadoEndereco(estado));
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
    public ResponseEntity<?> criar(@RequestBody PecaDTO dto) {
        try {
            
            if (dto.getRevendedorId() == null || dto.getRevendedorId().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("O campo 'revendedorId' é obrigatório");
            }

            
            Peca peca = converterParaEntity(dto);

            
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

    @PostMapping("/{id}/imagens/upload")
    public ResponseEntity<?> uploadImagem(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo de imagem vazio");
        }

        try {
            Peca peca = pecaService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Peça não encontrada"));
            if (peca.getImagens() != null && peca.getImagens().size() >= 3) {
                return ResponseEntity.badRequest().body("Limite de 3 imagens por peça");
            }

            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf('.'));
            }

            String filename = UUID.randomUUID() + extension;
            Path dir = Paths.get("uploads", "pecas", id);
            Files.createDirectories(dir);
            Path destination = dir.resolve(filename);
            Files.copy(file.getInputStream(), destination);

            String url = "/uploads/pecas/" + id + "/" + filename;
            pecaService.adicionarImagem(id, url);

            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar imagem");
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
        if (dto.getEndereco() != null) {
            com.metalSpring.model.embeddable.Endereco endereco = new com.metalSpring.model.embeddable.Endereco();
            endereco.setRua(dto.getEndereco().getRua());
            endereco.setNumero(dto.getEndereco().getNumero());
            endereco.setComplemento(dto.getEndereco().getComplemento());
            endereco.setBairro(dto.getEndereco().getBairro());
            endereco.setCidade(dto.getEndereco().getCidade());
            endereco.setEstado(dto.getEndereco().getEstado());
            endereco.setCep(dto.getEndereco().getCep());
            peca.setEndereco(endereco);
        }
        
        return peca;
    }
}

