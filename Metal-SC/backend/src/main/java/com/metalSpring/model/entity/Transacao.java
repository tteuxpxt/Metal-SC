package com.metalSpring.model.entity;

import com.metalSpring.model.enums.MetodoPagamento;
import com.metalSpring.model.enums.TransacaoStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false)
    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransacaoStatus status;

    private String referencia;

    public Transacao() {
        this.data = LocalDateTime.now();
        this.status = TransacaoStatus.PENDENTE;
    }

    public Transacao(Pedido pedido, MetodoPagamento metodo, String referencia) {
        this();
        this.pedido = pedido;
        this.metodo = metodo;
        this.referencia = referencia;
    }

    public void processarPagamento() {
        if (this.status != TransacaoStatus.PENDENTE) {
            throw new IllegalStateException("Apenas transações pendentes podem ser processadas");
        }
        this.status = TransacaoStatus.PROCESSANDO;
    }

    public void confirmar() {
        if (this.status != TransacaoStatus.PROCESSANDO) {
            throw new IllegalStateException("Apenas transações em processamento podem ser confirmadas");
        }
        this.status = TransacaoStatus.CONFIRMADA;
        pedido.confirmarPagamento();
    }

    public void recusar(String motivo) {
        if (this.status == TransacaoStatus.CONFIRMADA || this.status == TransacaoStatus.ESTORNADA) {
            throw new IllegalStateException("Transação não pode ser recusada neste estado");
        }
        this.status = TransacaoStatus.RECUSADA;
        System.out.println("Transação recusada: " + motivo);
    }

    public void estornar() {
        if (this.status != TransacaoStatus.CONFIRMADA) {
            throw new IllegalStateException("Apenas transações confirmadas podem ser estornadas");
        }
        this.status = TransacaoStatus.ESTORNADA;
    }

    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public MetodoPagamento getMetodo() { return metodo; }
    public void setMetodo(MetodoPagamento metodo) { this.metodo = metodo; }

    public TransacaoStatus getStatus() { return status; }
    public void setStatus(TransacaoStatus status) { this.status = status; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        return Objects.equals(id, transacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}