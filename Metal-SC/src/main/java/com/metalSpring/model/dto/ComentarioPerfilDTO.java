package com.metalSpring.model.dto;

import java.time.LocalDateTime;

public class ComentarioPerfilDTO {
    private String id;
    private String autorId;
    private String autorNome;
    private String alvoId;
    private String alvoNome;
    private Integer nota;
    private String comentario;
    private LocalDateTime data;

    public ComentarioPerfilDTO() {}

    public ComentarioPerfilDTO(String autorId, String alvoId, Integer nota, String comentario) {
        this.autorId = autorId;
        this.alvoId = alvoId;
        this.nota = nota;
        this.comentario = comentario;
        this.data = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutorId() {
        return autorId;
    }

    public void setAutorId(String autorId) {
        this.autorId = autorId;
    }

    public String getAutorNome() {
        return autorNome;
    }

    public void setAutorNome(String autorNome) {
        this.autorNome = autorNome;
    }

    public String getAlvoId() {
        return alvoId;
    }

    public void setAlvoId(String alvoId) {
        this.alvoId = alvoId;
    }

    public String getAlvoNome() {
        return alvoNome;
    }

    public void setAlvoNome(String alvoNome) {
        this.alvoNome = alvoNome;
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
