package com.metalSpring.model.entity;

import com.metalSpring.model.enums.UsuarioTipo;
import com.metalSpring.model.embeddable.Endereco;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public class Usuario {

    private boolean ativo = true;

    public boolean isAtivo() { return ativo; }

    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    private String telefone;

    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsuarioTipo tipo;

    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @Embedded
    private Endereco endereco;

    public Usuario() {
        this.dataCadastro = LocalDateTime.now();
    }

    public Usuario(String nome, String email, String senhaHash, String telefone, UsuarioTipo tipo) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.telefone = telefone;
        this.tipo = tipo;
        this.dataCadastro = LocalDateTime.now();
    }

    public boolean validarSenha(String senha) {
        return this.senhaHash.equals(senha);
    }

    public void alterarSenha(String senhaAtual, String novaSenha) {
        if (validarSenha(senhaAtual)) {
            this.senhaHash = novaSenha;
        } else {
            throw new IllegalArgumentException("Senha atual incorreta");
        }
    }

    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public UsuarioTipo getTipo() { return tipo; }
    public void setTipo(UsuarioTipo tipo) { this.tipo = tipo; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
