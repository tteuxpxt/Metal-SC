package com.metalSpring.model.dto;

import com.metalSpring.model.enums.NegociacaoStatus;
import com.metalSpring.model.enums.TipoMensagemNegociacao;
import java.time.LocalDateTime;

public class NegociacaoMensagemDTO {
    private String id;
    private String conversaId;
    private String remetenteId;
    private String remetenteNome;
    private String destinatarioId;
    private String destinatarioNome;
    private String conteudo;
    private LocalDateTime dataEnvio;
    private Boolean lida;
    private NegociacaoStatus statusNegociacao;
    private TipoMensagemNegociacao tipo;
    private Double valorProposto;
    private Boolean removida;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConversaId() { return conversaId; }
    public void setConversaId(String conversaId) { this.conversaId = conversaId; }
    public String getRemetenteId() { return remetenteId; }
    public void setRemetenteId(String remetenteId) { this.remetenteId = remetenteId; }
    public String getRemetenteNome() { return remetenteNome; }
    public void setRemetenteNome(String remetenteNome) { this.remetenteNome = remetenteNome; }
    public String getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(String destinatarioId) { this.destinatarioId = destinatarioId; }
    public String getDestinatarioNome() { return destinatarioNome; }
    public void setDestinatarioNome(String destinatarioNome) { this.destinatarioNome = destinatarioNome; }
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
}
