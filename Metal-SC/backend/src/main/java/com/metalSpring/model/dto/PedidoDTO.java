package com.metalSpring.model.dto;

import com.metalSpring.model.enums.PedidoStatus;
import com.metalSpring.model.enums.PagamentoStatus;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {
    private String id;
    private String clienteId;
    private String clienteNome;
    private String revendedorId;
    private String revendedorNome;
    private List<ItemPedidoDTO> itens;
    private Double valorTotal;
    private Double valorFinalNegociado;
    private PedidoStatus status;
    private PagamentoStatus statusPagamento;
    private Boolean aprovacaoCliente;
    private Boolean aprovacaoRevendedor;
    private String conversaId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAprovacaoCliente;
    private LocalDateTime dataAprovacaoRevendedor;
    private LocalDateTime dataPagamentoInformadoCliente;
    private LocalDateTime dataPagamentoConfirmado;
    private EnderecoDTO enderecoEntrega;
    private List<String> historicoEventos;

    
    public PedidoDTO() {}

    public PedidoDTO(String id, String clienteId, String revendedorId,
                     Double valorTotal, PedidoStatus status, LocalDateTime dataCriacao) {
        this.id = id;
        this.clienteId = clienteId;
        this.revendedorId = revendedorId;
        this.valorTotal = valorTotal;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getRevendedorId() { return revendedorId; }
    public void setRevendedorId(String revendedorId) { this.revendedorId = revendedorId; }

    public String getRevendedorNome() { return revendedorNome; }
    public void setRevendedorNome(String revendedorNome) { this.revendedorNome = revendedorNome; }

    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public Double getValorFinalNegociado() { return valorFinalNegociado; }
    public void setValorFinalNegociado(Double valorFinalNegociado) { this.valorFinalNegociado = valorFinalNegociado; }

    public PedidoStatus getStatus() { return status; }
    public void setStatus(PedidoStatus status) { this.status = status; }

    public PagamentoStatus getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(PagamentoStatus statusPagamento) { this.statusPagamento = statusPagamento; }

    public Boolean getAprovacaoCliente() { return aprovacaoCliente; }
    public void setAprovacaoCliente(Boolean aprovacaoCliente) { this.aprovacaoCliente = aprovacaoCliente; }

    public Boolean getAprovacaoRevendedor() { return aprovacaoRevendedor; }
    public void setAprovacaoRevendedor(Boolean aprovacaoRevendedor) { this.aprovacaoRevendedor = aprovacaoRevendedor; }

    public String getConversaId() { return conversaId; }
    public void setConversaId(String conversaId) { this.conversaId = conversaId; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAprovacaoCliente() { return dataAprovacaoCliente; }
    public void setDataAprovacaoCliente(LocalDateTime dataAprovacaoCliente) { this.dataAprovacaoCliente = dataAprovacaoCliente; }

    public LocalDateTime getDataAprovacaoRevendedor() { return dataAprovacaoRevendedor; }
    public void setDataAprovacaoRevendedor(LocalDateTime dataAprovacaoRevendedor) { this.dataAprovacaoRevendedor = dataAprovacaoRevendedor; }

    public LocalDateTime getDataPagamentoInformadoCliente() { return dataPagamentoInformadoCliente; }
    public void setDataPagamentoInformadoCliente(LocalDateTime dataPagamentoInformadoCliente) { this.dataPagamentoInformadoCliente = dataPagamentoInformadoCliente; }

    public LocalDateTime getDataPagamentoConfirmado() { return dataPagamentoConfirmado; }
    public void setDataPagamentoConfirmado(LocalDateTime dataPagamentoConfirmado) { this.dataPagamentoConfirmado = dataPagamentoConfirmado; }

    public EnderecoDTO getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(EnderecoDTO enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }

    public List<String> getHistoricoEventos() { return historicoEventos; }
    public void setHistoricoEventos(List<String> historicoEventos) { this.historicoEventos = historicoEventos; }
}
