package com.pragma.crediya.autenticacion.domain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CredencialesInvalidasExceptionTest {
    @Test
    void constructorPorDefecto() {
        CredencialesInvalidasException ex = new CredencialesInvalidasException();
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void constructorConMensajeInterno() {
        CredencialesInvalidasException ex = new CredencialesInvalidasException("Mensaje interno");
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void constructorConMensajeInternoYCausa() {
        Throwable causa = new RuntimeException("Causa interna");
        CredencialesInvalidasException ex = new CredencialesInvalidasException("Mensaje interno", causa);
        assertEquals("Credenciales inválidas", ex.getMessage());
        assertEquals(causa, ex.getCause());
    }
}
