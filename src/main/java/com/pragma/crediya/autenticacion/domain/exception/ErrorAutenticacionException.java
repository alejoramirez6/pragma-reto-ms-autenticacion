package com.pragma.crediya.autenticacion.domain.exception;

/**
 * Excepción lanzada cuando ocurre un error durante el proceso de autenticación.
 * Usa un mensaje genérico por razones de seguridad.
 */
public class ErrorAutenticacionException extends RuntimeException {
    
    private static final String MENSAJE_GENERICO = "Error en el proceso de autenticación";
    
    public ErrorAutenticacionException() {
        super(MENSAJE_GENERICO);
    }
    
    public ErrorAutenticacionException(String mensajeInterno) {
        super(MENSAJE_GENERICO);
        // El mensaje interno se loggea pero no se expone al usuario
    }
    
    public ErrorAutenticacionException(String mensajeInterno, Throwable causa) {
        super(MENSAJE_GENERICO, causa);
    }
}
