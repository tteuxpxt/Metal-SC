package com.metalSpring.model.dto;

import com.metalSpring.model.enums.UsuarioTipo;
import java.time.LocalDateTime;

public class UsuarioDTO {
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private UsuarioTipo tipo;
    private boolean ativo;
    private LocalDateTime dataCadastro;
    private EnderecoDTO endereco;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String id, String nome, String email, String telefone,
                      UsuarioTipo tipo, boolean ativo, LocalDateTime dataCadastro, EnderecoDTO endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.tipo = tipo;
        this.ativo = ativo;
        this.dataCadastro = dataCadastro;
        this.endereco = endereco;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public UsuarioTipo getTipo() {
        return tipo;
    }

    public void setTipo(UsuarioTipo tipo) {
        this.tipo = tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }
}

