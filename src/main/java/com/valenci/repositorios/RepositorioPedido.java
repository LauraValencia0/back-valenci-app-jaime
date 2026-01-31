package com.valenci.repositorios;

import com.valenci.entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioPedido extends JpaRepository<Pedido, Integer> {

    // Este método es simple y rápido, útil para cuando solo necesitas la lista de pedidos
    // pero no sus detalles internos. Lo dejamos porque puede ser útil en otros lugares.
    List<Pedido> findByClienteId(int idCliente);

    List<Pedido> findByEstadoPedido(com.valenci.entidades.EstadoPedido estado);

    @Query("SELECT p FROM Pedido p WHERE DATE(p.fechaPedido) = :fecha")
    List<Pedido> findByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT p FROM Pedido p JOIN p.detalles d WHERE d.producto.id = :idProducto")
    List<Pedido> findByProductoId(@Param("idProducto") int idProducto);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.idPedido = :id")
    @Override
    Optional<Pedido> findById(@Param("id") Integer id);

    // --- ¡NUEVO MÉTODO OPTIMIZADO PARA EL HISTORIAL! ---
    /**
     * Explicación Pedagógica (La Solución Definitiva):
     * Este método resuelve la LazyInitializationException para el historial del cliente.
     * - "SELECT DISTINCT p": Seleccionamos los pedidos, evitando duplicados.
     * - "FROM Pedido p": Empezamos por la entidad Pedido.
     * - "LEFT JOIN FETCH p.detalles d": Traemos inmediatamente (FETCH) todos los 'detalles' de cada pedido. Usamos LEFT JOIN por si un pedido pudiera no tener detalles.
     * - "LEFT JOIN FETCH d.producto": Para cada detalle 'd' que trajimos, traemos inmediatamente (FETCH) el 'producto' asociado.
     * - "WHERE p.cliente.id = :idCliente": Filtramos todo por el ID del cliente.
     * El resultado es una única consulta a la base de datos que devuelve los Pedidos con todas sus relaciones necesarias ya cargadas en memoria.
     */
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto WHERE p.cliente.id = :idCliente")
    List<Pedido> findAllByClienteIdWithDetalles(@Param("idCliente") int idCliente);
}
