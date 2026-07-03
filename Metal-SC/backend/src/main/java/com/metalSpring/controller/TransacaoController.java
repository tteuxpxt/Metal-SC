package com.metalSpring.controller;

import com.metalSpring.model.dto.TransacaoDTO;
import com.metalSpring.model.entity.Transacao;
import com.metalSpring.model.enums.MetodoPagamento;
import com.metalSpring.model.enums.TransacaoStatus;
import com.metalSpring.services.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @GetMapping
    public ResponseEntity<List<Transacao>> listarTodas() {
        return ResponseEntity.ok(transacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transacao> buscarPorId(@PathVariable String id) {
        return transacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Transacao> buscarPorPedido(@PathVariable String pedidoId) {
        return transacaoService.buscarPorPedido(pedidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Transacao>> buscarPorStatus(@PathVariable TransacaoStatus status) {
        return ResponseEntity.ok(transacaoService.buscarPorStatus(status));
    }

    @GetMapping("/metodo/{metodo}")
    public ResponseEntity<List<Transacao>> buscarPorMetodo(@PathVariable MetodoPagamento metodo) {
        return ResponseEntity.ok(transacaoService.buscarPorMetodo(metodo));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Transacao>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        return ResponseEntity.ok(transacaoService.buscarPorData(dataInicio, dataFim));
    }

    @PostMapping
    public ResponseEntity<Transacao> criar(@RequestBody TransacaoDTO dto) {
        try {
            Transacao transacao = transacaoService.criarTransacao(
                    dto.getPedidoId(),
                    dto.getMetodo(),
                    dto.getReferencia()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/processar")
    public ResponseEntity<Transacao> processar(@PathVariable String id) {
        try {
            Transacao transacao = transacaoService.processarPagamento(id);
            return ResponseEntity.ok(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Transacao> confirmar(@PathVariable String id) {
        try {
            Transacao transacao = transacaoService.confirmar(id);
            return ResponseEntity.ok(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/recusar")
    public ResponseEntity<Transacao> recusar(
            @PathVariable String id,
            @RequestParam String motivo) {
        try {
            Transacao transacao = transacaoService.recusar(id, motivo);
            return ResponseEntity.ok(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/estornar")
    public ResponseEntity<Transacao> estornar(@PathVariable String id) {
        try {
            Transacao transacao = transacaoService.estornar(id);
            return ResponseEntity.ok(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Transacao> cancelar(@PathVariable String id) {
        try {
            Transacao transacao = transacaoService.cancelar(id);
            return ResponseEntity.ok(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/pode-estornar")
    public ResponseEntity<Boolean> podeEstornar(@PathVariable String id) {
        return transacaoService.buscarPorId(id)
                .map(transacao -> ResponseEntity.ok(transacaoService.podeSerEstornada(transacao)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/relatorios/total-periodo")
    public ResponseEntity<Double> calcularTotalPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        Double total = transacaoService.calcularTotalPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/relatorios/contar/{status}")
    public ResponseEntity<Long> contarPorStatus(@PathVariable TransacaoStatus status) {
        Long total = transacaoService.contarPorStatus(status);
        return ResponseEntity.ok(total);
    }
}