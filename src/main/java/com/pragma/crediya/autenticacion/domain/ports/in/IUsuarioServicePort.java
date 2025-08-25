package com.pragma.crediya.autenticacion.domain.ports.in;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import reactor.core.publisher.Mono;

public interface IUsuarioServicePort {
    Mono<Usuario> registrarUsuario(Usuario usuario);
}
