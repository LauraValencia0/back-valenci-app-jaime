package com.valenci.repositorios;

import com.valenci.entidades.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioFactura extends JpaRepository<Factura, Integer> {

    /**
     * Busca una factura por su ID, trayendo también el Pedido y el Cliente asociados.
     */
    @Query("SELECT f FROM Factura f JOIN FETCH f.pedido p JOIN FETCH p.cliente WHERE f.idFactura = :id")
    @Override
    Optional<Factura> findById(@Param("id") Integer id);

    /**
     * Busca todas las facturas, trayendo también el Pedido y el Cliente de cada una.
     */
    @Query("SELECT f FROM Factura f JOIN FETCH f.pedido p JOIN FETCH p.cliente")
    @Override
    List<Factura> findAll();

    // La ruta es: Factura -> pedido -> idPedido
    Optional<Factura> findByPedidoIdPedido(int idPedido);

    /**
     * Busca todas las facturas de un cliente específico, trayendo también el Pedido asociado.
     */
    @Query("SELECT f FROM Factura f JOIN FETCH f.pedido p WHERE p.cliente.id = :idCliente")
    List<Factura> findByPedidoClienteId(@Param("idCliente") int idCliente);
}
