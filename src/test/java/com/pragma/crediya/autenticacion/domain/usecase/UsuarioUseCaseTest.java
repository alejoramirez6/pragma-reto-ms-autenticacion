package com.pragma.crediya.autenticacion.domain.usecase;

import com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException;
import com.pragma.crediya.autenticacion.domain.exception.UsuarioYaExisteException;
import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.out.IAuthorizationPort;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private IUsuarioRepositoryPort usuarioRepositoryPort;
    
    @Mock(lenient = true)
    private IPasswordGateway passwordGateway;
    
    @Mock(lenient = true)
    private IAuthorizationPort authorizationPort;
    
    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = new Usuario();
        usuarioValido.setNombres("Juan Carlos");
        usuarioValido.setApellidos("Pérez González");
        usuarioValido.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        usuarioValido.setDireccion("Calle 123 #45-67");
        usuarioValido.setTelefono("3001234567");
        usuarioValido.setCorreoElectronico("juan.perez@email.com");
        usuarioValido.setSalarioBase(2500000.0);
        usuarioValido.setContrasena("password123");
        usuarioValido.setRol("CLIENTE");
        
        // Configurar comportamiento del mock de passwordGateway
        when(passwordGateway.encriptar(anyString())).thenReturn("$2a$10$hashedPassword");
        
        // Configurar comportamiento del mock de authorizationPort (por defecto permitir)
        when(authorizationPort.puedeRegistrarUsuarios()).thenReturn(Mono.just(true));
    }

    private Usuario crearUsuario(Long id, String correo) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombres("Juan Carlos");
        usuario.setApellidos("Pérez González");
        usuario.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        usuario.setDireccion("Calle 123 #45-67");
        usuario.setTelefono("3001234567");
        usuario.setCorreoElectronico(correo);
        usuario.setSalarioBase(2500000.0);
        return usuario;
    }

    private Usuario crearUsuarioConId(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombres(usuarioValido.getNombres());
        usuario.setApellidos(usuarioValido.getApellidos());
        usuario.setFechaNacimiento(usuarioValido.getFechaNacimiento());
        usuario.setDireccion(usuarioValido.getDireccion());
        usuario.setTelefono(usuarioValido.getTelefono());
        usuario.setCorreoElectronico(usuarioValido.getCorreoElectronico());
        usuario.setSalarioBase(usuarioValido.getSalarioBase());
        return usuario;
    }

    @Test
    void deberiaRegistrarUsuarioExitosamente_CuandoCorreoNoExiste() {
        // Given - Arrange
        Usuario usuarioGuardado = crearUsuarioConId(1L);
        
        when(usuarioRepositoryPort.existeUsuarioPorCorreo(anyString()))
                .thenReturn(Mono.just(false));
        when(usuarioRepositoryPort.guardarUsuario(any(Usuario.class)))
                .thenReturn(Mono.just(usuarioGuardado));

        // When - Act & Then - Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectNext(usuarioGuardado)
                .verifyComplete();

        // Verify interactions
        verify(usuarioRepositoryPort).existeUsuarioPorCorreo("juan.perez@email.com");
        verify(usuarioRepositoryPort).guardarUsuario(usuarioValido);
    }

    @Test
    void deberiaLanzarUsuarioYaExisteException_CuandoCorreoYaExiste() {
        // Given - Arrange
        when(usuarioRepositoryPort.existeUsuarioPorCorreo(anyString()))
                .thenReturn(Mono.just(true));

        // When - Act & Then - Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectErrorMatches(throwable -> 
                    throwable instanceof UsuarioYaExisteException &&
                    throwable.getMessage().contains("juan.perez@email.com") &&
                    throwable.getMessage().contains("ya está registrado"))
                .verify();

        // Verify interactions
        verify(usuarioRepositoryPort).existeUsuarioPorCorreo("juan.perez@email.com");
        verify(usuarioRepositoryPort, never()).guardarUsuario(any(Usuario.class));
    }

    @Test
    void deberiaLanzarError_CuandoVerificacionCorreoFalla() {
        // Given - Arrange
        RuntimeException errorBD = new RuntimeException("Error de conexión a BD");
        when(usuarioRepositoryPort.existeUsuarioPorCorreo(anyString()))
                .thenReturn(Mono.error(errorBD));

        // When - Act & Then - Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectError(RuntimeException.class)
                .verify();

        // Verify interactions
        verify(usuarioRepositoryPort).existeUsuarioPorCorreo("juan.perez@email.com");
        verify(usuarioRepositoryPort, never()).guardarUsuario(any(Usuario.class));
    }

    @Test
    void deberiaLanzarError_CuandoGuardadoFalla() {
        // Given - Arrange
        RuntimeException errorGuardado = new RuntimeException("Error al guardar usuario");
        
        when(usuarioRepositoryPort.existeUsuarioPorCorreo(anyString()))
                .thenReturn(Mono.just(false));
        when(usuarioRepositoryPort.guardarUsuario(any(Usuario.class)))
                .thenReturn(Mono.error(errorGuardado));

        // When - Act & Then - Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectError(RuntimeException.class)
                .verify();

        // Verify interactions
        verify(usuarioRepositoryPort).existeUsuarioPorCorreo("juan.perez@email.com");
        verify(usuarioRepositoryPort).guardarUsuario(usuarioValido);
    }

    @Test
    void deberiaObtenerTodosLosUsuarios() {
        // Given - Arrange
        Usuario usuario1 = crearUsuario(1L, "user1@email.com");
        Usuario usuario2 = crearUsuario(2L, "user2@email.com");
        
        when(usuarioRepositoryPort.obtenerTodosLosUsuarios())
                .thenReturn(reactor.core.publisher.Flux.just(usuario1, usuario2));

        // When - Act & Then - Assert
        StepVerifier.create(usuarioUseCase.obtenerTodosLosUsuarios())
                .expectNext(usuario1)
                .expectNext(usuario2)
                .verifyComplete();

        verify(usuarioRepositoryPort).obtenerTodosLosUsuarios();
    }

    @Test
    void deberiaObtenerUsuarioPorId_CuandoExiste() {
        // Given - Arrange
        Long usuarioId = 1L;
        Usuario usuarioEncontrado = crearUsuarioConId(usuarioId);
        
        when(usuarioRepositoryPort.obtenerUsuarioPorId(usuarioId))
                .thenReturn(Mono.just(usuarioEncontrado));

        // When - Act & Then - Assert
        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorId(usuarioId))
                .expectNext(usuarioEncontrado)
                .verifyComplete();

        verify(usuarioRepositoryPort).obtenerUsuarioPorId(usuarioId);
    }

    @Test
    void deberiaRetornarVacio_CuandoUsuarioNoExistePorId() {
        // Given - Arrange
        Long usuarioId = 999L;
        
        when(usuarioRepositoryPort.obtenerUsuarioPorId(usuarioId))
                .thenReturn(Mono.empty());

        // When - Act & Then - Assert
        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorId(usuarioId))
                .verifyComplete();

        verify(usuarioRepositoryPort).obtenerUsuarioPorId(usuarioId);
    }

    @Test
    void deberiaLanzarAccesoDenegadoException_CuandoUsuarioNoTienePermisos() {
        // Given - Arrange
        when(authorizationPort.puedeRegistrarUsuarios()).thenReturn(Mono.just(false));

        // When - Act & Then - Assert
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectError(AccesoDenegadoException.class)
                .verify();

        // Verificar que no se llame al repositorio si no hay permisos
        verify(usuarioRepositoryPort, never()).existeUsuarioPorCorreo(anyString());
        verify(usuarioRepositoryPort, never()).guardarUsuario(any(Usuario.class));
        verify(authorizationPort).puedeRegistrarUsuarios();
    }

    @Test
    void deberiaObtenerUsuarioPorCorreo_CuandoExiste() {
        Usuario usuario = crearUsuarioConId(1L);
        when(usuarioRepositoryPort.obtenerUsuarioPorCorreo("juan.perez@email.com"))
                .thenReturn(Mono.just(usuario));
        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorCorreo("juan.perez@email.com"))
                .expectNext(usuario)
                .verifyComplete();
        verify(usuarioRepositoryPort).obtenerUsuarioPorCorreo("juan.perez@email.com");
    }

    @Test
    void deberiaRetornarVacio_CuandoUsuarioNoExistePorCorreo() {
        when(usuarioRepositoryPort.obtenerUsuarioPorCorreo("noexiste@email.com"))
                .thenReturn(Mono.empty());
        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorCorreo("noexiste@email.com"))
                .verifyComplete();
        verify(usuarioRepositoryPort).obtenerUsuarioPorCorreo("noexiste@email.com");
    }

    @Test
    void deberiaLanzarError_CuandoObtencionPorCorreoFalla() {
        RuntimeException error = new RuntimeException("Error al buscar por correo");
        when(usuarioRepositoryPort.obtenerUsuarioPorCorreo("error@email.com"))
                .thenReturn(Mono.error(error));
        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorCorreo("error@email.com"))
                .expectError(RuntimeException.class)
                .verify();
        verify(usuarioRepositoryPort).obtenerUsuarioPorCorreo("error@email.com");
    }

    @Test
    void deberiaObtenerUsuarioPorDocumento_CuandoExiste() {
        Usuario usuario = crearUsuarioConId(1L);
        when(usuarioRepositoryPort.obtenerUsuarioPorDocumento("12345678"))
                .thenReturn(Mono.just(usuario));
        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorDocumento("12345678"))
                .expectNext(usuario)
                .verifyComplete();
        verify(usuarioRepositoryPort).obtenerUsuarioPorDocumento("12345678");
    }

    @Test
    void deberiaRetornarVacio_CuandoUsuarioNoExistePorDocumento() {
        when(usuarioRepositoryPort.obtenerUsuarioPorDocumento("00000000"))
                .thenReturn(Mono.empty());
        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorDocumento("00000000"))
                .verifyComplete();
        verify(usuarioRepositoryPort).obtenerUsuarioPorDocumento("00000000");
    }

    @Test
    void deberiaLanzarError_CuandoObtencionPorDocumentoFalla() {
        RuntimeException error = new RuntimeException("Error al buscar por documento");
        when(usuarioRepositoryPort.obtenerUsuarioPorDocumento("errorDoc"))
                .thenReturn(Mono.error(error));
        StepVerifier.create(usuarioUseCase.obtenerUsuarioPorDocumento("errorDoc"))
                .expectError(RuntimeException.class)
                .verify();
        verify(usuarioRepositoryPort).obtenerUsuarioPorDocumento("errorDoc");
    }
}
