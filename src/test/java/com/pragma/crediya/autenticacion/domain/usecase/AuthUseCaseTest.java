package com.pragma.crediya.autenticacion.domain.usecase;

import com.pragma.crediya.autenticacion.domain.exception.CredencialesInvalidasException;
import com.pragma.crediya.autenticacion.domain.model.AuthResponse;
import com.pragma.crediya.autenticacion.domain.model.Login;
import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.out.IJwtGateway;
import com.pragma.crediya.autenticacion.domain.ports.out.IPasswordGateway;
import com.pragma.crediya.autenticacion.domain.ports.out.IUsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private IUsuarioRepositoryPort usuarioRepositoryPort;
    
    @Mock
    private IPasswordGateway passwordGateway;
    
    @Mock
    private IJwtGateway jwtGateway;
    
    @InjectMocks
    private AuthUseCase authUseCase;

    private Login loginValido;
    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        loginValido = new Login();
        loginValido.setCorreoElectronico("admin@pragma.com");
        loginValido.setContrasena("admin123");
        
        usuarioValido = new Usuario();
        usuarioValido.setId(1L);
        usuarioValido.setNombres("Admin");
        usuarioValido.setApellidos("Sistema");
        usuarioValido.setDocumentoIdentidad("12345678");
        usuarioValido.setFechaNacimiento(LocalDate.of(1980, 1, 1));
        usuarioValido.setCorreoElectronico("admin@pragma.com");
        usuarioValido.setContrasena("$2a$10$hashedPassword");
        usuarioValido.setRol("ADMIN");
    }

    @Test
    void deberiaAutenticarExitosamente_CuandoCredencialesSonValidas() {
        // Given
        when(usuarioRepositoryPort.obtenerUsuarioPorCorreo(anyString()))
                .thenReturn(Mono.just(usuarioValido));
        when(passwordGateway.validar(anyString(), anyString()))
                .thenReturn(true);
        when(jwtGateway.generarToken(any(Usuario.class)))
                .thenReturn("jwt.token.here");

        // When & Then
        StepVerifier.create(authUseCase.autenticar(loginValido))
                .expectNextMatches(response -> 
                    response instanceof AuthResponse &&
                    response.getToken().equals("jwt.token.here") &&
                    response.getCorreoElectronico().equals("admin@pragma.com") &&
                    response.getRol().equals("ADMIN"))
                .verifyComplete();
    }

    @Test
    void deberiaLanzarCredencialesInvalidasException_CuandoUsuarioNoExiste() {
        // Given
        when(usuarioRepositoryPort.obtenerUsuarioPorCorreo(anyString()))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(authUseCase.autenticar(loginValido))
                .expectErrorMatches(throwable -> 
                    throwable instanceof CredencialesInvalidasException &&
                    throwable.getMessage().equals("Credenciales inválidas"))
                .verify();
    }

    @Test
    void deberiaLanzarCredencialesInvalidasException_CuandoContrasenaEsIncorrecta() {
        // Given
        when(usuarioRepositoryPort.obtenerUsuarioPorCorreo(anyString()))
                .thenReturn(Mono.just(usuarioValido));
        when(passwordGateway.validar(anyString(), anyString()))
                .thenReturn(false);

        // When & Then
        StepVerifier.create(authUseCase.autenticar(loginValido))
                .expectErrorMatches(throwable -> 
                    throwable instanceof CredencialesInvalidasException &&
                    throwable.getMessage().equals("Credenciales inválidas"))
                .verify();
    }

    @Test
    void deberiaMantenerMensajeGenerico_ParaSeguridad() {
        // Given
        Login loginUsuarioInexistente = new Login();
        loginUsuarioInexistente.setCorreoElectronico("noexiste@pragma.com");
        loginUsuarioInexistente.setContrasena("cualquiera");
        
        Login loginContrasenaIncorrecta = new Login();
        loginContrasenaIncorrecta.setCorreoElectronico("admin@pragma.com");
        loginContrasenaIncorrecta.setContrasena("incorrecta");

        when(usuarioRepositoryPort.obtenerUsuarioPorCorreo("noexiste@pragma.com"))
                .thenReturn(Mono.empty());
        when(usuarioRepositoryPort.obtenerUsuarioPorCorreo("admin@pragma.com"))
                .thenReturn(Mono.just(usuarioValido));
        when(passwordGateway.validar("incorrecta", usuarioValido.getContrasena()))
                .thenReturn(false);

        // When & Then - Ambos casos deben devolver el mismo mensaje genérico
        StepVerifier.create(authUseCase.autenticar(loginUsuarioInexistente))
                .expectErrorMatches(throwable -> 
                    throwable instanceof CredencialesInvalidasException &&
                    throwable.getMessage().equals("Credenciales inválidas"))
                .verify();

        StepVerifier.create(authUseCase.autenticar(loginContrasenaIncorrecta))
                .expectErrorMatches(throwable -> 
                    throwable instanceof CredencialesInvalidasException &&
                    throwable.getMessage().equals("Credenciales inválidas"))
                .verify();
    }
}
