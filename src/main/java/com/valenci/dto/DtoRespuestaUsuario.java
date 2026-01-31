package com.valenci.dto;

import lombok.Data;

/**
 * DTO para devolver información pública y segura de un usuario (sin contraseña).
 */
@Data
public class DtoRespuestaUsuario {
    private int id;
    private String nombre;
    private String correo;
    private String rol;
    // Campos específicos que se llenarán según el rol del usuario
    private String direccionEnvio;
    private String nombreEmpresa;
}
