package com.pragma.crediya.autenticacion.domain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccesoDenegadoExceptionTest {
    @Test
    void constructorConMensaje() {
        AccesoDenegadoException ex = new AccesoDenegadoException("Acceso denegado");
        assertEquals("Acceso denegado", ex.getMessage());
    }

    @Test
    void constructorConMensajeYCausa() {
        Throwable causa = new RuntimeException("Causa interna");
        AccesoDenegadoException ex = new AccesoDenegadoException("Acceso denegado", causa);
        assertEquals("Acceso denegado", ex.getMessage());
        assertEquals(causa, ex.getCause());
    }
}
