package com.valenci.servicios;

import com.valenci.entidades.Proveedor;
import java.util.List;
import java.util.Optional;

public interface ServicioProveedor {
    Proveedor crear(Proveedor proveedor);
    Proveedor actualizar(int id, Proveedor datosNuevos);
    void eliminarPorId(int id);
    Optional<Proveedor> buscarPorId(int id);
    List<Proveedor> listarTodos();
}