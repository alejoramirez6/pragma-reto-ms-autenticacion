package com.pragma.crediya.autenticacion.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder())))
            .authorizeExchange(exchanges -> exchanges
                // Permitir acceso libre a estos endpoints
                .pathMatchers("/api/v1/auth/login").permitAll()
                .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                // Todas las rutas de usuarios requieren autenticación, pero la validación de rol se hace en el controlador
                .pathMatchers("/api/v1/usuarios").authenticated()
                // Cualquier otra ruta requiere autenticación
                .anyExchange().authenticated()
            )
            .build();
        }

        private ReactiveAuthorizationManager<AuthorizationContext> rolesAdminOrAsesor() {
        return (Mono<Authentication> authentication, AuthorizationContext context) ->
            authentication
                .map(auth -> {
                    if (auth.getPrincipal() instanceof Jwt jwt) {
                    String rol = jwt.getClaimAsString("rol");
                    return new AuthorizationDecision("ADMIN".equals(rol) || "ASESOR".equals(rol));
                    }
                    return new AuthorizationDecision(false);
                });
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        // Decodificar desde Base64 para mantener consistencia
        byte[] secretBytes = java.util.Base64.getDecoder().decode(jwtSecret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
