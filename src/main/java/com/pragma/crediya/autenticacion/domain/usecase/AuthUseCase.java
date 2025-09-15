package com.pragma.crediya.autenticacion.domain.usecase;

import com.pragma.crediya.autenticacion.domain.exception.CredencialesInvalidasException;
import com.pragma.crediya.autenticacion.domain.model.AuthResponse;
import com.pragma.crediya.autenticacion.domain.model.Login;
import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.in.IAuthServicePort;
import com.pragma.crediya.autenticacion.domain.ports.out.IJwtGateway;
import com.pragma.crediya.autenticacion.domain.ports.out.IPasswordGateway;
import com.pragma.crediya.autenticacion.domain.ports.out.IUsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class AuthUseCase implements IAuthServicePort {

    private final IUsuarioRepositoryPort usuarioRepositoryPort;
    private final IPasswordGateway passwordGateway;
    private final IJwtGateway jwtGateway;

    @Override
    public Mono<AuthResponse> autenticar(Login login) {
        log.info("Iniciando proceso de autenticación para el correo: {}", login.getCorreoElectronico());
        
        return usuarioRepositoryPort.obtenerUsuarioPorCorreo(login.getCorreoElectronico())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Intento de autenticación con correo no registrado: {}", login.getCorreoElectronico());
                    return Mono.error(new CredencialesInvalidasException("Usuario no encontrado"));
                }))
                .filter(usuario -> passwordGateway.validar(login.getContrasena(), usuario.getContrasena()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Intento de autenticación con contraseña incorrecta para: {}", login.getCorreoElectronico());
                    return Mono.error(new CredencialesInvalidasException("Contraseña incorrecta"));
                }))
                .map(this::crearRespuestaAutenticacion)
                .doOnSuccess(response -> log.info("Autenticación exitosa para: {}", login.getCorreoElectronico()))
                .doOnError(error -> {
                    if (!(error instanceof CredencialesInvalidasException)) {
                        log.error("Error técnico en autenticación para {}: {}", login.getCorreoElectronico(), error.getMessage());
                    }
                });
    }

    private AuthResponse crearRespuestaAutenticacion(Usuario usuario) {
        String token = jwtGateway.generarToken(usuario);
        return new AuthResponse(token, usuario.getDocumentoIdentidad(), usuario.getCorreoElectronico(), usuario.getRol());
    }
}
