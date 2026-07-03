package com.metalSpring.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "avaliacoes")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Revendedor vendedor;

    @ManyToOne
    @JoinColumn(name = "peca_id")
    private Peca peca;

    @Column(nullable = false)
    private Integer nota;

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime data;

    
    public Avaliacao() {
        this.data = LocalDateTime.now();
    }

    public Avaliacao(Usuario cliente, Revendedor vendedor, Peca peca, Integer nota, String comentario) {
        this();
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.peca = peca;
        this.nota = nota;
        this.comentario = comentario;
    }

    
    public void editarAvaliacao(Integer nota, String comentario) {
        if (nota != null && nota >= 1 && nota <= 5) {
            this.nota = nota;
        }
        if (comentario != null) {
            this.comentario = comentario;
        }
        this.data = LocalDateTime.now();
    }

    public void excluir() {
        System.out.println("Avaliação excluída: " + this.id);
    }

    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public Revendedor getVendedor() { return vendedor; }
    public void setVendedor(Revendedor vendedor) { this.vendedor = vendedor; }

    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }

    public Integer getNota() { return nota; }  
    public void setNota(Integer nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avaliacao avaliacao = (Avaliacao) o;
        return Objects.equals(id, avaliacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}