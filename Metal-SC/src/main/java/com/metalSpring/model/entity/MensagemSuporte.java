package com.metalSpring.model.entity;

import com.metalSpring.model.enums.CanalSuporte;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "mensagens_suporte")
public class MensagemSuporte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 2000)
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime dataEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalSuporte canal;

    @Column(nullable = false)
    private Boolean lida;

    private String atendenteResponsavel;

    
    public MensagemSuporte() {
        this.dataEnvio = LocalDateTime.now();
        this.lida = false;
    }

    public MensagemSuporte(Usuario usuario, String conteudo, CanalSuporte canal) {
        this();
        this.usuario = usuario;
        this.conteudo = conteudo;
        this.canal = canal;
    }

    
    public void marcarComoLida() {
        this.lida = true;
    }

    public void responder(String texto, String idAtendente) {
        this.atendenteResponsavel = idAtendente;
        this.lida = true;
        System.out.println("Resposta enviada: " + texto);
    }

    public void encaminharParaAtendente(String idAtendente) {
        this.atendenteResponsavel = idAtendente;
        System.out.println("Mensagem encaminhada para atendente: " + idAtendente);
    }

    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public CanalSuporte getCanal() { return canal; }
    public void setCanal(CanalSuporte canal) { this.canal = canal; }

    public Boolean getLida() { return lida; }
    public void setLida(Boolean lida) { this.lida = lida; }

    public String getAtendenteResponsavel() { return atendenteResponsavel; }
    public void setAtendenteResponsavel(String atendenteResponsavel) {
        this.atendenteResponsavel = atendenteResponsavel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MensagemSuporte that = (MensagemSuporte) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}