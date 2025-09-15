package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.UsuarioRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IUsuarioRequestMapperTest {

    private IUsuarioRequestMapper mapper;
    private UsuarioRequestDto usuarioRequestDto;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(IUsuarioRequestMapper.class);
        
        usuarioRequestDto = new UsuarioRequestDto();
        usuarioRequestDto.setNombres("Juan Carlos");
        usuarioRequestDto.setApellidos("Pérez González");
        usuarioRequestDto.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        usuarioRequestDto.setDireccion("Calle 123 #45-67");
        usuarioRequestDto.setTelefono("3001234567");
        usuarioRequestDto.setCorreoElectronico("juan.perez@email.com");
        usuarioRequestDto.setSalarioBase(2500000.0);
    }

    @Test
    void deberiaMapearCorrectamenteUsuarioRequestDtoAUsuario() {
        // When
        Usuario usuario = mapper.toUsuario(usuarioRequestDto);

        // Then
        assertNotNull(usuario);
        assertEquals(usuarioRequestDto.getNombres(), usuario.getNombres());
        assertEquals(usuarioRequestDto.getApellidos(), usuario.getApellidos());
        assertEquals(usuarioRequestDto.getFechaNacimiento(), usuario.getFechaNacimiento());
        assertEquals(usuarioRequestDto.getDireccion(), usuario.getDireccion());
        assertEquals(usuarioRequestDto.getTelefono(), usuario.getTelefono());
        assertEquals(usuarioRequestDto.getCorreoElectronico(), usuario.getCorreoElectronico());
        assertEquals(usuarioRequestDto.getSalarioBase(), usuario.getSalarioBase());
        
        // El ID debe ser null ya que viene del DTO de entrada
        assertNull(usuario.getId());
    }

    @Test
    void deberiaRetornarNull_CuandoUsuarioRequestDtoEsNull() {
        // When
        Usuario usuario = mapper.toUsuario(null);

        // Then
        assertNull(usuario);
    }

    @Test
    void deberiaMapearCorrectamente_CuandoCamposOpcionalesSonNull() {
        // Given
        usuarioRequestDto.setDireccion(null);
        usuarioRequestDto.setTelefono(null);

        // When
        Usuario usuario = mapper.toUsuario(usuarioRequestDto);

        // Then
        assertNotNull(usuario);
        assertEquals(usuarioRequestDto.getNombres(), usuario.getNombres());
        assertEquals(usuarioRequestDto.getApellidos(), usuario.getApellidos());
        assertEquals(usuarioRequestDto.getCorreoElectronico(), usuario.getCorreoElectronico());
        assertEquals(usuarioRequestDto.getSalarioBase(), usuario.getSalarioBase());
        
        // Campos opcionales deben ser null
        assertNull(usuario.getDireccion());
        assertNull(usuario.getTelefono());
    }

    @Test
    void deberiaMapearFechaCorrectamente() {
        // Given
        LocalDate fechaEspecifica = LocalDate.of(1985, 12, 25);
        usuarioRequestDto.setFechaNacimiento(fechaEspecifica);

        // When
        Usuario usuario = mapper.toUsuario(usuarioRequestDto);

        // Then
        assertEquals(fechaEspecifica, usuario.getFechaNacimiento());
        assertEquals(1985, usuario.getFechaNacimiento().getYear());
        assertEquals(12, usuario.getFechaNacimiento().getMonthValue());
        assertEquals(25, usuario.getFechaNacimiento().getDayOfMonth());
    }
}
