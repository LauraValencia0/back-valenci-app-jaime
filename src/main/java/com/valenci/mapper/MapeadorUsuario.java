package com.valenci.mapper;

import com.valenci.entidades.Cliente;
import com.valenci.entidades.Proveedor;
import com.valenci.entidades.Usuario;
import com.valenci.dto.DtoSolicitudCliente;
import com.valenci.dto.DtoRespuestaUsuario;

import java.util.List;
import java.util.stream.Collectors;

public class MapeadorUsuario {

    public static Cliente aEntidadCliente(DtoSolicitudCliente dto) {
        if (dto == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setCorreo(dto.getCorreo());
        cliente.setContrasena(dto.getContrasena());
        cliente.setDireccionEnvio(dto.getDireccionEnvio());
        return cliente;
    }

    public static DtoRespuestaUsuario aDtoRespuesta(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        DtoRespuestaUsuario dto = new DtoRespuestaUsuario();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol());

        if (usuario instanceof Cliente) {
            dto.setDireccionEnvio(((Cliente) usuario).getDireccionEnvio());
        }

        if (usuario instanceof Proveedor) {
            dto.setNombreEmpresa(((Proveedor) usuario).getNombreEmpresa());
        }

        return dto;
    }

    /**
     * --- CORRECCIÓN ---
     * Ahora acepta una lista de cualquier clase que herede de Usuario (List<? extends Usuario>),
     * lo que la hace compatible con List<Cliente>, List<Proveedor>, etc.
     */
    public static List<DtoRespuestaUsuario> aListaDtoRespuesta(List<? extends Usuario> usuarios) {
        return usuarios.stream()
                .map(MapeadorUsuario::aDtoRespuesta)
                .collect(Collectors.toList());
    }
}

