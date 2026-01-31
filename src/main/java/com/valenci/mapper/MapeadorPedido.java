package com.valenci.mapper;

import com.valenci.dto.DtoResumenFactura;
import com.valenci.entidades.DetallePedido;
import com.valenci.entidades.Factura;
import com.valenci.entidades.Pedido;
import com.valenci.dto.DtoRespuestaDetallePedido;
import com.valenci.dto.DtoPedidoHistorial;
import com.valenci.dto.DtoRespuestaPedido;

import java.math.BigDecimal; // Importamos BigDecimal
import java.util.Collections;
import java.util.stream.Collectors;

public class MapeadorPedido {

    // ... (otros métodos sin cambios)

    public static DtoRespuestaPedido aDtoRespuesta(Pedido pedido) {
        if (pedido == null) return null;

        DtoRespuestaPedido dto = new DtoRespuestaPedido();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setEstadoPedido(pedido.getEstadoPedido());
        dto.setNombreCliente(pedido.getCliente().getNombre());
        dto.setTotalPedido(pedido.getTotalPedido());

        if (pedido.getDetalles() != null) {
            dto.setDetalles(pedido.getDetalles().stream()
                    .map(MapeadorPedido::aDtoRespuestaDetalle)
                    .collect(Collectors.toList()));
        } else {
            dto.setDetalles(Collections.emptyList());
        }
        return dto;
    }

    public static DtoRespuestaDetallePedido aDtoRespuestaDetalle(DetallePedido detalle) {
        if (detalle == null) return null;

        DtoRespuestaDetallePedido dto = new DtoRespuestaDetallePedido();
        if (detalle.getProducto() != null) {
            dto.setNombreProducto(detalle.getProducto().getNombreProducto());
        }
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }


    /**
     * MÉTODO "BLINDADO": Ahora maneja facturas antiguas con IVA nulo.
     */
    public static DtoPedidoHistorial aDtoHistorial(Pedido pedido, Factura factura) {
        if (pedido == null) return null;

        DtoPedidoHistorial dto = new DtoPedidoHistorial();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setTotalPedido(pedido.getTotalPedido());
        dto.setEstadoPedido(pedido.getEstadoPedido().name());

        if (factura != null) {
            // --- ¡LÓGICA DEFENSIVA! ---
            // Explicación: Verificamos si el IVA de la factura es nulo.
            // Si lo es, usamos BigDecimal.ZERO como valor por defecto.
            // Si no, usamos el valor real de la factura.
            BigDecimal ivaSeguro = factura.getIva() != null ? factura.getIva() : BigDecimal.ZERO;

            dto.setFactura(new DtoResumenFactura(
                    factura.getIdFactura(),
                    factura.getFechaFactura(),
                    factura.getTotalFactura(),
                    ivaSeguro // <-- Pasamos el valor seguro
            ));
        }

        if (pedido.getDetalles() != null) {
            dto.setDetalles(pedido.getDetalles().stream()
                    .map(MapeadorPedido::aDtoRespuestaDetalle)
                    .collect(Collectors.toList()));
        } else {
            dto.setDetalles(Collections.emptyList());
        }

        return dto;
    }
}

