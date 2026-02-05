package com.metalSpring.model.entity;

import com.metalSpring.model.enums.UsuarioTipo;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    public Cliente() {
        super();
        this.setTipo(UsuarioTipo.CLIENTE);
    }

    public Cliente(String nome, String email, String senhaHash, String telefone) {
        super(nome, email, senhaHash, telefone, UsuarioTipo.CLIENTE);
    }

    
    public void adicionarPedido(Pedido pedido) {
        pedidos.add(pedido);
    }

    public void adicionarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(avaliacao);
    }

    public int getTotalPedidos() {
        return pedidos.size();
    }

    public double getValorTotalGasto() {
        return pedidos.stream()
                .filter(p -> p.getStatus() != null)
                .mapToDouble(Pedido::getValorTotal)
                .sum();
    }

    
    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }

    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<Avaliacao> avaliacoes) { this.avaliacoes = avaliacoes; }
}