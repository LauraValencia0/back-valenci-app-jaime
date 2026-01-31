package com.valenci.repositorios;

import com.valenci.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioCliente extends JpaRepository<Cliente, Integer> {

    /**
     * Consulta optimizada que trae directamente solo los usuarios cuyo rol es 'CLIENTE'.
     */
    @Query("SELECT c FROM Cliente c")
    List<Cliente> findAllClientes();
}