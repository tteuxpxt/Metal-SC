package com.metalSpring.model.dto;

public class ItemPedidoDTO {
    private String id;
    private String pecaId;
    private String pecaNome;
    private Integer quantidade;
    private Double precoUnitario;
    private Double subtotal;
    private String pedidoid;

    // Construtores
    public ItemPedidoDTO() {}

    public ItemPedidoDTO(String pecaId, Integer quantidade, Double precoUnitario) {
        this.pecaId = pecaId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = quantidade * precoUnitario;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPedidoId() { return pedidoid; }
    public void setPedidoId(String pedidoid) { this.pedidoid = pedidoid; }

    public String getPecaId() { return pecaId; }
    public void setPecaId(String pecaId) { this.pecaId = pecaId; }

    public String getPecaNome() { return pecaNome; }
    public void setPecaNome(String pecaNome) { this.pecaNome = pecaNome; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        calcularSubtotal();
    }

    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
        calcularSubtotal();
    }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    private void calcularSubtotal() {
        if (quantidade != null && precoUnitario != null) {
            this.subtotal = quantidade * precoUnitario;
        }
    }

}
