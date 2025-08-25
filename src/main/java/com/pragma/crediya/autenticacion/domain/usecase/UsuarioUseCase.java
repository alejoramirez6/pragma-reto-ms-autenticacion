package com.pragma.crediya.autenticacion.domain.usecase;

import com.pragma.crediya.autenticacion.domain.exception.UsuarioYaExisteException;
import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.in.IUsuarioServicePort;
import com.pragma.crediya.autenticacion.domain.ports.out.IUsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class UsuarioUseCase implements IUsuarioServicePort {

    private final IUsuarioRepositoryPort usuarioRepositoryPort;

    @Override
    @Transactional
    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        log.info("Iniciando registro de usuario con correo: {}", usuario.getCorreoElectronico());
        
        return usuarioRepositoryPort.existeUsuarioPorCorreo(usuario.getCorreoElectronico())
                .doOnNext(existe -> log.debug("Verificación de correo único - Existe: {}", existe))
                .flatMap(existe -> {
                    if (Boolean.TRUE.equals(existe)) {
                        log.warn("Intento de registro con correo ya existente: {}", usuario.getCorreoElectronico());
                        return Mono.error(new UsuarioYaExisteException(
                            "El correo electrónico '" + usuario.getCorreoElectronico() + "' ya está registrado en el sistema."
                        ));
                    }
                    
                    log.debug("Correo único validado, procediendo a guardar usuario");
                    return usuarioRepositoryPort.guardarUsuario(usuario);
                })
                .doOnSuccess(usuarioGuardado -> 
                    log.info("Usuario registrado exitosamente - ID: {}, Correo: {}", 
                        usuarioGuardado.getId(), usuarioGuardado.getCorreoElectronico())
                )
                .doOnError(error -> 
                    log.error("Error durante el registro de usuario con correo {}: {}", 
                        usuario.getCorreoElectronico(), error.getMessage(), error)
                );
    }
}
