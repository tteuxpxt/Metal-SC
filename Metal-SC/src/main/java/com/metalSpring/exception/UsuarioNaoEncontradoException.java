package com.metalSpring.exception;

public class UsuarioNaoEncontradoException extends BusinessException {

    public UsuarioNaoEncontradoException(String usuarioId) {
        super("Usuário não encontrado com ID: " + usuarioId, "USER_NOT_FOUND");
    }

    public UsuarioNaoEncontradoException(String email, boolean byEmail) {
        super("Usuário não encontrado com email: " + email, "USER_NOT_FOUND");
    }
}