package com.metalSpring.model.entity;

import com.metalSpring.model.enums.NegociacaoStatus;
import com.metalSpring.model.enums.TipoMensagemNegociacao;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "mensagens_negociacao")
public class MensagemNegociacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "conversa_id", nullable = false)
    private ConversaNegociacao conversa;

    @ManyToOne
    @JoinColumn(name = "remetente_id", nullable = false)
    private Usuario remetente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    @Column(nullable = false, length = 2000)
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime dataEnvio;

    @Column(nullable = false)
    private Boolean lida;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50)")
    private NegociacaoStatus statusNegociacao;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50)")
    private TipoMensagemNegociacao tipo;

    private Double valorProposto;

    @Column(nullable = false)
    private Boolean removida;

    public MensagemNegociacao() {
        this.dataEnvio = LocalDateTime.now();
        this.lida = false;
        this.tipo = TipoMensagemNegociacao.TEXTO;
        this.removida = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ConversaNegociacao getConversa() { return conversa; }
    public void setConversa(ConversaNegociacao conversa) { this.conversa = conversa; }

    public Usuario getRemetente() { return remetente; }
    public void setRemetente(Usuario remetente) { this.remetente = remetente; }

    public Usuario getDestinatario() { return destinatario; }
    public void setDestinatario(Usuario destinatario) { this.destinatario = destinatario; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public Boolean getLida() { return lida; }
    public void setLida(Boolean lida) { this.lida = lida; }

    public NegociacaoStatus getStatusNegociacao() { return statusNegociacao; }
    public void setStatusNegociacao(NegociacaoStatus statusNegociacao) { this.statusNegociacao = statusNegociacao; }

    public TipoMensagemNegociacao getTipo() { return tipo; }
    public void setTipo(TipoMensagemNegociacao tipo) { this.tipo = tipo; }

    public Double getValorProposto() { return valorProposto; }
    public void setValorProposto(Double valorProposto) { this.valorProposto = valorProposto; }

    public Boolean getRemovida() { return removida; }
    public void setRemovida(Boolean removida) { this.removida = removida; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MensagemNegociacao that = (MensagemNegociacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
