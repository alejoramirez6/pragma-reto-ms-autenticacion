package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.in.IUsuarioServicePort;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.UsuarioRequestDto;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper.IUsuarioRequestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
public class UsuarioController {

    private final IUsuarioServicePort usuarioServicePort;
    private final IUsuarioRequestMapper usuarioRequestMapper;

    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un nuevo usuario en el sistema validando que el correo no esté previamente registrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El correo electrónico ya está registrado")
    })
    @PostMapping
    public Mono<ResponseEntity<Usuario>> registrarUsuario(@Valid @RequestBody UsuarioRequestDto usuarioRequestDto) {
        log.info("Solicitud de registro de usuario recibida para el correo: {}", usuarioRequestDto.getCorreoElectronico());
        
        return Mono.just(usuarioRequestDto)
                .map(usuarioRequestMapper::toUsuario)
                .flatMap(usuarioServicePort::registrarUsuario)
                .map(usuario -> {
                    log.info("Usuario registrado exitosamente con ID: {}", usuario.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
                });
    }
}
