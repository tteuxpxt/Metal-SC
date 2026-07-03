package com.metalSpring.model.dto;

import com.metalSpring.model.enums.PecaEstado;

public class PecaDTO {
    private String nome;
    private String descricao;
    private String categoria;
    private Double preco;
    private PecaEstado estado;
    private Integer ano;
    private String marca;
    private String modeloVeiculo;
    private Integer estoque;
    private String revendedorId;
    private EnderecoDTO endereco;

    public PecaDTO() {}

    public PecaDTO(String nome, String descricao, String categoria, Double preco,
                   PecaEstado estado, Integer ano, String marca, String modeloVeiculo,
                   Integer estoque, String revendedorId, EnderecoDTO endereco) {
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.estado = estado;
        this.ano = ano;
        this.marca = marca;
        this.modeloVeiculo = modeloVeiculo;
        this.estoque = estoque;
        this.revendedorId = revendedorId;
        this.endereco = endereco;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

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

    public EnderecoDTO getEndereco() { return endereco; }
    public void setEndereco(EnderecoDTO endereco) { this.endereco = endereco; }
}
