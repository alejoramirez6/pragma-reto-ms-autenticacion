package com.pragma.crediya.autenticacion.domain.usecase;

import com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException;
import com.pragma.crediya.autenticacion.domain.exception.UsuarioYaExisteException;
import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.in.IUsuarioServicePort;
import com.pragma.crediya.autenticacion.domain.ports.out.IAuthorizationPort;
import com.pragma.crediya.autenticacion.domain.ports.out.IPasswordGateway;
import com.pragma.crediya.autenticacion.domain.ports.out.IUsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
public class UsuarioUseCase implements IUsuarioServicePort {

    private final IUsuarioRepositoryPort usuarioRepositoryPort;
    private final IPasswordGateway passwordGateway;
    private final IAuthorizationPort authorizationPort;

    @Override
    @Transactional
    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        log.info("Iniciando registro de usuario con correo: {}", usuario.getCorreoElectronico());
        
        // Validar autorización primero
        return authorizationPort.puedeRegistrarUsuarios()
                .flatMap(puedeRegistrar -> {
                    if (!puedeRegistrar) {
                        log.warn("Acceso denegado: Usuario no tiene permisos para registrar usuarios");
                        return Mono.error(new AccesoDenegadoException(
                            "No tiene permisos para registrar usuarios. Solo usuarios con rol ADMIN o ASESOR pueden realizar esta acción."
                        ));
                    }
                    
                    log.debug("Autorización validada, procediendo con verificación de correo único");
                    return usuarioRepositoryPort.existeUsuarioPorCorreo(usuario.getCorreoElectronico());
                })
                .doOnNext(existe -> log.debug("Verificación de correo único - Existe: {}", existe))
                .flatMap(existe -> {
                    if (Boolean.TRUE.equals(existe)) {
                        log.warn("Intento de registro con correo ya existente: {}", usuario.getCorreoElectronico());
                        return Mono.error(new UsuarioYaExisteException(
                            "El correo electrónico '" + usuario.getCorreoElectronico() + "' ya está registrado en el sistema."
                        ));
                    }
                    
                    log.debug("Correo único validado, procediendo a encriptar contraseña y guardar usuario");
                    // Encriptar la contraseña antes de guardar
                    usuario.setContrasena(passwordGateway.encriptar(usuario.getContrasena()));
                    return usuarioRepositoryPort.guardarUsuario(usuario);
                })
                .doOnSuccess(usuarioGuardado -> 
                    log.info("Usuario registrado exitosamente - ID: {}, Correo: {}, Rol: {}", 
                        usuarioGuardado.getId(), usuarioGuardado.getCorreoElectronico(), usuarioGuardado.getRol())
                )
                .doOnError(error -> 
                    log.error("Error durante el registro de usuario con correo {}: {}", 
                        usuario.getCorreoElectronico(), error.getMessage(), error)
                );
    }

    @Override
    public Flux<Usuario> obtenerTodosLosUsuarios() {
        log.info("Obteniendo todos los usuarios registrados");
        return usuarioRepositoryPort.obtenerTodosLosUsuarios()
                .doOnNext(usuario -> log.debug("Usuario obtenido: {}", usuario.getCorreoElectronico()))
                .doOnComplete(() -> log.info("Consulta de todos los usuarios completada"))
                .doOnError(error -> log.error("Error al obtener todos los usuarios: {}", error.getMessage(), error));
    }

    @Override
    public Mono<Usuario> obtenerUsuarioPorId(Long id) {
        log.info("Obteniendo usuario por ID: {}", id);
        return usuarioRepositoryPort.obtenerUsuarioPorId(id)
                .doOnNext(usuario -> log.debug("Usuario encontrado por ID {}: {}", id, usuario.getCorreoElectronico()))
                .doOnError(error -> log.error("Error al obtener usuario por ID {}: {}", id, error.getMessage(), error));
    }

    @Override
    public Mono<Usuario> obtenerUsuarioPorCorreo(String correo) {
        log.info("Obteniendo usuario por correo: {}", correo);
        return usuarioRepositoryPort.obtenerUsuarioPorCorreo(correo)
                .doOnNext(usuario -> log.debug("Usuario encontrado por correo {}: ID {}", correo, usuario.getId()))
                .doOnError(error -> log.error("Error al obtener usuario por correo {}: {}", correo, error.getMessage(), error));
    }

    @Override
    public Mono<Usuario> obtenerUsuarioPorDocumento(String documento) {
        log.info("Obteniendo usuario por documento: {}", documento);
        return usuarioRepositoryPort.obtenerUsuarioPorDocumento(documento)
                .doOnNext(usuario -> log.debug("Usuario encontrado por documento {}: ID {}, Email {}", 
                    documento, usuario.getId(), usuario.getCorreoElectronico()))
                .doOnError(error -> log.error("Error al obtener usuario por documento {}: {}", documento, error.getMessage(), error));
    }
}
