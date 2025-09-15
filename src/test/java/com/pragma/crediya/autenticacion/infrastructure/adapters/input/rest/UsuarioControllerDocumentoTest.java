
package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.in.IUsuarioServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.mockito.Mockito.when;
import org.springframework.context.annotation.Import;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper.IUsuarioRequestMapper;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper.IUsuarioResponseMapper;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(UsuarioController.class)
@ActiveProfiles("test")
@Import({IUsuarioRequestMapper.class, IUsuarioResponseMapper.class, NoSecurityConfig.class})
public class UsuarioControllerDocumentoTest {

        @Autowired
        private WebTestClient webTestClient;

        @MockBean
        private IUsuarioServicePort usuarioServicePort;
        @MockBean
        private IUsuarioRequestMapper usuarioRequestMapper;
        @MockBean
        private IUsuarioResponseMapper usuarioResponseMapper;

    @Test
    void deberiaObtenerUsuarioPorDocumento_CuandoUsuarioExiste() {
        // Given
        String documento = "12345678";
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombres("Juan Carlos");
        usuario.setApellidos("Pérez García");
        usuario.setDocumentoIdentidad(documento);
        usuario.setCorreoElectronico("juan.perez@test.com");
        usuario.setSalarioBase(2500000.0);

        when(usuarioServicePort.obtenerUsuarioPorDocumento(documento))
                .thenReturn(Mono.just(usuario));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/usuarios/documento/{documento}", documento)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.documentoIdentidad").isEqualTo(documento)
                .jsonPath("$.correoElectronico").isEqualTo("juan.perez@test.com")
                .jsonPath("$.nombres").isEqualTo("Juan Carlos")
                .jsonPath("$.apellidos").isEqualTo("Pérez García")
                .jsonPath("$.salarioBase").isEqualTo(2500000.0);
    }

    @Test
    void deberiaRetornar404_CuandoUsuarioNoExistePorDocumento() {
        // Given
        String documento = "99999999";
        
        when(usuarioServicePort.obtenerUsuarioPorDocumento(documento))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/usuarios/documento/{documento}", documento)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deberiaRetornar500_CuandoOcurreErrorInternoAlBuscarPorDocumento() {
        // Given
        String documento = "12345678";
        
        when(usuarioServicePort.obtenerUsuarioPorDocumento(documento))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/usuarios/documento/{documento}", documento)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
