// ==================== AvaliacaoDTO.java ====================
package com.metalSpring.model.dto;

import java.time.LocalDateTime;

public class AvaliacaoDTO {
    private String id;
    private String clienteId;
    private String clienteNome;
    private String revendedorId;
    private String revendedorNome;
    private String pecaId;
    private String pecaNome;
    private Integer nota;
    private String comentario;
    private LocalDateTime data;

    // Construtores
    public AvaliacaoDTO() {}

    public AvaliacaoDTO(String clienteId, String revendedorId, String pecaId,
                        Integer nota, String comentario) {
        this.clienteId = clienteId;
        this.revendedorId = revendedorId;
        this.pecaId = pecaId;
        this.nota = nota;
        this.comentario = comentario;
        this.data = LocalDateTime.now();
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getRevendedorId() {
        return revendedorId;
    }

    public void setRevendedorId(String revendedorId) {
        this.revendedorId = revendedorId;
    }

    public String getRevendedorNome() {
        return revendedorNome;
    }

    public void setRevendedorNome(String revendedorNome) {
        this.revendedorNome = revendedorNome;
    }

    public String getPecaId() {
        return pecaId;
    }

    public void setPecaId(String pecaId) {
        this.pecaId = pecaId;
    }

    public String getPecaNome() {
        return pecaNome;
    }

    public void setPecaNome(String pecaNome) {
        this.pecaNome = pecaNome;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
