package com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.mapper;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface IUsuarioPersistenceMapper {
    UsuarioEntity toEntity(Usuario usuario);
    Usuario toUsuario(UsuarioEntity usuarioEntity);
}
