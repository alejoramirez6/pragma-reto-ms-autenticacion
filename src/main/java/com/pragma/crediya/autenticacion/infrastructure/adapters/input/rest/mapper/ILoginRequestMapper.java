package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper;

import com.pragma.crediya.autenticacion.domain.model.Login;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.LoginRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ILoginRequestMapper {
    Login toLogin(LoginRequestDto loginRequestDto);
}
