package com.metalSpring.model.entity;

import com.metalSpring.model.enums.PedidoStatus;
import com.metalSpring.model.enums.PagamentoStatus;
import com.metalSpring.model.embeddable.Endereco;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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

    private Double taxaPlataforma;

    private Double valorLiquidoRevendedor;

    @Column(nullable = false)
    private Boolean taxaPaga = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50)")
    private PedidoStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50)")
    private PagamentoStatus statusPagamento;

    private Boolean aprovacaoCliente = false;

    private Boolean aprovacaoRevendedor = false;

    private LocalDateTime dataAprovacaoCliente;

    private LocalDateTime dataAprovacaoRevendedor;

    private Double valorFinalNegociado;

    private LocalDateTime dataPagamentoInformadoCliente;

    private LocalDateTime dataPagamentoConfirmado;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataPagamento;

    @Embedded
    private Endereco enderecoEntrega;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Transacao transacao;

    @OneToOne(mappedBy = "pedido")
    private ConversaNegociacao conversaNegociacao;

    @ElementCollection
    @CollectionTable(name = "pedido_eventos", joinColumns = @JoinColumn(name = "pedido_id"))
    @Column(name = "evento", length = 1000)
    private List<String> historicoEventos = new ArrayList<>();

    public Pedido() {
        this.dataCriacao = LocalDateTime.now();
        this.status = PedidoStatus.AGUARDANDO_NEGOCIACAO;
        this.statusPagamento = PagamentoStatus.BLOQUEADO_AGUARDANDO_NEGOCIACAO;
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
        return this.valorTotal;
    }

    public void atualizarStatus(PedidoStatus novoStatus) {
        if (this.status == PedidoStatus.CANCELADO) {
            throw new IllegalStateException("Pedido cancelado não pode ter status alterado");
        }
        this.status = novoStatus;
    }

    public void confirmarPagamento() {
        if (this.status != PedidoStatus.PAGAMENTO_INFORMADO_CLIENTE) {
            throw new IllegalStateException("Pagamento so pode ser confirmado depois do cliente informar pagamento");
        }
        this.status = PedidoStatus.EM_SEPARACAO;
        this.statusPagamento = PagamentoStatus.PAGAMENTO_CONFIRMADO;
    }

    public void cancelarPedido() {
        if (this.status == PedidoStatus.ENTREGUE) {
            throw new IllegalStateException("Pedidos entregues não podem ser cancelados");
        }
        this.status = PedidoStatus.CANCELADO;
    }

    
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

    public Double getTaxaPlataforma() { return taxaPlataforma; }
    public void setTaxaPlataforma(Double taxaPlataforma) { this.taxaPlataforma = taxaPlataforma; }

    public Double getValorLiquidoRevendedor() { return valorLiquidoRevendedor; }
    public void setValorLiquidoRevendedor(Double valorLiquidoRevendedor) { this.valorLiquidoRevendedor = valorLiquidoRevendedor; }

    public Boolean getTaxaPaga() { return taxaPaga; }
    public void setTaxaPaga(Boolean taxaPaga) { this.taxaPaga = taxaPaga; }

    public PedidoStatus getStatus() { return status; }
    public void setStatus(PedidoStatus status) { this.status = status; }

    public PagamentoStatus getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(PagamentoStatus statusPagamento) { this.statusPagamento = statusPagamento; }

    public Boolean getAprovacaoCliente() { return aprovacaoCliente; }
    public void setAprovacaoCliente(Boolean aprovacaoCliente) { this.aprovacaoCliente = aprovacaoCliente; }

    public Boolean getAprovacaoRevendedor() { return aprovacaoRevendedor; }
    public void setAprovacaoRevendedor(Boolean aprovacaoRevendedor) { this.aprovacaoRevendedor = aprovacaoRevendedor; }

    public LocalDateTime getDataAprovacaoCliente() { return dataAprovacaoCliente; }
    public void setDataAprovacaoCliente(LocalDateTime dataAprovacaoCliente) { this.dataAprovacaoCliente = dataAprovacaoCliente; }

    public LocalDateTime getDataAprovacaoRevendedor() { return dataAprovacaoRevendedor; }
    public void setDataAprovacaoRevendedor(LocalDateTime dataAprovacaoRevendedor) { this.dataAprovacaoRevendedor = dataAprovacaoRevendedor; }

    public Double getValorFinalNegociado() { return valorFinalNegociado; }
    public void setValorFinalNegociado(Double valorFinalNegociado) { this.valorFinalNegociado = valorFinalNegociado; }

    public LocalDateTime getDataPagamentoInformadoCliente() { return dataPagamentoInformadoCliente; }
    public void setDataPagamentoInformadoCliente(LocalDateTime dataPagamentoInformadoCliente) { this.dataPagamentoInformadoCliente = dataPagamentoInformadoCliente; }

    public LocalDateTime getDataPagamentoConfirmado() { return dataPagamentoConfirmado; }
    public void setDataPagamentoConfirmado(LocalDateTime dataPagamentoConfirmado) { this.dataPagamentoConfirmado = dataPagamentoConfirmado; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public Endereco getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(Endereco enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }

    public Transacao getTransacao() { return transacao; }
    public void setTransacao(Transacao transacao) { this.transacao = transacao; }

    public ConversaNegociacao getConversaNegociacao() { return conversaNegociacao; }
    public void setConversaNegociacao(ConversaNegociacao conversaNegociacao) { this.conversaNegociacao = conversaNegociacao; }

    public List<String> getHistoricoEventos() { return historicoEventos; }
    public void setHistoricoEventos(List<String> historicoEventos) { this.historicoEventos = historicoEventos; }

    public void registrarEvento(String evento) {
        if (this.historicoEventos == null) {
            this.historicoEventos = new ArrayList<>();
        }
        this.historicoEventos.add(LocalDateTime.now() + " - " + evento);
    }

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
