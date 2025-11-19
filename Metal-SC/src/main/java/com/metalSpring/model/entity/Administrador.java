package com.metalSpring.model.entity;

import com.metalSpring.model.enums.UsuarioTipo;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    public Administrador() {
        super();
    }

    public Administrador(String nome, String email, String senhaHash, String telefone) {
        super(nome, email, senhaHash, telefone, UsuarioTipo.ADMINISTRADOR);
    }

    public void bloquearUsuario(String usuarioId) {
        System.out.println("Bloqueando usuário: " + usuarioId);
    }

    public void removerPeca(String pecaId) {
        System.out.println("Removendo peça: " + pecaId);
    }

    public void aprovarRevendedor(String revendedorId) {
        System.out.println("Aprovando revendedor: " + revendedorId);
    }

    public void visualizarRelatorios() {
        System.out.println("Visualizando relatórios");
    }
}