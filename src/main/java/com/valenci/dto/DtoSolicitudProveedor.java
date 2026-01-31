package com.valenci.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para recibir los datos para crear o actualizar un Proveedor.
 */
@Data
public class DtoSolicitudProveedor {
    private String nombre;
    private String correo;
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String contrasena;
    private String nombreEmpresa;
}