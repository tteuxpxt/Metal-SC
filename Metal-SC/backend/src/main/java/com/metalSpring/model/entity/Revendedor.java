package com.metalSpring.model.entity;

import com.metalSpring.model.enums.UsuarioTipo;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("REVENDEDOR")
public class Revendedor extends Usuario {

    
    @Column(unique = true)  
    private String cnpj;

    @Column  
    private String nomeLoja;

    private Double avaliacaoMedia;

    @Column(nullable = false)
    private Double saldoTaxas = 0.0;

    @Column(nullable = false)
    private Boolean premiumAtivo = false;

    private LocalDateTime premiumAte;

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Peca> pecas = new ArrayList<>();

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    public Revendedor() {
        super();
        this.avaliacaoMedia = 0.0;
    }

    public Revendedor(String nome, String email, String senhaHash, String telefone,
                      String cnpj, String nomeLoja) {
        super(nome, email, senhaHash, telefone, UsuarioTipo.REVENDEDOR);
        this.cnpj = cnpj;
        this.nomeLoja = nomeLoja;
        this.avaliacaoMedia = 0.0;
    }

    public void adicionarPeca(Peca peca) {
        pecas.add(peca);
        peca.setVendedor(this);
    }

    public void removerPeca(String idPeca) {
        pecas.removeIf(peca -> peca.getId().equals(idPeca));
    }

    public void atualizarEstoque(String idPeca, int quantidade) {
        Peca peca = pecas.stream()
                .filter(p -> p.getId().equals(idPeca))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));
        peca.alterarEstoque(quantidade);
    }

    public double calcularAvaliacaoMedia() {
        if (avaliacoes.isEmpty()) {
            this.avaliacaoMedia = 0.0;
            return 0;
        }
        double soma = avaliacoes.stream()
                .mapToInt(Avaliacao::getNota)
                .sum();
        this.avaliacaoMedia = soma / avaliacoes.size();
        return soma;
    }

    
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getNomeLoja() { return nomeLoja; }
    public void setNomeLoja(String nomeLoja) { this.nomeLoja = nomeLoja; }

    public Double getAvaliacaoMedia() { return avaliacaoMedia; }
    public void setAvaliacaoMedia(Double avaliacaoMedia) { this.avaliacaoMedia = avaliacaoMedia; }

    public Double getSaldoTaxas() { return saldoTaxas; }
    public void setSaldoTaxas(Double saldoTaxas) { this.saldoTaxas = saldoTaxas; }

    public Boolean getPremiumAtivo() { return premiumAtivo; }
    public void setPremiumAtivo(Boolean premiumAtivo) { this.premiumAtivo = premiumAtivo; }

    public LocalDateTime getPremiumAte() { return premiumAte; }
    public void setPremiumAte(LocalDateTime premiumAte) { this.premiumAte = premiumAte; }

    public List<Peca> getPecas() { return pecas; }
    public void setPecas(List<Peca> pecas) { this.pecas = pecas; }

    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<Avaliacao> avaliacoes) { this.avaliacoes = avaliacoes; }
}
