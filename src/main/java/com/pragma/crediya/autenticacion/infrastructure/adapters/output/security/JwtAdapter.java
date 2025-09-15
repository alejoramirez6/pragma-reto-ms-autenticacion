package com.pragma.crediya.autenticacion.infrastructure.adapters.output.security;

import com.pragma.crediya.autenticacion.domain.model.Usuario;
import com.pragma.crediya.autenticacion.domain.ports.out.IJwtGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtAdapter implements IJwtGateway {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private Long expiration;

    private SecretKey getSigningKey() {
        // Decodificar desde Base64 para mantener consistencia con la documentación
        byte[] secretBytes = java.util.Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    @Override
    public String generarToken(Usuario usuario) {
        log.info("Generando token JWT para usuario: {}", usuario.getCorreoElectronico());
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("correo", usuario.getCorreoElectronico());
        claims.put("rol", usuario.getRol());
        claims.put("documento", usuario.getDocumentoIdentidad());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getCorreoElectronico())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String obtenerCorreoDeToken(String token) {
        Claims claims = obtenerClaims(token);
        return claims.get("correo", String.class);
    }

    @Override
    public String obtenerRolDeToken(String token) {
        Claims claims = obtenerClaims(token);
        return claims.get("rol", String.class);
    }

    @Override
    public String obtenerDocumentoDeToken(String token) {
        Claims claims = obtenerClaims(token);
        return claims.get("documento", String.class);
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
