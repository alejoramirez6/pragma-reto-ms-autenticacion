package com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.out.IUsuarioRepositoryPort;
import com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.repository.IUsuarioRepository;
import com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.mapper.IUsuarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements IUsuarioRepositoryPort {

    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioPersistenceMapper usuarioMapper;

    @Override
    @Transactional
    public Mono<Usuario> guardarUsuario(Usuario usuario) {
        log.debug("Guardando usuario en base de datos - Correo: {}", usuario.getCorreoElectronico());
        
        return Mono.just(usuario)
                .map(usuarioMapper::toEntity)
                .doOnNext(entity -> log.debug("Entidad mapeada para persistencia"))
                .flatMap(usuarioRepository::save)
                .doOnNext(savedEntity -> log.debug("Usuario guardado con ID: {}", savedEntity.getId()))
                .map(usuarioMapper::toUsuario)
                .doOnError(error -> log.error("Error al guardar usuario en base de datos: {}", error.getMessage(), error));
    }

    @Override
    public Mono<Boolean> existeUsuarioPorCorreo(String correo) {
        log.debug("Verificando si existe usuario con correo: {}", correo);
        
        return usuarioRepository.existsByCorreoElectronico(correo)
                .doOnNext(existe -> log.debug("Resultado verificación correo {}: {}", correo, existe))
                .doOnError(error -> log.error("Error al verificar existencia de correo {}: {}", correo, error.getMessage(), error));
    }
}
