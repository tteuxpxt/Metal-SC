package com.metalSpring.model.dto;

public class NegociacaoCriarDTO {
    private String pecaId;
    private String clienteId;
    private String conteudo;
    private Double valorProposto;

    public String getPecaId() { return pecaId; }
    public void setPecaId(String pecaId) { this.pecaId = pecaId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public Double getValorProposto() { return valorProposto; }
    public void setValorProposto(Double valorProposto) { this.valorProposto = valorProposto; }
}
