package com.valenci.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CLIENTE")
// --- CORRECCIÓN ---
// Reemplazamos @Data y @EqualsAndHashCode para evitar problemas con JPA.
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Usuario {

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    // Constructor simplificado para la creación de nuevas entidades.
    public Cliente(String nombre, String correo, String contrasena, String direccionEnvio) {
        super(nombre, correo, contrasena);
        this.direccionEnvio = direccionEnvio;
    }
}