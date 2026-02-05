package com.metalSpring.model.dto;

import com.metalSpring.model.enums.UsuarioTipo;
import java.time.LocalDateTime;

public class RevendedorAdminDTO {
    private String id;
    private String nome;
    private String email;
    private UsuarioTipo tipo;
    private boolean ativo;
    private Double saldoTaxas;
    private Boolean premiumAtivo;
    private LocalDateTime premiumAte;
    private LocalDateTime dataCadastro;

    public RevendedorAdminDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UsuarioTipo getTipo() { return tipo; }
    public void setTipo(UsuarioTipo tipo) { this.tipo = tipo; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Double getSaldoTaxas() { return saldoTaxas; }
    public void setSaldoTaxas(Double saldoTaxas) { this.saldoTaxas = saldoTaxas; }

    public Boolean getPremiumAtivo() { return premiumAtivo; }
    public void setPremiumAtivo(Boolean premiumAtivo) { this.premiumAtivo = premiumAtivo; }

    public LocalDateTime getPremiumAte() { return premiumAte; }
    public void setPremiumAte(LocalDateTime premiumAte) { this.premiumAte = premiumAte; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}
