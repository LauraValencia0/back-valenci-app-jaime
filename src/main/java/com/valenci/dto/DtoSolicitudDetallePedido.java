package com.valenci.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DtoSolicitudDetallePedido {
    @NotNull(message = "El ID del producto no puede ser nulo.")
    private Integer idProducto;

    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    private int cantidad;
}
