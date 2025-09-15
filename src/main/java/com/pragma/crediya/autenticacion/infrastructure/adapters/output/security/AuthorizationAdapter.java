package com.pragma.crediya.autenticacion.infrastructure.adapters.output.security;

import com.pragma.crediya.autenticacion.domain.ports.out.IAuthorizationPort;
import com.pragma.crediya.autenticacion.infrastructure.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationAdapter implements IAuthorizationPort {

    private final AuthenticationContext authenticationContext;

    @Override
    public Mono<Boolean> puedeRegistrarUsuarios() {
        log.info("Verificando permisos para registrar usuarios");
        return authenticationContext.isAdminOrAsesor()
                .doOnNext(puede -> log.info("Usuario puede registrar usuarios: {}", puede))
                .doOnError(error -> log.error("Error al verificar permisos de registro: {}", error.getMessage()));
    }

    @Override
    public Mono<Boolean> puedeCrearSolicitudPrestamo(String documentoObjetivo) {
        log.info("Verificando permisos para crear solicitud de préstamo para documento: {}", documentoObjetivo);
        
        return Mono.zip(
                authenticationContext.isCliente(),
                authenticationContext.getCurrentUserDocumento()
        ).flatMap(tuple -> {
            boolean esCliente = tuple.getT1();
            String documentoActual = tuple.getT2();
            
            if (!esCliente) {
                log.warn("Usuario no es CLIENTE, no puede crear solicitudes de préstamo");
                return Mono.just(false);
            }
            
            boolean puedeCrear = documentoObjetivo.equals(documentoActual);
            log.info("Usuario CLIENTE puede crear solicitud para documento {}: {}", documentoObjetivo, puedeCrear);
            
            return Mono.just(puedeCrear);
        })
        .doOnError(error -> log.error("Error al verificar permisos de solicitud de préstamo: {}", error.getMessage()));
    }

    @Override
    public Mono<String> obtenerRolUsuarioActual() {
        return authenticationContext.getCurrentUserRole();
    }

    @Override
    public Mono<String> obtenerDocumentoUsuarioActual() {
        return authenticationContext.getCurrentUserDocumento();
    }
}
