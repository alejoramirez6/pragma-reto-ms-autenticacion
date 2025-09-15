package com.pragma.crediya.autenticacion.domain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioYaExisteExceptionTest {
    @Test
    void constructorConMensaje() {
        UsuarioYaExisteException ex = new UsuarioYaExisteException("Ya existe");
        assertEquals("Ya existe", ex.getMessage());
    }
}
