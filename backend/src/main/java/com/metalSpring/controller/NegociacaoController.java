package com.metalSpring.controller;

import com.metalSpring.model.dto.NegociacaoConversaDTO;
import com.metalSpring.model.dto.NegociacaoCriarDTO;
import com.metalSpring.model.dto.NegociacaoEnviarMensagemDTO;
import com.metalSpring.model.enums.NegociacaoStatus;
import com.metalSpring.model.enums.TipoMensagemNegociacao;
import com.metalSpring.services.NegociacaoService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/negociacoes")
@CrossOrigin(origins = "*")
public class NegociacaoController {

    @Autowired
    private NegociacaoService negociacaoService;

    @PostMapping
    public ResponseEntity<?> iniciar(@RequestBody NegociacaoCriarDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    negociacaoService.iniciar(dto.getPecaId(), dto.getClienteId(), dto.getConteudo(), dto.getValorProposto())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obter(@PathVariable String id, @RequestParam(required = false) String visualizadorId) {
        try {
            return ResponseEntity.ok(negociacaoService.obter(id, visualizadorId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<NegociacaoConversaDTO>> listarPorCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(negociacaoService.listarPorCliente(clienteId));
    }

    @GetMapping("/revendedor/{revendedorId}")
    public ResponseEntity<List<NegociacaoConversaDTO>> listarPorRevendedor(@PathVariable String revendedorId) {
        return ResponseEntity.ok(negociacaoService.listarPorRevendedor(revendedorId));
    }

    @PostMapping("/{id}/mensagens")
    public ResponseEntity<?> enviarMensagem(@PathVariable String id, @RequestBody NegociacaoEnviarMensagemDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    negociacaoService.enviarMensagem(id, dto.getRemetenteId(), dto.getConteudo(), dto.getValorProposto(), TipoMensagemNegociacao.TEXTO)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/contraproposta")
    public ResponseEntity<?> contraproposta(@PathVariable String id, @RequestBody NegociacaoEnviarMensagemDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    negociacaoService.enviarMensagem(id, dto.getRemetenteId(), dto.getConteudo(), dto.getValorProposto(), TipoMensagemNegociacao.CONTRAPROPOSTA)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/aceitar")
    public ResponseEntity<?> aceitar(@PathVariable String id, @RequestBody NegociacaoEnviarMensagemDTO dto) {
        try {
            return ResponseEntity.ok(negociacaoService.aprovar(id, dto.getRemetenteId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovar(@PathVariable String id, @RequestBody NegociacaoEnviarMensagemDTO dto) {
        try {
            return ResponseEntity.ok(negociacaoService.aprovar(id, dto.getRemetenteId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/recusar")
    public ResponseEntity<?> recusar(@PathVariable String id, @RequestBody NegociacaoEnviarMensagemDTO dto) {
        try {
            return ResponseEntity.ok(negociacaoService.recusar(id, dto.getRemetenteId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/encerrar")
    public ResponseEntity<?> encerrar(
            @PathVariable String id,
            @RequestParam String usuarioId,
            @RequestParam(defaultValue = "CANCELADO") NegociacaoStatus status
    ) {
        try {
            return ResponseEntity.ok(negociacaoService.encerrar(id, usuarioId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/lidas")
    public ResponseEntity<Void> marcarComoLidas(@PathVariable String id, @RequestParam String usuarioId) {
        negociacaoService.marcarComoLidas(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
