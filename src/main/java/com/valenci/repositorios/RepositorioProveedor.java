package com.valenci.repositorios;

import com.valenci.entidades.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioProveedor extends JpaRepository<Proveedor, Integer> {

    /**
     * Esta consulta utiliza JPQL (Java Persistence Query Language) para seleccionar
     * entidades del tipo 'Proveedor'. JPA es lo suficientemente inteligente para traducir esto
     * a una consulta SQL eficiente: "SELECT * FROM usuarios WHERE rol = 'PROVEEDOR'".
     * Esto evita traer toda la tabla de usuarios a la memoria de Java.
     */
    @Query("SELECT p FROM Proveedor p")
    List<Proveedor> findAllProveedores();
}