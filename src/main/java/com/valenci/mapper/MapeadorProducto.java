package com.valenci.mapper;

import com.valenci.dto.DtoRespuestaProducto;
import com.valenci.dto.DtoSolicitudProducto;
import com.valenci.entidades.Producto;

import java.util.List;
import java.util.stream.Collectors;

public class MapeadorProducto {

    public static Producto aEntidad(DtoSolicitudProducto dto) {
        if (dto == null) {
            return null;
        }
        Producto entidad = new Producto();
        entidad.setNombreProducto(dto.getNombre());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setPrecio(dto.getPrecio());
        entidad.setCantidad(dto.getCantidad());
        // El proveedor se asigna en el controlador/servicio
        return entidad;
    }

    public static DtoRespuestaProducto aDto(Producto entidad) {
        if (entidad == null) {
            return null;
        }
        String nombreProveedor = (entidad.getProveedor() != null) ? entidad.getProveedor().getNombre() : "Sin proveedor";
        return new DtoRespuestaProducto(
                entidad.getIdProducto(),
                entidad.getNombreProducto(),
                entidad.getDescripcion(),
                entidad.getPrecio(),
                entidad.getCantidad(),
                nombreProveedor
        );
    }

    public static List<DtoRespuestaProducto> aListaDto(List<Producto> entidades) {
        return entidades.stream()
                .map(MapeadorProducto::aDto)
                .collect(Collectors.toList());
    }
}
