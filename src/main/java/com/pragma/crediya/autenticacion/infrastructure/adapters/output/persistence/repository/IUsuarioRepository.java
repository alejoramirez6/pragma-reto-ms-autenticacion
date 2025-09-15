package com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.repository;

import com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IUsuarioRepository extends ReactiveCrudRepository<UsuarioEntity, Long> {
    Mono<Boolean> existsByCorreoElectronico(String correoElectronico);
    Mono<UsuarioEntity> findByCorreoElectronico(String correoElectronico);
    Mono<UsuarioEntity> findByDocumentoIdentidad(String documentoIdentidad);
}
