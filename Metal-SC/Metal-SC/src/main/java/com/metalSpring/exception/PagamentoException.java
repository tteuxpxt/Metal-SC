package com.metalSpring.exception;

public class PagamentoException extends BusinessException {

    public PagamentoException(String message) {
        super(message, "PAYMENT_ERROR");
    }

    public PagamentoException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PagamentoException pagamentoRecusado(String motivo) {
        return new PagamentoException("Pagamento recusado: " + motivo);
    }

    public static PagamentoException timeoutProcessamento() {
        return new PagamentoException(
                "Timeout ao processar pagamento. Tente novamente."
        );
    }
}