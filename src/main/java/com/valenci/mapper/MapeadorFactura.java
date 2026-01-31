package com.valenci.mapper;

import com.valenci.entidades.Factura;
import com.valenci.dto.DtoRespuestaFactura;

public class MapeadorFactura {

    /**
     * Convierte una entidad Factura a su DTO de respuesta.
     */
    public static DtoRespuestaFactura aDto(Factura factura) {
        if (factura == null) {
            return null;
        }

        DtoRespuestaFactura dto = new DtoRespuestaFactura();
        dto.setIdFactura(factura.getIdFactura());
        dto.setFechaFactura(factura.getFechaFactura());
        dto.setTotalFactura(factura.getTotalFactura());
        dto.setIva(factura.getIva());

        if (factura.getPedido() != null) {
            dto.setIdPedido(factura.getPedido().getIdPedido());
            if (factura.getPedido().getCliente() != null) {
                dto.setNombreCliente(factura.getPedido().getCliente().getNombre());
            }
        }

        return dto;
    }
}
