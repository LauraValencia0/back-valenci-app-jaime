package com.valenci.dto;

import lombok.Data;

/**
 * DTO para recibir los datos necesarios para registrar un nuevo cliente.
 */
@Data
public class DtoSolicitudCliente {
    private String nombre;
    private String correo;
    private String contrasena;
    private String direccionEnvio;
}