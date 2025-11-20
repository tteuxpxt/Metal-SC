package com.metalSpring.model.entity;

import com.metalSpring.model.enums.PedidoStatus;
import com.metalSpring.model.embeddable.Endereco;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Revendedor vendedor;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(nullable = false)
    private Double valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PedidoStatus status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Embedded
    private Endereco enderecoEntrega;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Transacao transacao;

    public Pedido() {
        this.dataCriacao = LocalDateTime.now();
        this.status = PedidoStatus.PENDENTE;
        this.valorTotal = 0.0;
    }

    public Pedido(Usuario cliente, Revendedor vendedor, Endereco enderecoEntrega) {
        this();
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.enderecoEntrega = enderecoEntrega;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
        calcularTotal();
    }

    public void removerItem(String idPeca) {
        itens.removeIf(item -> item.getPeca().getId().equals(idPeca));
        calcularTotal();
    }

    public double calcularTotal() {
        this.valorTotal = itens.stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();
        return 0;
    }

    public void atualizarStatus(PedidoStatus novoStatus) {
        if (this.status == PedidoStatus.CANCELADO) {
            throw new IllegalStateException("Pedido cancelado não pode ter status alterado");
        }
        this.status = novoStatus;
    }

    public void confirmarPagamento() {
        if (this.status != PedidoStatus.PENDENTE) {
            throw new IllegalStateException("Apenas pedidos pendentes podem ter pagamento confirmado");
        }
        this.status = PedidoStatus.CONFIRMADO;
    }

    public void cancelarPedido() {
        if (this.status == PedidoStatus.ENTREGUE) {
            throw new IllegalStateException("Pedidos entregues não podem ser cancelados");
        }
        this.status = PedidoStatus.CANCELADO;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public Revendedor getVendedor() { return vendedor; }
    public void setVendedor(Revendedor vendedor) { this.vendedor = vendedor; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public PedidoStatus getStatus() { return status; }
    public void setStatus(PedidoStatus status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Endereco getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(Endereco enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }

    public Transacao getTransacao() { return transacao; }
    public void setTransacao(Transacao transacao) { this.transacao = transacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}