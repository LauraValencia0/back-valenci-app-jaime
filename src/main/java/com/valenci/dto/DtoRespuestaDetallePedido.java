package com.valenci.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoRespuestaDetallePedido {
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
