package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest;

import com.pragma.crediya.autenticacion.domain.exception.UsuarioYaExisteException;
import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.in.IUsuarioServicePort;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.UsuarioRequestDto;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.UsuarioResponseDto;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper.IUsuarioRequestMapper;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper.IUsuarioResponseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {
    @Test
    void clienteSoloPuedeConsultarSuPropioDocumento() {
        String documento = "12345678";
        when(usuarioServicePort.obtenerUsuarioPorDocumento(documento)).thenReturn(Mono.just(usuarioGuardado));

        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", "CLIENTE");
        claims.put("documento", documento);
        java.util.Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("alg", "HS256");
        org.springframework.security.oauth2.jwt.Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        reactor.core.publisher.Mono<org.springframework.security.core.context.SecurityContext> contextMono = reactor.core.publisher.Mono.just(securityContext);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorDocumento(documento);
            StepVerifier.create(resultado)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                .verifyComplete();
        }
    }

    @Test
    void clienteNoPuedeConsultarDocumentoDeOtro() {
        String documento = "12345678";
        String otroDocumento = "99999999";
        when(usuarioServicePort.obtenerUsuarioPorDocumento(otroDocumento)).thenReturn(Mono.just(usuarioGuardado));

        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", "CLIENTE");
        claims.put("documento", documento);
        java.util.Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("alg", "HS256");
        org.springframework.security.oauth2.jwt.Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        reactor.core.publisher.Mono<org.springframework.security.core.context.SecurityContext> contextMono = reactor.core.publisher.Mono.just(securityContext);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorDocumento(otroDocumento);
            StepVerifier.create(resultado)
                .expectError(com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException.class)
                .verify();
        }
    }
    @Test
    void deberiaPermitirAccesoPorIdSoloAdminOAsesor() {
        Long usuarioId = 1L;
        when(usuarioServicePort.obtenerUsuarioPorId(usuarioId)).thenReturn(Mono.just(usuarioGuardado));

        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", "ADMIN");
        java.util.Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("alg", "HS256");
        org.springframework.security.oauth2.jwt.Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        reactor.core.publisher.Mono<org.springframework.security.core.context.SecurityContext> contextMono = reactor.core.publisher.Mono.just(securityContext);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorId(usuarioId);
            StepVerifier.create(resultado)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                .verifyComplete();
        }
        // Cambiar rol a ASESOR y repetir
        claims.put("rol", "ASESOR");
        jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorId(usuarioId);
            StepVerifier.create(resultado)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                .verifyComplete();
        }
    }

    @Test
    void deberiaDenegarAccesoPorIdSiNoEsAdminNiAsesor() {
        Long usuarioId = 2L;
        when(usuarioServicePort.obtenerUsuarioPorId(usuarioId)).thenReturn(Mono.just(usuarioGuardado));

        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", "CLIENTE");
        java.util.Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("alg", "HS256");
        org.springframework.security.oauth2.jwt.Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        reactor.core.publisher.Mono<org.springframework.security.core.context.SecurityContext> contextMono = reactor.core.publisher.Mono.just(securityContext);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorId(usuarioId);
            StepVerifier.create(resultado)
                .expectError(com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException.class)
                .verify();
        }
    }
    @Test
    void deberiaPermitirAccesoPorCorreoSoloAdminOAsesor() {
        String correo = "admin@email.com";
        when(usuarioServicePort.obtenerUsuarioPorCorreo(correo)).thenReturn(Mono.just(usuarioGuardado));

        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", "ADMIN");
        java.util.Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("alg", "HS256");
        org.springframework.security.oauth2.jwt.Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        reactor.core.publisher.Mono<org.springframework.security.core.context.SecurityContext> contextMono = reactor.core.publisher.Mono.just(securityContext);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorCorreo(correo);
            StepVerifier.create(resultado)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                .verifyComplete();
        }
        // Cambiar rol a ASESOR y repetir
        claims.put("rol", "ASESOR");
        jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorCorreo(correo);
            StepVerifier.create(resultado)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                .verifyComplete();
        }
    }

    @Test
    void deberiaDenegarAccesoPorCorreoSiNoEsAdminNiAsesor() {
        String correo = "cliente@email.com";
        when(usuarioServicePort.obtenerUsuarioPorCorreo(correo)).thenReturn(Mono.just(usuarioGuardado));

        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", "CLIENTE");
        java.util.Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("alg", "HS256");
        org.springframework.security.oauth2.jwt.Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        reactor.core.publisher.Mono<org.springframework.security.core.context.SecurityContext> contextMono = reactor.core.publisher.Mono.just(securityContext);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorCorreo(correo);
            StepVerifier.create(resultado)
                .expectError(com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException.class)
                .verify();
        }
    }

    @Test
    void deberiaPermitirAccesoPorDocumentoSoloAdminOAsesor() {
        String documento = "12345678";
        when(usuarioServicePort.obtenerUsuarioPorDocumento(documento)).thenReturn(Mono.just(usuarioGuardado));

        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", "ADMIN");
        java.util.Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("alg", "HS256");
        org.springframework.security.oauth2.jwt.Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        reactor.core.publisher.Mono<org.springframework.security.core.context.SecurityContext> contextMono = reactor.core.publisher.Mono.just(securityContext);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorDocumento(documento);
            StepVerifier.create(resultado)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                .verifyComplete();
        }
        // Cambiar rol a ASESOR y repetir
        claims.put("rol", "ASESOR");
        jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorDocumento(documento);
            StepVerifier.create(resultado)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK)
                .verifyComplete();
        }
    }

    @Test
    void deberiaDenegarAccesoPorDocumentoSiNoEsAdminNiAsesor() {
        String documento = "87654321";
        when(usuarioServicePort.obtenerUsuarioPorDocumento(documento)).thenReturn(Mono.just(usuarioGuardado));

        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", "CLIENTE");
        java.util.Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("alg", "HS256");
        org.springframework.security.oauth2.jwt.Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
        org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        reactor.core.publisher.Mono<org.springframework.security.core.context.SecurityContext> contextMono = reactor.core.publisher.Mono.just(securityContext);
        try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
            mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
            Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorDocumento(documento);
            StepVerifier.create(resultado)
                .expectError(com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException.class)
                .verify();
        }
    }

    @Mock
    private IUsuarioServicePort usuarioServicePort;

    @Mock
    private IUsuarioRequestMapper usuarioRequestMapper;

    @Mock
    private IUsuarioResponseMapper usuarioResponseMapper;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioRequestDto usuarioRequestDto;
    private Usuario usuario;
    private Usuario usuarioGuardado;
    private UsuarioResponseDto usuarioResponseDto;

    @BeforeEach
    void setUp() {
        usuarioRequestDto = new UsuarioRequestDto();
        usuarioRequestDto.setNombres("Juan Carlos");
        usuarioRequestDto.setApellidos("Pérez González");
        usuarioRequestDto.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        usuarioRequestDto.setDireccion("Calle 123 #45-67");
        usuarioRequestDto.setTelefono("3001234567");
        usuarioRequestDto.setCorreoElectronico("juan.perez@email.com");
        usuarioRequestDto.setSalarioBase(2500000.0);

        usuario = new Usuario();
        usuario.setNombres("Juan Carlos");
        usuario.setApellidos("Pérez González");
        usuario.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        usuario.setDireccion("Calle 123 #45-67");
        usuario.setTelefono("3001234567");
        usuario.setCorreoElectronico("juan.perez@email.com");
        usuario.setSalarioBase(2500000.0);

        usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setNombres("Juan Carlos");
        usuarioGuardado.setApellidos("Pérez González");
        usuarioGuardado.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        usuarioGuardado.setDireccion("Calle 123 #45-67");
        usuarioGuardado.setTelefono("3001234567");
        usuarioGuardado.setCorreoElectronico("juan.perez@email.com");
        usuarioGuardado.setSalarioBase(2500000.0);

        usuarioResponseDto = new UsuarioResponseDto();
        usuarioResponseDto.setMensaje("Usuario registrado exitosamente");
        usuarioResponseDto.setTimestamp(LocalDateTime.now());
    }

    @Test
    void deberiaResponder201_CuandoUsuarioEsRegistradoExitosamente() {
        // Given
        when(usuarioRequestMapper.toUsuario(usuarioRequestDto)).thenReturn(usuario);
        when(usuarioServicePort.registrarUsuario(usuario)).thenReturn(Mono.just(usuarioGuardado));
        when(usuarioResponseMapper.toSuccessResponse(usuarioGuardado)).thenReturn(usuarioResponseDto);

        // When
        Mono<ResponseEntity<UsuarioResponseDto>> resultado = usuarioController.registrarUsuario(usuarioRequestDto);

        // Then
        StepVerifier.create(resultado)
                .expectNextMatches(response -> 
                    response.getStatusCode() == HttpStatus.CREATED &&
                    response.getBody() != null &&
                    response.getBody().getMensaje().equals("Usuario registrado exitosamente"))
                .verifyComplete();

        verify(usuarioRequestMapper).toUsuario(usuarioRequestDto);
        verify(usuarioServicePort).registrarUsuario(usuario);
        verify(usuarioResponseMapper).toSuccessResponse(usuarioGuardado);
    }

    @Test
    void deberiaLanzarError_CuandoUsuarioYaExiste() {
        // Given
        UsuarioYaExisteException excepcion = new UsuarioYaExisteException("Usuario ya existe");
        
        when(usuarioRequestMapper.toUsuario(usuarioRequestDto)).thenReturn(usuario);
        when(usuarioServicePort.registrarUsuario(usuario)).thenReturn(Mono.error(excepcion));

        // When
        Mono<ResponseEntity<UsuarioResponseDto>> resultado = usuarioController.registrarUsuario(usuarioRequestDto);

        // Then
        StepVerifier.create(resultado)
                .expectError(UsuarioYaExisteException.class)
                .verify();

        verify(usuarioRequestMapper).toUsuario(usuarioRequestDto);
        verify(usuarioServicePort).registrarUsuario(usuario);
    }

    @Test
    void deberiaObtenerTodosLosUsuarios() {
    // Given
    Usuario usuario1 = new Usuario();
    usuario1.setId(1L);
    usuario1.setCorreoElectronico("user1@email.com");
    usuario1.setRol("ADMIN");

    Usuario usuario2 = new Usuario();
    usuario2.setId(2L);
    usuario2.setCorreoElectronico("user2@email.com");
    usuario2.setRol("ADMIN");

    // No es necesario stubbing adicional, solo el mock principal

    // Solo mockear el SecurityContext con el claim 'rol' necesario
    java.util.Map<String, Object> claims = new java.util.HashMap<>();
    claims.put("rol", "ADMIN");
    java.util.Map<String, Object> headers = new java.util.HashMap<>();
    headers.put("alg", "HS256");
    org.springframework.security.oauth2.jwt.Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt("token", java.time.Instant.now(), java.time.Instant.now().plusSeconds(3600), headers, claims);
    org.springframework.security.core.Authentication authentication = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
    org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
    org.springframework.security.core.context.SecurityContext securityContext = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
    org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    reactor.core.publisher.Mono<org.springframework.security.core.context.SecurityContext> contextMono = reactor.core.publisher.Mono.just(securityContext);

    try (org.mockito.MockedStatic<org.springframework.security.core.context.ReactiveSecurityContextHolder> mockedStatic = org.mockito.Mockito.mockStatic(org.springframework.security.core.context.ReactiveSecurityContextHolder.class)) {
    mockedStatic.when(org.springframework.security.core.context.ReactiveSecurityContextHolder::getContext).thenReturn(contextMono);
    org.mockito.Mockito.lenient().when(usuarioServicePort.obtenerTodosLosUsuarios()).thenReturn(Flux.just(usuario1, usuario2));

        Mono<ResponseEntity<?>> resultado = usuarioController.obtenerTodosLosUsuarios();

    // ...el bloque try-with-resources ya contiene la lógica correcta...
    }

    // ...el bloque try-with-resources ya contiene la lógica correcta...
    }

    @Test
    void deberiaResponder200_CuandoUsuarioExistePorId() {
        // Given
        Long usuarioId = 1L;
        when(usuarioServicePort.obtenerUsuarioPorId(usuarioId))
                .thenReturn(Mono.just(usuarioGuardado));

        // When
        Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorId(usuarioId);

        // Then
        StepVerifier.create(resultado)
                .expectNextMatches(response -> 
                    response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    response.getBody().getId().equals(usuarioId))
                .verifyComplete();

        verify(usuarioServicePort).obtenerUsuarioPorId(usuarioId);
    }

    @Test
    void deberiaResponder404_CuandoUsuarioNoExistePorId() {
        // Given
        Long usuarioId = 999L;
        when(usuarioServicePort.obtenerUsuarioPorId(usuarioId))
                .thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorId(usuarioId);

        // Then
        StepVerifier.create(resultado)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();

        verify(usuarioServicePort).obtenerUsuarioPorId(usuarioId);
    }

    @Test
    void deberiaResponder200_CuandoUsuarioExistePorCorreo() {
        // Given
        String correo = "juan.perez@email.com";
        when(usuarioServicePort.obtenerUsuarioPorCorreo(correo))
                .thenReturn(Mono.just(usuarioGuardado));

        // When
        Mono<ResponseEntity<Usuario>> resultado = usuarioController.obtenerUsuarioPorCorreo(correo);

        // Then
        StepVerifier.create(resultado)
                .expectNextMatches(response -> 
                    response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    response.getBody().getCorreoElectronico().equals(correo))
                .verifyComplete();

        verify(usuarioServicePort).obtenerUsuarioPorCorreo(correo);
    }
}
