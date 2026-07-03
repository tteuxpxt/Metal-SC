package com.metalSpring.model.entity;

import com.metalSpring.model.enums.NegociacaoStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "conversas_negociacao")
public class ConversaNegociacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;

    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "revendedor_id", nullable = false)
    private Revendedor revendedor;

    @Column(nullable = false)
    private Double valorOriginal;

    private Double valorNegociado;

    private Double valorFinalAcordado;

    private Boolean aprovacaoCliente = false;

    private Boolean aprovacaoRevendedor = false;

    private LocalDateTime dataAprovacaoCliente;

    private LocalDateTime dataAprovacaoRevendedor;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50)")
    private NegociacaoStatus status;

    @Column(nullable = false)
    private LocalDateTime criadaEm;

    @Column(nullable = false)
    private LocalDateTime atualizadaEm;

    @OneToMany(mappedBy = "conversa", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataEnvio ASC")
    private List<MensagemNegociacao> mensagens = new ArrayList<>();

    public ConversaNegociacao() {
        this.status = NegociacaoStatus.EM_NEGOCIACAO;
        this.criadaEm = LocalDateTime.now();
        this.atualizadaEm = LocalDateTime.now();
    }

    public void tocar() {
        this.atualizadaEm = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Revendedor getRevendedor() { return revendedor; }
    public void setRevendedor(Revendedor revendedor) { this.revendedor = revendedor; }

    public Double getValorOriginal() { return valorOriginal; }
    public void setValorOriginal(Double valorOriginal) { this.valorOriginal = valorOriginal; }

    public Double getValorNegociado() { return valorNegociado; }
    public void setValorNegociado(Double valorNegociado) { this.valorNegociado = valorNegociado; }

    public Double getValorFinalAcordado() { return valorFinalAcordado; }
    public void setValorFinalAcordado(Double valorFinalAcordado) { this.valorFinalAcordado = valorFinalAcordado; }

    public Boolean getAprovacaoCliente() { return aprovacaoCliente; }
    public void setAprovacaoCliente(Boolean aprovacaoCliente) { this.aprovacaoCliente = aprovacaoCliente; }

    public Boolean getAprovacaoRevendedor() { return aprovacaoRevendedor; }
    public void setAprovacaoRevendedor(Boolean aprovacaoRevendedor) { this.aprovacaoRevendedor = aprovacaoRevendedor; }

    public LocalDateTime getDataAprovacaoCliente() { return dataAprovacaoCliente; }
    public void setDataAprovacaoCliente(LocalDateTime dataAprovacaoCliente) { this.dataAprovacaoCliente = dataAprovacaoCliente; }

    public LocalDateTime getDataAprovacaoRevendedor() { return dataAprovacaoRevendedor; }
    public void setDataAprovacaoRevendedor(LocalDateTime dataAprovacaoRevendedor) { this.dataAprovacaoRevendedor = dataAprovacaoRevendedor; }

    public NegociacaoStatus getStatus() { return status; }
    public void setStatus(NegociacaoStatus status) { this.status = status; }

    public LocalDateTime getCriadaEm() { return criadaEm; }
    public void setCriadaEm(LocalDateTime criadaEm) { this.criadaEm = criadaEm; }

    public LocalDateTime getAtualizadaEm() { return atualizadaEm; }
    public void setAtualizadaEm(LocalDateTime atualizadaEm) { this.atualizadaEm = atualizadaEm; }

    public List<MensagemNegociacao> getMensagens() { return mensagens; }
    public void setMensagens(List<MensagemNegociacao> mensagens) { this.mensagens = mensagens; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversaNegociacao that = (ConversaNegociacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
