package com.metalSpring.controller;

import com.metalSpring.model.enums.AlertaModeracaoStatus;
import com.metalSpring.services.AdministradorService;
import com.metalSpring.services.NegociacaoService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/moderacao")
@CrossOrigin(origins = "*")
public class ModeracaoController {

    @Autowired
    private NegociacaoService negociacaoService;

    @Autowired
    private AdministradorService administradorService;

    @GetMapping("/alertas")
    public ResponseEntity<?> listarAlertas(
            @RequestParam(required = false) String usuarioId,
            @RequestParam(required = false) String data,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) AlertaModeracaoStatus status
    ) {
        return ResponseEntity.ok(negociacaoService.listarAlertas(usuarioId, data, tipo, status));
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<?> estatisticas() {
        return ResponseEntity.ok(negociacaoService.estatisticasAlertas());
    }

    @PostMapping("/alertas/{alertaId}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable String alertaId,
            @RequestParam AlertaModeracaoStatus status
    ) {
        try {
            return ResponseEntity.ok(negociacaoService.atualizarAlerta(alertaId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/mensagens/{mensagemId}")
    public ResponseEntity<?> removerMensagem(@PathVariable String mensagemId) {
        try {
            negociacaoService.removerMensagem(mensagemId);
            return ResponseEntity.ok(Map.of("message", "Mensagem removida"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/usuarios/{usuarioId}/suspender")
    public ResponseEntity<?> suspenderUsuario(@PathVariable String usuarioId) {
        try {
            administradorService.bloquearUsuario(usuarioId, "Infracao no chat de negociacao");
            return ResponseEntity.ok(Map.of("message", "Usuario suspenso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
