package com.pragma.crediya.autenticacion.domain.ports.out;

import com.pragma.crediya.autenticacion.domain.model.Usuario;

public interface IJwtGateway {
    String generarToken(Usuario usuario);
    boolean validarToken(String token);
    String obtenerCorreoDeToken(String token);
    String obtenerRolDeToken(String token);
    String obtenerDocumentoDeToken(String token);
}
