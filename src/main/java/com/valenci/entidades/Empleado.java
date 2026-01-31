package com.valenci.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("EMPLEADO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Empleado extends Usuario {

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "salario")
    private BigDecimal salario;

    public Empleado(int id, String nombre, String correo, String contrasena, String cargo, BigDecimal salario) {
        super(id, nombre, correo, contrasena, null);
        this.cargo = cargo;
        this.salario = salario;
    }
}