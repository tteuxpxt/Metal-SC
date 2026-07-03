package com.metalSpring.services;

import com.metalSpring.model.dto.ItemPedidoDTO;
import com.metalSpring.model.entity.ItemPedido;
import com.metalSpring.model.entity.Peca;
import com.metalSpring.model.entity.Pedido;
import com.metalSpring.repository.ItemPedidoRepository;
import com.metalSpring.repository.PecaRepository;
import com.metalSpring.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ItemPedidoService {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PecaRepository pecaRepository;

    

    public List<ItemPedido> listarTodos() {
        return itemPedidoRepository.findAll();
    }

    public Optional<ItemPedido> buscarPorId(String id) {
        return itemPedidoRepository.findById(id);
    }

    public List<ItemPedido> buscarPorPedido(String pedidoId) {
        return itemPedidoRepository.findByPedidoId(pedidoId);
    }

    public List<ItemPedido> buscarPorPeca(String pecaId) {
        return itemPedidoRepository.findByPecaId(pecaId);
    }

    

    @Transactional
    public ItemPedido criar(ItemPedidoDTO dto) {
        
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + dto.getPedidoId()));

        
        Peca peca = pecaRepository.findById(dto.getPecaId())
                .orElseThrow(() -> new RuntimeException("Peça não encontrada: " + dto.getPecaId()));

        
        if (!peca.estaDisponivel() || peca.getEstoque() < dto.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente para a peça: " + peca.getNome());
        }

        
        ItemPedido item = new ItemPedido();
        item.setId(UUID.randomUUID().toString());
        item.setPedido(pedido);
        item.setPeca(peca);
        item.setQuantidade(dto.getQuantidade());
        item.setPrecoUnitario(peca.getPreco());

        
        item.calcularSubtotal();

        
        peca.setEstoque(peca.getEstoque() - dto.getQuantidade());
        pecaRepository.save(peca);

        
        ItemPedido itemSalvo = itemPedidoRepository.save(item);

        
        atualizarTotalPedido(pedido.getId());

        return itemSalvo;
    }

    

    @Transactional
    public ItemPedido atualizarQuantidade(String itemId, Integer novaQuantidade) {
        ItemPedido item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + itemId));

        if (novaQuantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        Peca peca = item.getPeca();
        Integer quantidadeAnterior = item.getQuantidade();
        Integer diferenca = novaQuantidade - quantidadeAnterior;

        
        if (diferenca > 0 && peca.getEstoque() < diferenca) {
            throw new RuntimeException("Estoque insuficiente. Disponível: " + peca.getEstoque());
        }

        
        item.setQuantidade(novaQuantidade);
        item.calcularSubtotal();

        
        peca.setEstoque(peca.getEstoque() - diferenca);
        pecaRepository.save(peca);

        
        ItemPedido itemAtualizado = itemPedidoRepository.save(item);

        
        atualizarTotalPedido(item.getPedido().getId());

        return itemAtualizado;
    }

    

    @Transactional
    public void excluir(String itemId) {
        ItemPedido item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + itemId));

        
        Peca peca = item.getPeca();
        peca.setEstoque(peca.getEstoque() + item.getQuantidade());
        pecaRepository.save(peca);

        String pedidoId = item.getPedido().getId();

        
        itemPedidoRepository.delete(item);

        
        atualizarTotalPedido(pedidoId);
    }

    

    public Double calcularSubtotal(String itemId) {
        return itemPedidoRepository.findById(itemId)
                .map(ItemPedido::getSubtotal)
                .orElse(0.0);
    }

    public Double calcularTotalPedido(String pedidoId) {
        List<ItemPedido> itens = buscarPorPedido(pedidoId);
        return itens.stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();
    }

    @Transactional
    public void atualizarTotalPedido(String pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

        Double novoTotal = calcularTotalPedido(pedidoId);
        pedido.setValorTotal(novoTotal);
        pedidoRepository.save(pedido);
    }

    

    public boolean itemPertenceAoPedido(String itemId, String pedidoId) {
        return itemPedidoRepository.findById(itemId)
                .map(item -> item.getPedido().getId().equals(pedidoId))
                .orElse(false);
    }

    public boolean pedidoPossuiItem(String pedidoId, String pecaId) {
        List<ItemPedido> itens = buscarPorPedido(pedidoId);
        return itens.stream()
                .anyMatch(item -> item.getPeca().getId().equals(pecaId));
    }

    public Integer quantidadeTotalPecaNoPedido(String pedidoId, String pecaId) {
        List<ItemPedido> itens = buscarPorPedido(pedidoId);
        return itens.stream()
                .filter(item -> item.getPeca().getId().equals(pecaId))
                .mapToInt(ItemPedido::getQuantidade)
                .sum();
    }

    

    public ItemPedidoDTO toDTO(ItemPedido item) {
        ItemPedidoDTO dto = new ItemPedidoDTO();
        dto.setId(item.getId());
        dto.setPecaId(item.getPeca().getId());
        dto.setPecaNome(item.getPeca().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    public List<ItemPedidoDTO> toDTOList(List<ItemPedido> itens) {
        return itens.stream()
                .map(this::toDTO)
                .toList();
    }
}