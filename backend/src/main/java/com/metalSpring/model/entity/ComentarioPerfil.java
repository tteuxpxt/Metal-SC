package com.metalSpring.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comentarios_perfil")
public class ComentarioPerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne
    @JoinColumn(name = "alvo_id", nullable = false)
    private Usuario alvo;

    @Column(length = 1000)
    private String comentario;

    private Integer nota;

    @Column(nullable = false)
    private LocalDateTime data;

    public ComentarioPerfil() {
        this.data = LocalDateTime.now();
    }

    public ComentarioPerfil(Usuario autor, Usuario alvo, Integer nota, String comentario) {
        this();
        this.autor = autor;
        this.alvo = alvo;
        this.nota = nota;
        this.comentario = comentario;
    }

    public void editar(Integer nota, String comentario) {
        if (nota != null) {
            this.nota = nota;
        }
        if (comentario != null) {
            this.comentario = comentario;
        }
        this.data = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public Usuario getAlvo() { return alvo; }
    public void setAlvo(Usuario alvo) { this.alvo = alvo; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComentarioPerfil that = (ComentarioPerfil) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
