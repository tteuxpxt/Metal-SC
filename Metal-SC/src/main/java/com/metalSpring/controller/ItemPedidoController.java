package com.metalSpring.controller;

import com.metalSpring.model.dto.ItemPedidoDTO;
import com.metalSpring.model.entity.ItemPedido;
import com.metalSpring.services.ItemPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens-pedido")
@CrossOrigin(origins = "*")
public class ItemPedidoController {

    @Autowired
    private ItemPedidoService itemPedidoService;

    @GetMapping
    public ResponseEntity<List<ItemPedido>> listarTodos() {
        // return ResponseEntity.ok(itemPedidoService.listarTodos());
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> buscarPorId(@PathVariable String id) {
        // return itemPedidoService.buscarPorId(id)
        //         .map(ResponseEntity::ok)
        //         .orElse(ResponseEntity.notFound().build());
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<ItemPedido>> buscarPorPedido(@PathVariable String pedidoId) {
        // return ResponseEntity.ok(itemPedidoService.buscarPorPedido(pedidoId));
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/peca/{pecaId}")
    public ResponseEntity<List<ItemPedido>> buscarPorPeca(@PathVariable String pecaId) {
        // return ResponseEntity.ok(itemPedidoService.buscarPorPeca(pecaId));
        return ResponseEntity.ok(List.of());
    }

    @PostMapping
    public ResponseEntity<ItemPedido> criar(@RequestBody ItemPedidoDTO dto) {
        try {
            // ItemPedido item = itemPedidoService.criar(dto);
            // return ResponseEntity.status(HttpStatus.CREATED).body(item);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/quantidade")
    public ResponseEntity<ItemPedido> atualizarQuantidade(
            @PathVariable String id,
            @RequestParam Integer quantidade) {
        try {
            // ItemPedido item = itemPedidoService.atualizarQuantidade(id, quantidade);
            // return ResponseEntity.ok(item);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/subtotal")
    public ResponseEntity<Double> calcularSubtotal(@PathVariable String id) {
        // return itemPedidoService.buscarPorId(id)
        //         .map(item -> ResponseEntity.ok(item.getSubtotal()))
        //         .orElse(ResponseEntity.notFound().build());
        return ResponseEntity.ok(0.0);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        try {
            // itemPedidoService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pedido/{pedidoId}/total")
    public ResponseEntity<Double> calcularTotalPedido(@PathVariable String pedidoId) {
        // Double total = itemPedidoService.calcularTotalPedido(pedidoId);
        // return ResponseEntity.ok(total);
        return ResponseEntity.ok(0.0);
    }
}