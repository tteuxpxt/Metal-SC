package com.metalSpring.model.dto;

import com.metalSpring.model.enums.AlertaModeracaoStatus;
import com.metalSpring.model.enums.NivelRiscoModeracao;
import com.metalSpring.model.enums.UsuarioTipo;
import java.time.LocalDateTime;

public class AlertaModeracaoDTO {
    private String id;
    private String conversaId;
    private String mensagemId;
    private String pecaId;
    private String pecaNome;
    private String imagemUrl;
    private String usuarioId;
    private String usuarioNome;
    private UsuarioTipo usuarioTipo;
    private String mensagemEnviada;
    private LocalDateTime dataHora;
    private String palavraDetectada;
    private String tipoInfracao;
    private NivelRiscoModeracao nivelRisco;
    private AlertaModeracaoStatus status;
    private long contadorInfracoesUsuario;
    private boolean denunciaManual;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConversaId() { return conversaId; }
    public void setConversaId(String conversaId) { this.conversaId = conversaId; }
    public String getMensagemId() { return mensagemId; }
    public void setMensagemId(String mensagemId) { this.mensagemId = mensagemId; }
    public String getPecaId() { return pecaId; }
    public void setPecaId(String pecaId) { this.pecaId = pecaId; }
    public String getPecaNome() { return pecaNome; }
    public void setPecaNome(String pecaNome) { this.pecaNome = pecaNome; }
    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
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
    public long getContadorInfracoesUsuario() { return contadorInfracoesUsuario; }
    public void setContadorInfracoesUsuario(long contadorInfracoesUsuario) { this.contadorInfracoesUsuario = contadorInfracoesUsuario; }
    public boolean isDenunciaManual() { return denunciaManual; }
    public void setDenunciaManual(boolean denunciaManual) { this.denunciaManual = denunciaManual; }
}
