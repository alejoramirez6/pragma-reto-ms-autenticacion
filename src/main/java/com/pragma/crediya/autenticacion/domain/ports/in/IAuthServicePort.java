package com.pragma.crediya.autenticacion.domain.ports.in;

import com.pragma.crediya.autenticacion.domain.model.AuthResponse;
import com.pragma.crediya.autenticacion.domain.model.Login;
import reactor.core.publisher.Mono;

public interface IAuthServicePort {
    Mono<AuthResponse> autenticar(Login login);
}
