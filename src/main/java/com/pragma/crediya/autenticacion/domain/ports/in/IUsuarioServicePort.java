package com.pragma.crediya.autenticacion.domain.ports.in;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface IUsuarioServicePort {
    Mono<Usuario> registrarUsuario(Usuario usuario);
    Flux<Usuario> obtenerTodosLosUsuarios();
    Mono<Usuario> obtenerUsuarioPorId(Long id);
    Mono<Usuario> obtenerUsuarioPorCorreo(String correo);
    Mono<Usuario> obtenerUsuarioPorDocumento(String documento);
}
