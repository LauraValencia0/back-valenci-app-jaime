package com.valenci.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoSolicitudActualizacionPerfil {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    private String direccionEnvio;

    // El teléfono es opcional
    private String telefono;
}