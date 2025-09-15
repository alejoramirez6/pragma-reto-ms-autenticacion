package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDto {
    
    private String mensaje;
    private LocalDateTime timestamp;
    private UsuarioDataDto usuario;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioDataDto {
        private Long id;
        private String nombres;
        private String apellidos;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate fechaNacimiento;
        
        private String direccion;
        private String telefono;
        private String correoElectronico;
        private Double salarioBase;
    }
}
