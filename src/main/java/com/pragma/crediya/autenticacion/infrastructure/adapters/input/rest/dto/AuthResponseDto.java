package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta del proceso de autenticación")
public class AuthResponseDto {

    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Documento de identidad del usuario", example = "12345678")
    private String documentoIdentidad;

    @Schema(description = "Correo electrónico del usuario", example = "usuario@ejemplo.com")
    private String correoElectronico;

    @Schema(description = "Rol del usuario", example = "CLIENTE")
    private String rol;
}
