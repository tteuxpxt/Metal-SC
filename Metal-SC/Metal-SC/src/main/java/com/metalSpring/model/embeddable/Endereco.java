package com.metalSpring.model.embeddable;

import jakarta.persistence.Embeddable;

@Embeddable
public class Endereco {

    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    public Endereco() {}

    public Endereco(String rua, String numero, String complemento, String bairro,
                    String cidade, String estado, String cep) {
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public String formatarEndereco() {
        StringBuilder sb = new StringBuilder();
        sb.append(rua).append(", ").append(numero);
        if (complemento != null && !complemento.isEmpty()) {
            sb.append(" - ").append(complemento);
        }
        sb.append(", ").append(bairro);
        sb.append(", ").append(cidade).append(" - ").append(estado);
        sb.append(", CEP: ").append(cep);
        return sb.toString();
    }

    public void atualizar(Endereco novoEndereco) {
        if (novoEndereco.getRua() != null) this.rua = novoEndereco.getRua();
        if (novoEndereco.getNumero() != null) this.numero = novoEndereco.getNumero();
        if (novoEndereco.getComplemento() != null) this.complemento = novoEndereco.getComplemento();
        if (novoEndereco.getBairro() != null) this.bairro = novoEndereco.getBairro();
        if (novoEndereco.getCidade() != null) this.cidade = novoEndereco.getCidade();
        if (novoEndereco.getEstado() != null) this.estado = novoEndereco.getEstado();
        if (novoEndereco.getCep() != null) this.cep = novoEndereco.getCep();
    }

    
    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
}