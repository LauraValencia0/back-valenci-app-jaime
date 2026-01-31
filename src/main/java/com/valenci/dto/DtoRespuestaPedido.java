package com.valenci.dto;

import com.valenci.entidades.EstadoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoRespuestaPedido {
    private int idPedido;
    private LocalDateTime fechaPedido;
    private EstadoPedido estadoPedido;
    private String nombreCliente;
    private BigDecimal totalPedido;
    private List<DtoRespuestaDetallePedido> detalles;
}
