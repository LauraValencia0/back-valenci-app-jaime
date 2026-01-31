package com.valenci.servicios;

import com.valenci.entidades.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Define el contrato para las operaciones de negocio relacionadas
 * con la gestión de Clientes por parte de un Administrador.
 */
public interface ServicioCliente {

    /**
     * Obtiene una lista de todos los usuarios con el rol de Cliente.
     * @return Una lista de entidades Cliente.
     */
    List<Cliente> listarTodos();

    /**
     * Busca un cliente específico por su ID.
     * @param id El ID del cliente a buscar.
     * @return Un Optional que contiene el Cliente si se encuentra, o vacío si no.
     */
    Optional<Cliente> buscarPorId(int id);

    /**
     * Actualiza la información de un cliente existente.
     * La contraseña solo se actualiza si se proporciona un nuevo valor.
     * @param id El ID del cliente a actualizar.
     * @param datosNuevos Un objeto Cliente con la nueva información.
     * @return El Cliente actualizado y guardado en la base de datos.
     */
    Cliente actualizar(int id, Cliente datosNuevos);

    /**
     * Elimina un cliente de la base de datos por su ID.
     * @param id El ID del cliente a eliminar.
     */
    void eliminarPorId(int id);
}
