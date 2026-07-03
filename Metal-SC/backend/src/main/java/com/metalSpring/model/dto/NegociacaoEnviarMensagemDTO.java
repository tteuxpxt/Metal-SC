package com.metalSpring.model.dto;

public class NegociacaoEnviarMensagemDTO {
    private String remetenteId;
    private String conteudo;
    private Double valorProposto;

    public String getRemetenteId() { return remetenteId; }
    public void setRemetenteId(String remetenteId) { this.remetenteId = remetenteId; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public Double getValorProposto() { return valorProposto; }
    public void setValorProposto(Double valorProposto) { this.valorProposto = valorProposto; }
}
