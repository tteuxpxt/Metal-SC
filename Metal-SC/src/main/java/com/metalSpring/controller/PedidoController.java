package com.metalSpring.controller;

import com.metalSpring.model.dto.ItemPedidoDTO;
import com.metalSpring.model.dto.PedidoDTO;
import com.metalSpring.model.dto.EnderecoDTO;
import com.metalSpring.model.entity.Pedido;
import com.metalSpring.model.entity.Peca;
import com.metalSpring.model.entity.Revendedor;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.model.enums.PedidoStatus;
import com.metalSpring.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    private EnderecoDTO toEnderecoDTO(com.metalSpring.model.embeddable.Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        EnderecoDTO dto = new EnderecoDTO();
        dto.setRua(endereco.getRua());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());
        dto.setCep(endereco.getCep());
        return dto;
    }

    private PedidoDTO toPedidoDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setValorTotal(pedido.getValorTotal());
        dto.setStatus(pedido.getStatus());
        dto.setDataCriacao(pedido.getDataCriacao());
        dto.setEnderecoEntrega(toEnderecoDTO(pedido.getEnderecoEntrega()));

        Usuario cliente = pedido.getCliente();
        if (cliente != null) {
            dto.setClienteId(cliente.getId());
            dto.setClienteNome(cliente.getNome());
        }

        Revendedor vendedor = pedido.getVendedor();
        if (vendedor != null) {
            dto.setRevendedorId(vendedor.getId());
            dto.setRevendedorNome(vendedor.getNome());
            if (pedido.getStatus() == PedidoStatus.CONFIRMADO) {
                dto.setRevendedorTelefone(vendedor.getTelefone());
            }
        }

        if (pedido.getItens() != null) {
            List<ItemPedidoDTO> itens = pedido.getItens().stream().map(item -> {
                ItemPedidoDTO itemDTO = new ItemPedidoDTO();
                itemDTO.setId(item.getId());
                itemDTO.setQuantidade(item.getQuantidade());
                itemDTO.setPrecoUnitario(item.getPrecoUnitario());
                itemDTO.setSubtotal(item.getSubtotal());
                itemDTO.setPedidoId(pedido.getId());
                Peca peca = item.getPeca();
                if (peca != null) {
                    itemDTO.setPecaId(peca.getId());
                    itemDTO.setPecaNome(peca.getNome());
                }
                return itemDTO;
            }).collect(Collectors.toList());
            dto.setItens(itens);
        }

        return dto;
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarTodos() {
        List<PedidoDTO> pedidos = pedidoService.listarTodos().stream()
                .map(this::toPedidoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable String id) {
        return pedidoService.buscarPorId(id)
                .map(pedido -> ResponseEntity.ok(toPedidoDTO(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoDTO>> buscarPorCliente(@PathVariable String clienteId) {
        List<PedidoDTO> pedidos = pedidoService.buscarPorCliente(clienteId).stream()
                .map(this::toPedidoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/revendedor/{revendedorId}")
    public ResponseEntity<List<PedidoDTO>> buscarPorRevendedor(@PathVariable String revendedorId) {
        List<PedidoDTO> pedidos = pedidoService.buscarPorRevendedor(revendedorId).stream()
                .map(this::toPedidoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PedidoDTO>> buscarPorStatus(@PathVariable PedidoStatus status) {
        List<PedidoDTO> pedidos = pedidoService.buscarPorStatus(status).stream()
                .map(this::toPedidoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody PedidoDTO dto) {
        try {
            Pedido pedido = pedidoService.criarPedido(
                    dto.getClienteId(),
                    dto.getRevendedorId(),
                    dto.getEnderecoEntrega()
            );

            if (dto.getItens() != null) {
                for (ItemPedidoDTO item : dto.getItens()) {
                    if (item.getPecaId() != null && item.getQuantidade() != null) {
                        pedidoService.adicionarItem(pedido.getId(), item.getPecaId(), item.getQuantidade());
                    }
                }
            }

            Pedido atualizado = pedidoService.buscarPorId(pedido.getId()).orElse(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(toPedidoDTO(atualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<?> adicionarItem(
            @PathVariable String id,
            @RequestBody ItemPedidoDTO itemDTO) {
        try {
            if (itemDTO.getPecaId() == null || itemDTO.getQuantidade() == null) {
                return ResponseEntity.badRequest().build();
            }
            Pedido pedido = pedidoService.adicionarItem(id, itemDTO.getPecaId(), itemDTO.getQuantidade());
            return ResponseEntity.ok(toPedidoDTO(pedido));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{pedidoId}/itens/{pecaId}")
    public ResponseEntity<?> removerItem(
            @PathVariable String pedidoId,
            @PathVariable String pecaId) {
        try {
            Pedido pedido = pedidoService.removerItem(pedidoId, pecaId);
            return ResponseEntity.ok(toPedidoDTO(pedido));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable String id,
            @RequestParam PedidoStatus status) {
        try {
            Pedido pedido = pedidoService.atualizarStatus(id, status);
            return ResponseEntity.ok(toPedidoDTO(pedido));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/confirmar-pagamento")
    public ResponseEntity<?> confirmarPagamento(@PathVariable String id) {
        try {
            Pedido pedido = pedidoService.confirmarPagamento(id);
            return ResponseEntity.ok(toPedidoDTO(pedido));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable String id) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(toPedidoDTO(pedido));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<Double> calcularTotal(@PathVariable String id) {
        return pedidoService.buscarPorId(id)
                .map(pedido -> ResponseEntity.ok(pedido.getValorTotal()))
                .orElse(ResponseEntity.notFound().build());
    }
}
