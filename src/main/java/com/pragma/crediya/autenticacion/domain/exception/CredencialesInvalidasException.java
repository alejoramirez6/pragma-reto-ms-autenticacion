package com.pragma.crediya.autenticacion.domain.exception;

/**
 * Excepción lanzada cuando las credenciales de autenticación son inválidas.
 * Usa un mensaje genérico por razones de seguridad para no revelar información
 * sobre si el usuario existe o si la contraseña es incorrecta.
 */
public class CredencialesInvalidasException extends RuntimeException {
    
    private static final String MENSAJE_GENERICO = "Credenciales inválidas";
    
    public CredencialesInvalidasException() {
        super(MENSAJE_GENERICO);
    }
    
    public CredencialesInvalidasException(String mensajeInterno) {
        super(MENSAJE_GENERICO);
        // El mensaje interno se loggea pero no se expone al usuario
    }
    
    public CredencialesInvalidasException(String mensajeInterno, Throwable causa) {
        super(MENSAJE_GENERICO, causa);
    }
}
