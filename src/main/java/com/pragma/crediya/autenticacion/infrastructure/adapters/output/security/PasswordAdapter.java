package com.pragma.crediya.autenticacion.infrastructure.adapters.output.security;

import com.pragma.crediya.autenticacion.domain.ports.out.IPasswordGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordAdapter implements IPasswordGateway {

    private final PasswordEncoder passwordEncoder;

    public PasswordAdapter() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encriptar(String contrasenaEnClaro) {
        return passwordEncoder.encode(contrasenaEnClaro);
    }

    @Override
    public boolean validar(String contrasenaEnClaro, String contrasenaEncriptada) {
        return passwordEncoder.matches(contrasenaEnClaro, contrasenaEncriptada);
    }
}
