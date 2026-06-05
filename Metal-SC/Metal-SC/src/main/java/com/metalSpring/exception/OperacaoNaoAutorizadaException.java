package com.metalSpring.exception;

public class OperacaoNaoAutorizadaException extends BusinessException {

    public OperacaoNaoAutorizadaException(String operacao) {
        super("Operação não autorizada: " + operacao, "UNAUTHORIZED_OPERATION");
    }

    public OperacaoNaoAutorizadaException(String usuarioId, String recurso) {
        super(String.format("Usuário %s não tem permissão para acessar %s",
                        usuarioId, recurso),
                "UNAUTHORIZED_OPERATION");
    }
}