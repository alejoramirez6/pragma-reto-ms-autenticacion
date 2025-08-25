package com.pragma.crediya.autenticacion.domain.ports.out;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import reactor.core.publisher.Mono;

public interface IUsuarioRepositoryPort {
    Mono<Usuario> guardarUsuario(Usuario usuario);
    Mono<Boolean> existeUsuarioPorCorreo(String correo);
}
