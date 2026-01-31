package com.valenci.dto;

import lombok.Data;

/**
 * DTO para recibir las credenciales de inicio de sesión (correo y contraseña).
 */
@Data
public class DtoSolicitudAutenticacion {
    private String correo;
    private String contrasena;
}

