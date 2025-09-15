package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.in.IUsuarioServicePort;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.UsuarioRequestDto;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.dto.UsuarioResponseDto;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper.IUsuarioRequestMapper;
import com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.mapper.IUsuarioResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
public class UsuarioController {

    private final IUsuarioServicePort usuarioServicePort;
    private final IUsuarioRequestMapper usuarioRequestMapper;
    private final IUsuarioResponseMapper usuarioResponseMapper;

    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un nuevo usuario en el sistema validando que el correo no esté previamente registrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El correo electrónico ya está registrado")
    })
    @PostMapping
    public Mono<ResponseEntity<UsuarioResponseDto>> registrarUsuario(@Valid @RequestBody UsuarioRequestDto usuarioRequestDto) {
        log.info("Solicitud de registro de usuario recibida para el correo: {}", usuarioRequestDto.getCorreoElectronico());
        
        return Mono.just(usuarioRequestDto)
                .map(usuarioRequestMapper::toUsuario)
                .flatMap(usuarioServicePort::registrarUsuario)
                .map(usuario -> {
                    log.info("Usuario registrado exitosamente con ID: {}", usuario.getId());
                    UsuarioResponseDto response = usuarioResponseMapper.toSuccessResponse(usuario);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                });
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Obtiene la lista de todos los usuarios registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping
    public Mono<ResponseEntity<?>> obtenerTodosLosUsuarios() {
        log.info("Solicitud para obtener todos los usuarios");
        // Validar autenticación y rol
        // Suponiendo que existe un método para obtener el usuario autenticado y su rol
        // Si no está autenticado o el rol no es ADMIN/ASESOR, lanzar excepción personalizada
        return reactor.core.publisher.Mono.defer(() ->
            reactor.core.publisher.Mono.justOrEmpty(org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext())
                .flatMap(contextMono -> contextMono)
                .map(securityContext -> securityContext.getAuthentication())
                .cast(org.springframework.security.core.Authentication.class)
                .map(auth -> {
                    Object principal = auth.getPrincipal();
                    log.info("Principal class: {}", principal != null ? principal.getClass().getName() : "null");
                    if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
                        String rol = jwt.getClaimAsString("rol");
                        log.info("JWT claim 'rol': {}", rol);
                        String rolSinPrefijo = rol != null ? rol.toUpperCase().replace("ROLE_", "") : null;
                        if (rolSinPrefijo == null || !(rolSinPrefijo.equals("ADMIN") || rolSinPrefijo.equals("ASESOR"))) {
                            throw new com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException("Proceso no autorizado: solo usuarios con rol ADMIN o ASESOR pueden consultar el listado de usuarios.");
                        }
                        return true;
                    } else {
                        log.warn("Principal no es instancia de Jwt o no autenticado. Principal: {}", principal);
                        throw new com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException("Proceso no autorizado: solo usuarios con rol ADMIN o ASESOR pueden consultar el listado de usuarios.");
                    }
                })
                .flatMap(valid -> usuarioServicePort.obtenerTodosLosUsuarios().collectList().map(ResponseEntity::ok))
        );
    }

    // Método auxiliar para obtener el rol del usuario autenticado
    private String obtenerRolDesdeJwt() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            return jwt.getClaimAsString("rol");
        }
        return null;
    }

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario específico por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Usuario>> obtenerUsuarioPorId(@PathVariable Long id) {
        log.info("Solicitud para obtener usuario con ID: {}", id);
        return org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .map(auth -> {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
                        String rol = (String) jwt.getClaim("rol");
                        if (!"ADMIN".equals(rol) && !"ASESOR".equals(rol)) {
                            throw new com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException("Acceso denegado: solo ADMIN o ASESOR pueden consultar por ID");
                        }
                    }
                    return true;
                })
                .then(usuarioServicePort.obtenerUsuarioPorId(id))
                .map(usuario -> {
                    log.info("Usuario encontrado con ID: {}", id);
                    return ResponseEntity.ok(usuario);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener usuario por correo", description = "Obtiene un usuario específico por su correo electrónico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/correo/{correo}")
    public Mono<ResponseEntity<Usuario>> obtenerUsuarioPorCorreo(@PathVariable String correo) {
        log.info("Solicitud para obtener usuario con correo: {}", correo);
        return org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .map(auth -> {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
                        String rol = (String) jwt.getClaim("rol");
                        if (!"ADMIN".equals(rol) && !"ASESOR".equals(rol)) {
                            throw new com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException("Acceso denegado: solo ADMIN o ASESOR pueden consultar por correo");
                        }
                    }
                    return true;
                })
                .then(usuarioServicePort.obtenerUsuarioPorCorreo(correo))
                .map(usuario -> {
                    log.info("Usuario encontrado con correo: {}", correo);
                    return ResponseEntity.ok(usuario);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener usuario por documento", 
               description = "Obtiene un usuario específico por su documento de identidad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/documento/{documento}")
    public Mono<ResponseEntity<Usuario>> obtenerUsuarioPorDocumento(@PathVariable String documento) {
        log.info("Solicitud para obtener usuario con documento: {}", documento);
        return org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .map(auth -> {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
                        String rol = (String) jwt.getClaim("rol");
                        String docJwt = (String) jwt.getClaim("documento");
                        if ("CLIENTE".equals(rol)) {
                            if (!documento.equals(docJwt)) {
                                throw new com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException("Acceso denegado: solo puedes consultar tu propio usuario por documento.");
                            }
                        } else if (!"ADMIN".equals(rol) && !"ASESOR".equals(rol)) {
                            throw new com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException("Acceso denegado: solo ADMIN, ASESOR o el propio CLIENTE pueden consultar por documento.");
                        }
                    }
                    return true;
                })
                .then(usuarioServicePort.obtenerUsuarioPorDocumento(documento))
                .map(usuario -> {
                    log.info("Usuario encontrado con documento {}: ID {}, Email {}", 
                        documento, usuario.getId(), usuario.getCorreoElectronico());
                    return ResponseEntity.ok(usuario);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
