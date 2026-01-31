package com.valenci.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoCambioContrasena {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String contrasenaActual;

    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    private String nuevaContrasena;
}

