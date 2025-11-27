package com.metalSpring.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.metalSpring.model.enums.PecaEstado;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pecas")
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Double preco;

    @ElementCollection
    @CollectionTable(name = "peca_imagens", joinColumns = @JoinColumn(name = "peca_id"))
    @Column(name = "url_imagem")
    private List<String> imagens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PecaEstado estado;

    private Integer ano;

    private String marca;

    @Column(name = "modelo_veiculo")
    private String modeloVeiculo;

    // ✅ JsonBackReference: Evita loop infinito na serialização
    // Esta é a parte "filha" da relação - não será serializada quando vindo do Revendedor
    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    @JsonBackReference("revendedor-pecas")
    private Revendedor vendedor;

    @Column(nullable = false)
    private Integer estoque;

    // ✅ JsonManagedReference: Esta é a parte "pai" da relação com Avaliacao
    // As avaliações SERÃO serializadas quando buscarmos uma Peca
    @OneToMany(mappedBy = "peca", cascade = CascadeType.ALL)
    @JsonManagedReference("peca-avaliacoes")
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    private boolean disponivel = true;

    // ==================== CONSTRUTORES ====================

    public Peca() {}

    public Peca(String nome, String descricao, String categoria, Double preco,
                PecaEstado estado, Integer ano, String marca, String modeloVeiculo,
                Revendedor vendedor, Integer estoque) {
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.estado = estado;
        this.ano = ano;
        this.marca = marca;
        this.modeloVeiculo = modeloVeiculo;
        this.vendedor = vendedor;
        this.estoque = estoque;
    }

    // ==================== MÉTODOS DE NEGÓCIO ====================

    public void adicionarImagem(String url) {
        if (!imagens.contains(url)) {
            imagens.add(url);
        }
    }

    public void removerImagem(String url) {
        imagens.remove(url);
    }

    public void alterarEstoque(int quantidade) {
        this.estoque += quantidade;
        if (this.estoque < 0) {
            throw new IllegalStateException("Estoque não pode ser negativo");
        }
    }

    public boolean estaDisponivel() {
        return this.estoque > 0;
    }

    // ==================== GETTERS E SETTERS ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public List<String> getImagens() {
        return imagens;
    }

    public void setImagens(List<String> imagens) {
        this.imagens = imagens;
    }

    public PecaEstado getEstado() {
        return estado;
    }

    public void setEstado(PecaEstado estado) {
        this.estado = estado;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModeloVeiculo() {
        return modeloVeiculo;
    }

    public void setModeloVeiculo(String modeloVeiculo) {
        this.modeloVeiculo = modeloVeiculo;
    }

    public Revendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Revendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    // ==================== EQUALS E HASHCODE ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peca peca = (Peca) o;
        return Objects.equals(id, peca.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}