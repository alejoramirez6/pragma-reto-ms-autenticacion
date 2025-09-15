package com.pragma.crediya.autenticacion.infrastructure.adapters.output.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

import java.time.LocalDate;

@Table("usuarios")
@Data
public class UsuarioEntity {

    @Id
    private Long id;

    private String nombres;
    private String apellidos;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String direccion;
    private String telefono;

    @Column("correo_electronico")
    private String correoElectronico;

    @Column("salario_base")
    private Double salarioBase;

    private String contrasena;
    private String rol;
}
