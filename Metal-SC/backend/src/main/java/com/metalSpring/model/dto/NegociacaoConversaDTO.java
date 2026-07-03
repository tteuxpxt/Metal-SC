package com.metalSpring.model.dto;

import com.metalSpring.model.enums.NegociacaoStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NegociacaoConversaDTO {
    private String id;
    private String pedidoId;
    private String pecaId;
    private String pecaNome;
    private String clienteId;
    private String clienteNome;
    private String revendedorId;
    private String revendedorNome;
    private Double valorOriginal;
    private Double valorNegociado;
    private Double valorFinalAcordado;
    private Boolean aprovacaoCliente;
    private Boolean aprovacaoRevendedor;
    private LocalDateTime dataAprovacaoCliente;
    private LocalDateTime dataAprovacaoRevendedor;
    private NegociacaoStatus status;
    private LocalDateTime criadaEm;
    private LocalDateTime atualizadaEm;
    private long naoLidas;
    private List<NegociacaoMensagemDTO> mensagens = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPedidoId() { return pedidoId; }
    public void setPedidoId(String pedidoId) { this.pedidoId = pedidoId; }
    public String getPecaId() { return pecaId; }
    public void setPecaId(String pecaId) { this.pecaId = pecaId; }
    public String getPecaNome() { return pecaNome; }
    public void setPecaNome(String pecaNome) { this.pecaNome = pecaNome; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public String getRevendedorId() { return revendedorId; }
    public void setRevendedorId(String revendedorId) { this.revendedorId = revendedorId; }
    public String getRevendedorNome() { return revendedorNome; }
    public void setRevendedorNome(String revendedorNome) { this.revendedorNome = revendedorNome; }
    public Double getValorOriginal() { return valorOriginal; }
    public void setValorOriginal(Double valorOriginal) { this.valorOriginal = valorOriginal; }
    public Double getValorNegociado() { return valorNegociado; }
    public void setValorNegociado(Double valorNegociado) { this.valorNegociado = valorNegociado; }
    public Double getValorFinalAcordado() { return valorFinalAcordado; }
    public void setValorFinalAcordado(Double valorFinalAcordado) { this.valorFinalAcordado = valorFinalAcordado; }
    public Boolean getAprovacaoCliente() { return aprovacaoCliente; }
    public void setAprovacaoCliente(Boolean aprovacaoCliente) { this.aprovacaoCliente = aprovacaoCliente; }
    public Boolean getAprovacaoRevendedor() { return aprovacaoRevendedor; }
    public void setAprovacaoRevendedor(Boolean aprovacaoRevendedor) { this.aprovacaoRevendedor = aprovacaoRevendedor; }
    public LocalDateTime getDataAprovacaoCliente() { return dataAprovacaoCliente; }
    public void setDataAprovacaoCliente(LocalDateTime dataAprovacaoCliente) { this.dataAprovacaoCliente = dataAprovacaoCliente; }
    public LocalDateTime getDataAprovacaoRevendedor() { return dataAprovacaoRevendedor; }
    public void setDataAprovacaoRevendedor(LocalDateTime dataAprovacaoRevendedor) { this.dataAprovacaoRevendedor = dataAprovacaoRevendedor; }
    public NegociacaoStatus getStatus() { return status; }
    public void setStatus(NegociacaoStatus status) { this.status = status; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public void setCriadaEm(LocalDateTime criadaEm) { this.criadaEm = criadaEm; }
    public LocalDateTime getAtualizadaEm() { return atualizadaEm; }
    public void setAtualizadaEm(LocalDateTime atualizadaEm) { this.atualizadaEm = atualizadaEm; }
    public long getNaoLidas() { return naoLidas; }
    public void setNaoLidas(long naoLidas) { this.naoLidas = naoLidas; }
    public List<NegociacaoMensagemDTO> getMensagens() { return mensagens; }
    public void setMensagens(List<NegociacaoMensagemDTO> mensagens) { this.mensagens = mensagens; }
}
