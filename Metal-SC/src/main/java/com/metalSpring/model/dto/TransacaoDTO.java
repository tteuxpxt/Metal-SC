package com.metalSpring.model.dto;

import com.metalSpring.model.enums.MetodoPagamento;
import com.metalSpring.model.enums.TransacaoStatus;
import java.time.LocalDateTime;

public class TransacaoDTO {
    private String id;
    private String pedidoId;
    private LocalDateTime data;
    private MetodoPagamento metodo;
    private TransacaoStatus status;
    private String referencia;

    // Construtores
    public TransacaoDTO() {}

    public TransacaoDTO(String id, String pedidoId, LocalDateTime data,
                        MetodoPagamento metodo, TransacaoStatus status) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.data = data;
        this.metodo = metodo;
        this.status = status;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPedidoId() { return pedidoId; }
    public void setPedidoId(String pedidoId) { this.pedidoId = pedidoId; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public MetodoPagamento getMetodo() { return metodo; }
    public void setMetodo(MetodoPagamento metodo) { this.metodo = metodo; }

    public TransacaoStatus getStatus() { return status; }
    public void setStatus(TransacaoStatus status) { this.status = status; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}