package com.metalSpring.services;

import com.metalSpring.model.dto.EnderecoDTO;
import com.metalSpring.model.embeddable.Endereco;
import com.metalSpring.model.entity.*;
import com.metalSpring.model.enums.PedidoStatus;
import com.metalSpring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RevendedorRepository revendedorRepository;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Value("${app.taxa.percent:0.05}")
    private double taxaPercentual;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(String id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> buscarPorCliente(String clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public List<Pedido> buscarPorRevendedor(String revendedorId) {
        return pedidoRepository.findByVendedorId(revendedorId);
    }

    public List<Pedido> buscarPorStatus(PedidoStatus status) {
        return pedidoRepository.findByStatus(status);
    }

    @Transactional
    public Pedido criar(String clienteId, String revendedorId, Endereco enderecoEntrega) {
        Optional<Usuario> cliente = usuarioRepository.findById(clienteId);
        Optional<Revendedor> revendedor = revendedorRepository.findById(revendedorId);

        if (cliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado");
        }

        if (revendedor.isEmpty()) {
            throw new RuntimeException("Revendedor não encontrado");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente.get());
        pedido.setVendedor(revendedor.get());
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setEnderecoEntrega(enderecoEntrega);
        pedido.setStatus(PedidoStatus.PENDENTE);
        pedido.setValorTotal(0.0);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido adicionarItem(String pedidoId, String pecaId, int quantidade) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        Optional<Peca> pecaOpt = pecaRepository.findById(pecaId);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado");
        }

        if (pecaOpt.isEmpty()) {
            throw new RuntimeException("Peça não encontrada");
        }

        Pedido pedido = pedidoOpt.get();
        Peca peca = pecaOpt.get();

        if (!peca.estaDisponivel() || peca.getEstoque() < quantidade) {
            throw new RuntimeException("Peça indisponível ou estoque insuficiente");
        }

        ItemPedido item = new ItemPedido();
        item.setPeca(peca);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(peca.getPreco());

        pedido.adicionarItem(item);
        pedido.calcularTotal();

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido removerItem(String pedidoId, String itemId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado");
        }

        Pedido pedido = pedidoOpt.get();
        pedido.removerItem(itemId);
        pedido.calcularTotal();

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarStatus(String pedidoId, PedidoStatus novoStatus) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado");
        }

        Pedido pedido = pedidoOpt.get();
        pedido.atualizarStatus(novoStatus);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido confirmarPagamento(String pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado");
        }

        Pedido pedido = pedidoOpt.get();
        pedido.confirmarPagamento();
        pedido.setDataPagamento(LocalDateTime.now());

        if (pedido.getTaxaPlataforma() == null) {
            double taxa = pedido.getValorTotal() * taxaPercentual;
            taxa = Math.round(taxa * 100.0) / 100.0;
            pedido.setTaxaPlataforma(taxa);
            pedido.setValorLiquidoRevendedor(pedido.getValorTotal() - taxa);
            pedido.setTaxaPaga(false);

            Revendedor vendedor = pedido.getVendedor();
            if (vendedor != null) {
                Double saldoAtual = vendedor.getSaldoTaxas() != null ? vendedor.getSaldoTaxas() : 0.0;
                vendedor.setSaldoTaxas(saldoAtual + taxa);
                revendedorRepository.save(vendedor);
            }
        }

        // Atualizar estoque das peças
        for (ItemPedido item : pedido.getItens()) {
            Peca peca = item.getPeca();
            peca.alterarEstoque(-item.getQuantidade());
            pecaRepository.save(peca);
        }

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelarPedido(String pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado");
        }

        Pedido pedido = pedidoOpt.get();
        pedido.cancelarPedido();

        return pedidoRepository.save(pedido);
    }

    public double calcularTotal(String pedidoId) {
        Optional<Pedido> pedido = pedidoRepository.findById(pedidoId);

        if (pedido.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado");
        }

        return pedido.get().calcularTotal();
    }

    @Transactional
    public void deletar(String id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido não encontrado");
        }
        pedidoRepository.deleteById(id);
    }

    public Pedido criarPedido(String clienteId, String revendedorId, EnderecoDTO enderecoEntrega) {
        Endereco endereco = new Endereco();
        if (enderecoEntrega != null) {
            endereco.setRua(enderecoEntrega.getRua());
            endereco.setNumero(enderecoEntrega.getNumero());
            endereco.setComplemento(enderecoEntrega.getComplemento());
            endereco.setBairro(enderecoEntrega.getBairro());
            endereco.setCidade(enderecoEntrega.getCidade());
            endereco.setEstado(enderecoEntrega.getEstado());
            endereco.setCep(enderecoEntrega.getCep());
        }

        return criar(clienteId, revendedorId, endereco);
    }
}



