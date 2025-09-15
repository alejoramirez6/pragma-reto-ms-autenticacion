package com.pragma.crediya.autenticacion.infrastructure.adapters.output.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordTest {
    
    @Test
    void testPasswordHashing() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String currentHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye/nrCz4Mzx8Mq8OA5W5nKvEr9V2Gn9sK";
        
        System.out.println("Contraseña: " + password);
        System.out.println("Hash actual: " + currentHash);
        
        // Generar nuevo hash
        String newHash = encoder.encode(password);
        System.out.println("Nuevo hash: " + newHash);
        
        // Verificar hash actual
        boolean currentMatches = encoder.matches(password, currentHash);
        System.out.println("Hash actual funciona: " + currentMatches);
        
        // Verificar nuevo hash
        boolean newMatches = encoder.matches(password, newHash);
        System.out.println("Nuevo hash funciona: " + newMatches);
        
        // Test con PasswordAdapter
        PasswordAdapter adapter = new PasswordAdapter();
        boolean adapterMatches = adapter.validar(password, currentHash);
        System.out.println("PasswordAdapter funciona: " + adapterMatches);
    }
}
