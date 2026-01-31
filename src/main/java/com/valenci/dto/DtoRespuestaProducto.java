package com.valenci.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para devolver la información pública de un producto, incluyendo el nombre del proveedor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoRespuestaProducto {
    private int id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int cantidad; // Añadido para dar información completa en la respuesta
    private String nombreProveedor;
}
