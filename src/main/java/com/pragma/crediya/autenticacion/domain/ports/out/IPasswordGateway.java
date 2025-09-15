package com.pragma.crediya.autenticacion.domain.ports.out;

public interface IPasswordGateway {
    String encriptar(String contrasenaEnClaro);
    boolean validar(String contrasenaEnClaro, String contrasenaEncriptada);
}
