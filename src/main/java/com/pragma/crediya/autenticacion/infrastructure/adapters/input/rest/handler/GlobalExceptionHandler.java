package com.pragma.crediya.autenticacion.infrastructure.adapters.input.rest.handler;

import com.pragma.crediya.autenticacion.domain.exception.AccesoDenegadoException;
import com.pragma.crediya.autenticacion.domain.exception.CredencialesInvalidasException;
import com.pragma.crediya.autenticacion.domain.exception.ErrorAutenticacionException;
import com.pragma.crediya.autenticacion.domain.exception.UsuarioYaExisteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        log.warn("Intento de autenticación fallido: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", "Credenciales inválidas");
        response.put("message", "Las credenciales proporcionadas no son válidas");
    response.put("message", "Usuario o contraseña incorrectos. Por favor, verifica tus datos e intenta nuevamente.");
    response.put("accion_requerida", "Verifica tu correo y clave. Si el problema persiste, contacta al soporte.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ErrorAutenticacionException.class)
    public ResponseEntity<Map<String, Object>> handleErrorAutenticacion(ErrorAutenticacionException ex) {
        log.error("Error en proceso de autenticación: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error de autenticación");
        response.put("message", "Ha ocurrido un error durante el proceso de autenticación. Inténtelo nuevamente.");
    response.put("message", "Hubo un problema al iniciar sesión. Por favor, intenta más tarde o contacta al soporte si el problema persiste.");
    response.put("accion_requerida", "Intenta nuevamente en unos minutos. Si el error continúa, contacta al soporte.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<Map<String, Object>> handleAccesoDenegado(AccesoDenegadoException ex) {
    log.warn("Acceso denegado: {}", ex.getMessage());
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.FORBIDDEN.value());
    response.put("error", "Acceso Denegado");
    response.put("message", ex.getMessage());
    response.put("accion_requerida", "Inicia sesión con una cuenta que tenga rol ADMIN o ASESOR, o solicita permisos a tu administrador.");
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioYaExiste(UsuarioYaExisteException ex) {
        log.error("Error: Usuario ya existe - {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Usuario ya existe");
        response.put("message", ex.getMessage());
    response.put("message", "El correo electrónico ya está registrado. Si olvidaste tu contraseña, contacta al soporte.");
    response.put("accion_requerida", "Utiliza otro correo o solicita ayuda para recuperar tu acceso.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(WebExchangeBindException ex) {
        log.error("Error de validación: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Errores de validación");
        response.put("validationErrors", errors);
    response.put("message", "Por favor, revisa los campos marcados y corrige los errores antes de continuar.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DecodingException.class)
    public ResponseEntity<Map<String, Object>> handleDecodingException(DecodingException ex) {
        log.error("Error de decodificación JSON: {}", ex.getMessage());
        
        String message = "Error en el formato de los datos enviados";
        
        // Detectar si es un error de fecha específicamente
        if (ex.getMessage().contains("LocalDate") && ex.getMessage().contains("could not be parsed")) {
            message = "Formato de fecha inválido. Use el formato YYYY-MM-DD (ejemplo: 1998-08-26)";
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Error de formato");
        response.put("message", message);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Error interno del servidor: ", ex);
        
        String message = "Ha ocurrido un error inesperado. Por favor, inténtelo más tarde.";
        
        // Verificar si es un error de formato de fecha en la cadena de causas
        Throwable cause = ex;
        while (cause != null) {
            if (cause.getMessage() != null && 
                (cause.getMessage().contains("LocalDate") && cause.getMessage().contains("could not be parsed")) ||
                (cause.getMessage().contains("DateTimeParseException"))) {
                message = "Formato de fecha inválido. Use el formato YYYY-MM-DD (ejemplo: 1998-08-26)";
                break;
            }
            cause = cause.getCause();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", message.contains("fecha") ? "Error de formato" : "Error interno del servidor");
        response.put("message", message);
        
        HttpStatus status = message.contains("fecha") ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(response);
    }
}
