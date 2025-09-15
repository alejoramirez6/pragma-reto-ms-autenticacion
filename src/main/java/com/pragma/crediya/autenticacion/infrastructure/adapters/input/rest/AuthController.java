package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest;

import com.pragma.crediya.autenticacion.domain.ports.in.IAuthServicePort;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.AuthResponseDto;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.LoginRequestDto;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper.IAuthResponseMapper;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper.ILoginRequestMapper;
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
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "API para autenticación de usuarios")
public class AuthController {

    private final IAuthServicePort authServicePort;
    private final ILoginRequestMapper loginRequestMapper;
    private final IAuthResponseMapper authResponseMapper;

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con correo y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("Solicitud de autenticación recibida para el correo: {}", loginRequestDto.getCorreoElectronico());
        
        return Mono.just(loginRequestDto)
                .map(loginRequestMapper::toLogin)
                .flatMap(authServicePort::autenticar)
                .map(authResponseMapper::toAuthResponseDto)
                .map(response -> ResponseEntity.ok(response))
                .doOnSuccess(response -> log.info("Autenticación exitosa para: {}", loginRequestDto.getCorreoElectronico()));
                // Removemos onErrorResume para que el GlobalExceptionHandler maneje los errores
    }
}
