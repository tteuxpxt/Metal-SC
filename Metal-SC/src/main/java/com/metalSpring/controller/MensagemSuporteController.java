package com.metalSpring.controller;

import com.metalSpring.model.dto.MensagemSuporteDTO;
import com.metalSpring.model.entity.MensagemSuporte;
import com.metalSpring.model.enums.CanalSuporte;
import com.metalSpring.services.MensagemSuporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suporte")
@CrossOrigin(origins = "*")
public class MensagemSuporteController {

    @Autowired
    private MensagemSuporteService mensagemSuporteService;

    @GetMapping
    public ResponseEntity<List<MensagemSuporte>> listarTodas() {
        // return ResponseEntity.ok(mensagemSuporteService.listarTodas());
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MensagemSuporte> buscarPorId(@PathVariable String id) {
        // return mensagemSuporteService.buscarPorId(id)
        //         .map(ResponseEntity::ok)
        //         .orElse(ResponseEntity.notFound().build());
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MensagemSuporte>> buscarPorUsuario(@PathVariable String usuarioId) {
        // return ResponseEntity.ok(mensagemSuporteService.buscarPorUsuario(usuarioId));
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/nao-lidas")
    public ResponseEntity<List<MensagemSuporte>> listarNaoLidas() {
        // return ResponseEntity.ok(mensagemSuporteService.listarNaoLidas());
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/atendente/{atendenteId}")
    public ResponseEntity<List<MensagemSuporte>> buscarPorAtendente(@PathVariable String atendenteId) {
        // return ResponseEntity.ok(mensagemSuporteService.buscarPorAtendente(atendenteId));
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/canal/{canal}")
    public ResponseEntity<List<MensagemSuporte>> buscarPorCanal(@PathVariable CanalSuporte canal) {
        // return ResponseEntity.ok(mensagemSuporteService.buscarPorCanal(canal));
        return ResponseEntity.ok(List.of());
    }

    @PostMapping
    public ResponseEntity<MensagemSuporte> criar(@RequestBody MensagemSuporteDTO dto) {
        try {
            // MensagemSuporte mensagem = mensagemSuporteService.criar(dto);
            // return ResponseEntity.status(HttpStatus.CREATED).body(mensagem);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/marcar-lida")
    public ResponseEntity<MensagemSuporte> marcarComoLida(@PathVariable String id) {
        try {
            // MensagemSuporte mensagem = mensagemSuporteService.marcarComoLida(id);
            // return ResponseEntity.ok(mensagem);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/responder")
    public ResponseEntity<MensagemSuporte> responder(
            @PathVariable String id,
            @RequestParam String texto,
            @RequestParam String atendenteId) {
        try {
            // MensagemSuporte mensagem = mensagemSuporteService.responder(id, texto, atendenteId);
            // return ResponseEntity.ok(mensagem);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/encaminhar")
    public ResponseEntity<MensagemSuporte> encaminhar(
            @PathVariable String id,
            @RequestParam String novoAtendenteId) {
        try {
            // MensagemSuporte mensagem = mensagemSuporteService.encaminhar(id, novoAtendenteId);
            // return ResponseEntity.ok(mensagem);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        try {
            // mensagemSuporteService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<?> obterEstatisticas() {
        // return ResponseEntity.ok(mensagemSuporteService.obterEstatisticas());
        return ResponseEntity.ok(java.util.Map.of(
                "total", 0,
                "naoLidas", 0,
                "emAndamento", 0,
                "resolvidas", 0
        ));
    }
}
