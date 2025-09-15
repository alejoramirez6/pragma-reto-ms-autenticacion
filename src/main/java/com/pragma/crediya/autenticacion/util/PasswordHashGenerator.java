package com.pragma.crediya.autenticacion.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("Contraseña: " + password);
        System.out.println("Hash BCrypt: " + hash);
        
        // Verificar que el hash funciona
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verificación: " + matches);
        
        // Verificar contra el hash que estamos usando actualmente
        String currentHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye/nrCz4Mzx8Mq8OA5W5nKvEr9V2Gn9sK";
        boolean currentMatches = encoder.matches(password, currentHash);
        System.out.println("Hash actual funciona: " + currentMatches);
    }
}
