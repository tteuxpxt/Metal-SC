package com.metalSpring.exception;

public class EstoqueInsuficienteException extends BusinessException {
    private final int quantidadeDisponivel;
    private final int quantidadeSolicitada;

    public EstoqueInsuficienteException(String pecaId,
                                        int disponivel,
                                        int solicitada) {
        super(String.format(
                "Estoque insuficiente para peça %s. Disponível: %d, Solicitado: %d",
                pecaId, disponivel, solicitada
        ), "INSUFFICIENT_STOCK");
        this.quantidadeDisponivel = disponivel;
        this.quantidadeSolicitada = solicitada;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public int getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }
}