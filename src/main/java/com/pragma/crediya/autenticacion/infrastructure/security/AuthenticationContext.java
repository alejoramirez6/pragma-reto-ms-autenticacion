package com.pragma.crediya.autenticacion.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationContext {

    /**
     * Obtiene el rol del usuario autenticado desde el token JWT
     */
    public Mono<String> getCurrentUserRole() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(Authentication.class)
                .map(Authentication::getPrincipal)
                .cast(Jwt.class)
                .map(jwt -> jwt.getClaimAsString("rol"))
                .doOnNext(rol -> log.debug("Rol extraído del JWT: {}", rol))
                .doOnError(error -> log.error("Error al extraer rol del JWT: {}", error.getMessage()));
    }

    /**
     * Obtiene el documento del usuario autenticado desde el token JWT
     */
    public Mono<String> getCurrentUserDocumento() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(Authentication.class)
                .map(Authentication::getPrincipal)
                .cast(Jwt.class)
                .map(jwt -> jwt.getClaimAsString("documento"))
                .doOnNext(documento -> log.debug("Documento extraído del JWT: {}", documento))
                .doOnError(error -> log.error("Error al extraer documento del JWT: {}", error.getMessage()));
    }

    /**
     * Verifica si el usuario actual tiene un rol específico
     */
    public Mono<Boolean> hasRole(String role) {
        return getCurrentUserRole()
                .map(currentRole -> role.equals(currentRole))
                .defaultIfEmpty(false)
                .doOnNext(hasRole -> log.debug("Usuario tiene rol '{}': {}", role, hasRole));
    }

    /**
     * Verifica si el usuario actual es ADMIN o ASESOR
     */
    public Mono<Boolean> isAdminOrAsesor() {
        return getCurrentUserRole()
                .map(role -> "ADMIN".equals(role) || "ASESOR".equals(role))
                .defaultIfEmpty(false)
                .doOnNext(isAdminOrAsesor -> log.debug("Usuario es ADMIN/ASESOR: {}", isAdminOrAsesor));
    }

    /**
     * Verifica si el usuario actual es CLIENTE
     */
    public Mono<Boolean> isCliente() {
        return hasRole("CLIENTE");
    }
}
