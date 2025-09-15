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
import reactor.core.publisher.Flux;

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

    @Override
    public Flux<Usuario> obtenerTodosLosUsuarios() {
        log.debug("Obteniendo todos los usuarios de la base de datos");
        
        return usuarioRepository.findAll()
                .doOnNext(entity -> log.debug("Usuario obtenido: ID {}, Correo: {}", entity.getId(), entity.getCorreoElectronico()))
                .map(usuarioMapper::toUsuario)
                .doOnComplete(() -> log.debug("Consulta de todos los usuarios completada"))
                .doOnError(error -> log.error("Error al obtener todos los usuarios: {}", error.getMessage(), error));
    }

    @Override
    public Mono<Usuario> obtenerUsuarioPorId(Long id) {
        log.debug("Obteniendo usuario por ID: {}", id);
        
        return usuarioRepository.findById(id)
                .doOnNext(entity -> log.debug("Usuario encontrado por ID {}: {}", id, entity.getCorreoElectronico()))
                .map(usuarioMapper::toUsuario)
                .doOnError(error -> log.error("Error al obtener usuario por ID {}: {}", id, error.getMessage(), error));
    }

    @Override
    public Mono<Usuario> obtenerUsuarioPorCorreo(String correo) {
        log.debug("Obteniendo usuario por correo: {}", correo);
        
        return usuarioRepository.findByCorreoElectronico(correo)
                .doOnNext(entity -> log.debug("Usuario encontrado por correo {}: ID {}", correo, entity.getId()))
                .map(usuarioMapper::toUsuario)
                .doOnError(error -> log.error("Error al obtener usuario por correo {}: {}", correo, error.getMessage(), error));
    }

    @Override
    public Mono<Usuario> obtenerUsuarioPorDocumento(String documento) {
        log.debug("Obteniendo usuario por documento: {}", documento);
        
        return usuarioRepository.findByDocumentoIdentidad(documento)
                .doOnNext(entity -> log.debug("Usuario encontrado por documento {}: ID {}, Email {}", 
                    documento, entity.getId(), entity.getCorreoElectronico()))
                .map(usuarioMapper::toUsuario)
                .doOnError(error -> log.error("Error al obtener usuario por documento {}: {}", documento, error.getMessage(), error));
    }
}
