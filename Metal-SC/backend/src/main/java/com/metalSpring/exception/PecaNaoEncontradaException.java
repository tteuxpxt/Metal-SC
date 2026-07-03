package com.metalSpring.exception;

public class PecaNaoEncontradaException extends BusinessException {

    public PecaNaoEncontradaException(String pecaId) {
        super("Peça não encontrada com ID: " + pecaId, "PECA_NOT_FOUND");
    }

    public PecaNaoEncontradaException(String campo, String valor) {
        super(String.format("Peça não encontrada com %s: %s", campo, valor),
                "PECA_NOT_FOUND");
    }
}