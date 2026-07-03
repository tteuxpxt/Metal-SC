package com.metalSpring.controller;

import com.metalSpring.model.dto.ComentarioPerfilDTO;
import com.metalSpring.model.entity.ComentarioPerfil;
import com.metalSpring.services.ComentarioPerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comentarios-perfil")
@CrossOrigin(origins = "*")
public class ComentarioPerfilController {

    @Autowired
    private ComentarioPerfilService comentarioPerfilService;

    @GetMapping
    public ResponseEntity<List<ComentarioPerfilDTO>> listarTodos() {
        return ResponseEntity.ok(toDTOList(comentarioPerfilService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioPerfilDTO> buscarPorId(@PathVariable String id) {
        return comentarioPerfilService.buscarPorId(id)
                .map(comentario -> ResponseEntity.ok(toDTO(comentario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/alvo/{alvoId}")
    public ResponseEntity<List<ComentarioPerfilDTO>> buscarPorAlvo(@PathVariable String alvoId) {
        return ResponseEntity.ok(toDTOList(comentarioPerfilService.buscarPorAlvo(alvoId)));
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<ComentarioPerfilDTO>> buscarPorAutor(@PathVariable String autorId) {
        return ResponseEntity.ok(toDTOList(comentarioPerfilService.buscarPorAutor(autorId)));
    }

    @GetMapping("/alvo/{alvoId}/media")
    public ResponseEntity<Double> calcularMediaPorAlvo(@PathVariable String alvoId) {
        return ResponseEntity.ok(comentarioPerfilService.calcularMediaPorAlvo(alvoId));
    }

    @PostMapping
    public ResponseEntity<ComentarioPerfilDTO> criar(@RequestBody ComentarioPerfilDTO dto) {
        try {
            ComentarioPerfil comentario = comentarioPerfilService.criar(
                    dto.getAutorId(),
                    dto.getAlvoId(),
                    dto.getNota(),
                    dto.getComentario()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(comentario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioPerfilDTO> editar(
            @PathVariable String id,
            @RequestBody ComentarioPerfilDTO dto) {
        try {
            ComentarioPerfil comentario = comentarioPerfilService.editar(id, dto.getNota(), dto.getComentario());
            return ResponseEntity.ok(toDTO(comentario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        try {
            comentarioPerfilService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ComentarioPerfilDTO toDTO(ComentarioPerfil comentario) {
        ComentarioPerfilDTO dto = new ComentarioPerfilDTO();
        dto.setId(comentario.getId());
        dto.setAutorId(comentario.getAutor() != null ? comentario.getAutor().getId() : null);
        dto.setAutorNome(comentario.getAutor() != null ? comentario.getAutor().getNome() : null);
        dto.setAlvoId(comentario.getAlvo() != null ? comentario.getAlvo().getId() : null);
        dto.setAlvoNome(comentario.getAlvo() != null ? comentario.getAlvo().getNome() : null);
        dto.setNota(comentario.getNota());
        dto.setComentario(comentario.getComentario());
        dto.setData(comentario.getData());
        return dto;
    }

    private List<ComentarioPerfilDTO> toDTOList(List<ComentarioPerfil> comentarios) {
        return comentarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
