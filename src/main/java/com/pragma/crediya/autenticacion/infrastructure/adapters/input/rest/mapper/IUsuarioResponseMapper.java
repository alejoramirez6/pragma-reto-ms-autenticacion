package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.UsuarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface IUsuarioResponseMapper {

    @Mapping(target = "mensaje", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "usuario", source = "usuario")
    UsuarioResponseDto toResponseDto(Usuario usuario);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombres", source = "nombres")
    @Mapping(target = "apellidos", source = "apellidos")
    @Mapping(target = "fechaNacimiento", source = "fechaNacimiento")
    @Mapping(target = "direccion", source = "direccion")
    @Mapping(target = "telefono", source = "telefono")
    @Mapping(target = "correoElectronico", source = "correoElectronico")
    @Mapping(target = "salarioBase", source = "salarioBase")
    UsuarioResponseDto.UsuarioDataDto toUsuarioDataDto(Usuario usuario);

    default UsuarioResponseDto toSuccessResponse(Usuario usuario) {
        return UsuarioResponseDto.builder()
                .mensaje("Usuario registrado exitosamente")
                .timestamp(LocalDateTime.now())
                .usuario(toUsuarioDataDto(usuario))
                .build();
    }
}
