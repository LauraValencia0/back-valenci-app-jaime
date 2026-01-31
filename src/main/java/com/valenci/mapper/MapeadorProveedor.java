package com.valenci.mapper;

import com.valenci.entidades.Proveedor;
import com.valenci.dto.DtoSolicitudProveedor;
import com.valenci.dto.DtoRespuestaProveedor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MapeadorProveedor {

    public static Proveedor aEntidad(DtoSolicitudProveedor dto) {
        if (dto == null) return null;
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setCorreo(dto.getCorreo());
        proveedor.setContrasena(dto.getContrasena());
        proveedor.setNombreEmpresa(dto.getNombreEmpresa());
        return proveedor;
    }

    public static DtoRespuestaProveedor aDtoRespuesta(Proveedor proveedor) {
        if (proveedor == null) return null;
        DtoRespuestaProveedor dto = new DtoRespuestaProveedor();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setCorreo(proveedor.getCorreo());
        dto.setNombreEmpresa(proveedor.getNombreEmpresa());
        return dto;
    }

    public static List<DtoRespuestaProveedor> aDtoRespuestaLista(List<Proveedor> proveedores) {
        if (proveedores == null) return Collections.emptyList();
        return proveedores.stream()
                .map(MapeadorProveedor::aDtoRespuesta)
                .collect(Collectors.toList());
    }
}

