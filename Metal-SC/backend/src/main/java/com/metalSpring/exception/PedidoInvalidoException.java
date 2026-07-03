package com.metalSpring.exception;

public class PedidoInvalidoException extends BusinessException {

    public PedidoInvalidoException(String message) {
        super(message, "INVALID_ORDER");
    }

    public static PedidoInvalidoException pedidoVazio() {
        return new PedidoInvalidoException(
                "Pedido não pode estar vazio. Adicione ao menos um item."
        );
    }

    public static PedidoInvalidoException statusInvalido(String statusAtual,
                                                         String statusNovo) {
        return new PedidoInvalidoException(
                String.format("Não é possível alterar pedido de %s para %s",
                        statusAtual, statusNovo)
        );
    }
}