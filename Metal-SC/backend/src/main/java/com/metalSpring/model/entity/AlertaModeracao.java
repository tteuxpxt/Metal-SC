package com.metalSpring.model.entity;

import com.metalSpring.model.enums.AlertaModeracaoStatus;
import com.metalSpring.model.enums.NivelRiscoModeracao;
import com.metalSpring.model.enums.UsuarioTipo;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "alertas_moderacao")
public class AlertaModeracao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "mensagem_id")
    private MensagemNegociacao mensagem;

    @ManyToOne
    @JoinColumn(name = "conversa_id")
    private ConversaNegociacao conversa;

    @Column(nullable = true)
    private Boolean denunciaManual = false;

    @ManyToOne
    @JoinColumn(name = "peca_id")
    private Peca peca;

    @Column(name = "peca_nome")
    private String pecaNome;

    @Column(name = "imagem_url", length = 1000)
    private String imagemUrl;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String usuarioNome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsuarioTipo usuarioTipo;

    @Column(nullable = false, length = 2000)
    private String mensagemEnviada;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String palavraDetectada;

    @Column(nullable = false)
    private String tipoInfracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelRiscoModeracao nivelRisco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertaModeracaoStatus status;

    public AlertaModeracao() {
        this.dataHora = LocalDateTime.now();
        this.status = AlertaModeracaoStatus.PENDENTE;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public MensagemNegociacao getMensagem() { return mensagem; }
    public void setMensagem(MensagemNegociacao mensagem) { this.mensagem = mensagem; }

    public ConversaNegociacao getConversa() { return conversa; }
    public void setConversa(ConversaNegociacao conversa) { this.conversa = conversa; }

    public Boolean getDenunciaManual() { return denunciaManual; }
    public void setDenunciaManual(Boolean denunciaManual) { this.denunciaManual = denunciaManual; }

    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }

    public String getPecaNome() { return pecaNome; }
    public void setPecaNome(String pecaNome) { this.pecaNome = pecaNome; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }

    public UsuarioTipo getUsuarioTipo() { return usuarioTipo; }
    public void setUsuarioTipo(UsuarioTipo usuarioTipo) { this.usuarioTipo = usuarioTipo; }

    public String getMensagemEnviada() { return mensagemEnviada; }
    public void setMensagemEnviada(String mensagemEnviada) { this.mensagemEnviada = mensagemEnviada; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getPalavraDetectada() { return palavraDetectada; }
    public void setPalavraDetectada(String palavraDetectada) { this.palavraDetectada = palavraDetectada; }

    public String getTipoInfracao() { return tipoInfracao; }
    public void setTipoInfracao(String tipoInfracao) { this.tipoInfracao = tipoInfracao; }

    public NivelRiscoModeracao getNivelRisco() { return nivelRisco; }
    public void setNivelRisco(NivelRiscoModeracao nivelRisco) { this.nivelRisco = nivelRisco; }

    public AlertaModeracaoStatus getStatus() { return status; }
    public void setStatus(AlertaModeracaoStatus status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertaModeracao that = (AlertaModeracao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
