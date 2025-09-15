package com.pragma.crediya.autenticacion.domain.ports.out;

import reactor.core.publisher.Mono;

public interface IAuthorizationPort {
    
    /**
     * Verifica si el usuario actual puede registrar usuarios
     * Solo ADMIN y ASESOR pueden registrar usuarios
     */
    Mono<Boolean> puedeRegistrarUsuarios();
    
    /**
     * Verifica si el usuario actual puede crear solicitudes de préstamo para el documento dado
     * Solo CLIENTE puede crear solicitudes y solo para sí mismo
     */
    Mono<Boolean> puedeCrearSolicitudPrestamo(String documentoObjetivo);
    
    /**
     * Obtiene el rol del usuario actual
     */
    Mono<String> obtenerRolUsuarioActual();
    
    /**
     * Obtiene el documento del usuario actual
     */
    Mono<String> obtenerDocumentoUsuarioActual();
}
