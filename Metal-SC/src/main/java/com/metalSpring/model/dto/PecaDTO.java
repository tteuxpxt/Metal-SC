package com.metalSpring.model.dto;

import com.metalSpring.model.enums.PecaEstado;
import java.util.List;

public class PecaDTO {
    private String id;
    private String nome;
    private String descricao;
    private String categoria;
    private Double preco;
    private List<String> imagens;
    private PecaEstado estado;
    private Integer ano;
    private String marca;
    private String modeloVeiculo;
    private Integer estoque;
    private String revendedorId;
    private String revendedorNome;

    // Construtores
    public PecaDTO() {}

    public PecaDTO(String id, String nome, String descricao, String categoria,
                   Double preco, PecaEstado estado, Integer ano, String marca,
                   String modeloVeiculo, Integer estoque) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.estado = estado;
        this.ano = ano;
        this.marca = marca;
        this.modeloVeiculo = modeloVeiculo;
        this.estoque = estoque;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public List<String> getImagens() { return imagens; }
    public void setImagens(List<String> imagens) { this.imagens = imagens; }

    public PecaEstado getEstado() { return estado; }
    public void setEstado(PecaEstado estado) { this.estado = estado; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModeloVeiculo() { return modeloVeiculo; }
    public void setModeloVeiculo(String modeloVeiculo) { this.modeloVeiculo = modeloVeiculo; }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }

    public String getRevendedorId() { return revendedorId; }
    public void setRevendedorId(String revendedorId) { this.revendedorId = revendedorId; }

    public String getRevendedorNome() { return revendedorNome; }
    public void setRevendedorNome(String revendedorNome) { this.revendedorNome = revendedorNome; }
}