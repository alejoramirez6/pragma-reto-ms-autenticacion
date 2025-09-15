package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper;

import com.pragma.crediya.autenticacion.domain.model.AuthResponse;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.AuthResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IAuthResponseMapper {
    AuthResponseDto toAuthResponseDto(AuthResponse authResponse);
}
