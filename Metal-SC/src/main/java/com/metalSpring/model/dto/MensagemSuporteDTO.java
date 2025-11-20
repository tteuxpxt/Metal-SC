// ==================== MensagemSuporteDTO.java ====================
package com.metalSpring.model.dto;

import com.metalSpring.model.enums.CanalSuporte;
import java.time.LocalDateTime;

public class MensagemSuporteDTO {
    private String id;
    private String usuarioId;
    private String usuarioNome;
    private String conteudo;
    private LocalDateTime dataEnvio;
    private CanalSuporte canal;
    private Boolean lida;
    private String atendenteResponsavel;
    private String resposta;
    private LocalDateTime dataResposta;

    // Construtores
    public MensagemSuporteDTO() {}

    public MensagemSuporteDTO(String usuarioId, String conteudo, CanalSuporte canal) {
        this.usuarioId = usuarioId;
        this.conteudo = conteudo;
        this.canal = canal;
        this.lida = false;
        this.dataEnvio = LocalDateTime.now();
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public CanalSuporte getCanal() {
        return canal;
    }

    public void setCanal(CanalSuporte canal) {
        this.canal = canal;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    public String getAtendenteResponsavel() {
        return atendenteResponsavel;
    }

    public void setAtendenteResponsavel(String atendenteResponsavel) {
        this.atendenteResponsavel = atendenteResponsavel;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public LocalDateTime getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(LocalDateTime dataResposta) {
        this.dataResposta = dataResposta;
    }
}
