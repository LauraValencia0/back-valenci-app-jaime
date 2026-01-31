package com.valenci.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("PROVEEDOR")
// Reemplazamos @Data por anotaciones específicas
@Getter
@Setter
@NoArgsConstructor
public class Proveedor extends Usuario {

    @Column(name = "nombre_empresa")
    private String nombreEmpresa;

    // Simplificamos el constructor, ya que el rol es manejado por el DiscriminatorValue
    public Proveedor(String nombre, String correo, String contrasena, String nombreEmpresa) {
        super(nombre, correo, contrasena); // Llamamos a un constructor más limpio en la clase padre
        this.nombreEmpresa = nombreEmpresa;
    }
}