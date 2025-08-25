package com.pragma.crediya.autenticacion.domain.exception;

public class UsuarioYaExisteException extends RuntimeException {
    
    public UsuarioYaExisteException(String mensaje) {
        super(mensaje);
    }
}
