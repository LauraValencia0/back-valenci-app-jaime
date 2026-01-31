package com.valenci.repositorios;

import com.valenci.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioProducto extends JpaRepository<Producto, Integer> {

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.proveedor")
    @Override
    List<Producto> findAll();

    // --- MEJORA DE RENDIMIENTO Y CONSISTENCIA ---
    // Hacemos lo mismo para findById para asegurar que el proveedor siempre venga
    // en la misma consulta, evitando problemas de carga perezosa en la vista de detalle.
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.proveedor WHERE p.idProducto = :id")
    @Override
    Optional<Producto> findById(Integer id);
}