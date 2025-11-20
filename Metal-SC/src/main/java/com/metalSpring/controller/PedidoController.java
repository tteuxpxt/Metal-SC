package com.metalSpring.controller;

import com.metalSpring.model.dto.ItemPedidoDTO;
import com.metalSpring.model.dto.PedidoDTO;
import com.metalSpring.model.entity.Pedido;
import com.metalSpring.model.enums.PedidoStatus;
import com.metalSpring.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable String id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> buscarPorCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(pedidoService.buscarPorCliente(clienteId));
    }

    @GetMapping("/revendedor/{revendedorId}")
    public ResponseEntity<List<Pedido>> buscarPorRevendedor(@PathVariable String revendedorId) {
        return ResponseEntity.ok(pedidoService.buscarPorRevendedor(revendedorId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pedido>> buscarPorStatus(@PathVariable PedidoStatus status) {
        return ResponseEntity.ok(pedidoService.buscarPorStatus(status));
    }

    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody PedidoDTO dto) {
        try {
            Pedido pedido = pedidoService.criarPedido(
                    dto.getClienteId(),
                    dto.getRevendedorId(),
                    dto.getEnderecoEntrega()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<Pedido> adicionarItem(
            @PathVariable String id,
            @RequestBody ItemPedidoDTO itemDTO) {
        return pedidoService.buscarPorId(id)
                .map(pedido -> {
                    // Implementar lógica de adicionar item
                    return ResponseEntity.ok(pedido);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{pedidoId}/itens/{pecaId}")
    public ResponseEntity<Pedido> removerItem(
            @PathVariable String pedidoId,
            @PathVariable String pecaId) {
        return pedidoService.buscarPorId(pedidoId)
                .map(pedido -> {
                    // Implementar lógica de remover item
                    return ResponseEntity.ok(pedido);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(
            @PathVariable String id,
            @RequestParam PedidoStatus status) {
        return pedidoService.buscarPorId(id)
                .map(pedido -> {
                    pedido.setStatus(status);
                    return ResponseEntity.ok(pedido);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/confirmar-pagamento")
    public ResponseEntity<Pedido> confirmarPagamento(@PathVariable String id) {
        try {
            Pedido pedido = pedidoService.confirmarPagamento(id);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelar(@PathVariable String id) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<Double> calcularTotal(@PathVariable String id) {
        return pedidoService.buscarPorId(id)
                .map(pedido -> ResponseEntity.ok(pedido.getValorTotal()))
                .orElse(ResponseEntity.notFound().build());
    }
}
