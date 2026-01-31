package com.valenci.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoRespuestaFactura {
    private int idFactura;
    private int idPedido;
    private LocalDateTime fechaFactura;
    private String nombreCliente;
    private BigDecimal totalFactura;
    private BigDecimal iva;
}
