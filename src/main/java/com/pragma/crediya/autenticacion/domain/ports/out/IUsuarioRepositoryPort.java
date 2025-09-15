package com.pragma.crediya.autenticacion.domain.ports.out;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface IUsuarioRepositoryPort {
    Mono<Usuario> guardarUsuario(Usuario usuario);
    Mono<Boolean> existeUsuarioPorCorreo(String correo);
    Flux<Usuario> obtenerTodosLosUsuarios();
    Mono<Usuario> obtenerUsuarioPorId(Long id);
    Mono<Usuario> obtenerUsuarioPorCorreo(String correo);
    Mono<Usuario> obtenerUsuarioPorDocumento(String documento);
}
