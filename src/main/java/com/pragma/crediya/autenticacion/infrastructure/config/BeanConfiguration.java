package com.pragma.crediya.autenticacion.infrastructure.config;

import com.pragma.crediya.autenticacion.domain.ports.in.IUsuarioServicePort;
import com.pragma.crediya.autenticacion.domain.ports.out.IUsuarioRepositoryPort;
import com.pragma.crediya.autenticacion.domain.usecase.UsuarioUseCase;
import com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.UsuarioPersistenceAdapter;
import com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.repository.IUsuarioRepository;
import com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.mapper.IUsuarioPersistenceMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public IUsuarioRepositoryPort usuarioRepositoryPort(IUsuarioRepository usuarioRepository, IUsuarioPersistenceMapper usuarioMapper) {
        return new UsuarioPersistenceAdapter(usuarioRepository, usuarioMapper);
    }

    @Bean
    public IUsuarioServicePort usuarioServicePort(IUsuarioRepositoryPort usuarioRepositoryPort) {
        return new UsuarioUseCase(usuarioRepositoryPort);
    }
}
