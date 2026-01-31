package com.valenci.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO para recibir los datos necesarios para crear o actualizar un Producto.
 */
@Data
public class DtoSolicitudProducto {
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;
    private String descripcion;
    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser un valor positivo")
    private BigDecimal precio;
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private int cantidad;
    @NotNull(message = "El ID del proveedor es obligatorio")
    private int idProveedor;
}
