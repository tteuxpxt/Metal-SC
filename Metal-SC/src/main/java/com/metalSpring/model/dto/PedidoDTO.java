package com.metalSpring.model.dto;

import com.metalSpring.model.enums.PedidoStatus;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {
    private String id;
    private String clienteId;
    private String clienteNome;
    private String revendedorId;
    private String revendedorNome;
    private List<ItemPedidoDTO> itens;
    private Double valorTotal;
    private PedidoStatus status;
    private LocalDateTime dataCriacao;
    private EnderecoDTO enderecoEntrega;

    // Construtores
    public PedidoDTO() {}

    public PedidoDTO(String id, String clienteId, String revendedorId,
                     Double valorTotal, PedidoStatus status, LocalDateTime dataCriacao) {
        this.id = id;
        this.clienteId = clienteId;
        this.revendedorId = revendedorId;
        this.valorTotal = valorTotal;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getRevendedorId() { return revendedorId; }
    public void setRevendedorId(String revendedorId) { this.revendedorId = revendedorId; }

    public String getRevendedorNome() { return revendedorNome; }
    public void setRevendedorNome(String revendedorNome) { this.revendedorNome = revendedorNome; }

    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public PedidoStatus getStatus() { return status; }
    public void setStatus(PedidoStatus status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public EnderecoDTO getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(EnderecoDTO enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }
}
