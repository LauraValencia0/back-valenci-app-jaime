package com.valenci.dto;

import com.valenci.entidades.EstadoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoPedidoHistorial {
    private int idPedido;
    private LocalDateTime fechaPedido;
    private BigDecimal totalPedido;
    // CORRECCIÓN: Cambiado a String para consistencia en las respuestas JSON
    private String estadoPedido;
    private DtoResumenFactura factura;
    private List<DtoRespuestaDetallePedido> detalles;
}
