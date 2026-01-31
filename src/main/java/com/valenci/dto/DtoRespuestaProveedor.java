package com.valenci.dto;

import lombok.Data;

/**
 * DTO para devolver la información pública de un proveedor.
 */
@Data
public class DtoRespuestaProveedor {
    private int id;
    private String nombre;
    private String correo;
    private String nombreEmpresa;
}