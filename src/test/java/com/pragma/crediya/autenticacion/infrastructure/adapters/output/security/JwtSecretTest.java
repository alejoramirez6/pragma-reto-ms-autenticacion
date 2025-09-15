package com.pragma.crediya.autenticacion.infrastructure.adapters.output.security;

import org.junit.jupiter.api.Test;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para verificar que la clave JWT Base64 se decodifica correctamente
 */
class JwtSecretTest {
    
    @Test
    void deberiaDecodificarClaveBase64Correctamente() {
        // Given - La clave tal como está en application.properties
        String claveBase64 = "cHJhZ21hQ3JlZGl5YU1pY3Jvc2VydmljaW9BdXRlbnRpY2FjaW9uU2VjcmV0S2V5MjAyNQ==";
        
        // When - Decodificamos la clave
        byte[] claveDecodificada = Base64.getDecoder().decode(claveBase64);
        String claveTexto = new String(claveDecodificada);
        
        // Then - Verificamos que la decodificación sea correcta
        assertNotNull(claveDecodificada);
        assertTrue(claveDecodificada.length > 0);
        assertEquals("pragmaCrediyaMicroservicioAutenticacionSecretKey2025", claveTexto);
        
        // Verificar que tiene suficiente entropía para HS256 (mínimo 256 bits = 32 bytes)
        assertTrue(claveDecodificada.length >= 32, 
            "La clave debe tener al menos 32 bytes para HS256");
        
        System.out.println("🔑 Clave Base64: " + claveBase64);
        System.out.println("📝 Clave decodificada: " + claveTexto);
        System.out.println("📏 Longitud en bytes: " + claveDecodificada.length);
        System.out.println("✅ Apta para HS256: " + (claveDecodificada.length >= 32));
    }
    
    @Test
    void deberiaValidarFormatoBase64() {
        String claveBase64 = "cHJhZ21hQ3JlZGl5YU1pY3Jvc2VydmljaW9BdXRlbnRpY2FjaW9uU2VjcmV0S2V5MjAyNQ==";
        
        // Verificar que es Base64 válido (no debe lanzar excepción)
        assertDoesNotThrow(() -> {
            Base64.getDecoder().decode(claveBase64);
        });
        
        // Verificar que termina con el padding correcto de Base64
        assertTrue(claveBase64.endsWith("==") || claveBase64.endsWith("=") || 
                  !claveBase64.contains("="));
    }
}
