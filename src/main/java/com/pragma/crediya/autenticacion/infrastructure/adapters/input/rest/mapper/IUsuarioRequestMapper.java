package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.UsuarioRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUsuarioRequestMapper {
    
    Usuario toUsuario(UsuarioRequestDto usuarioRequestDto);
}
