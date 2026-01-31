package com.valenci.servicios;

import com.valenci.entidades.Factura;
import com.valenci.entidades.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de gestión de Facturas.
 * Define un contrato claro para todas las operaciones relacionadas con facturas,
 * separando esta responsabilidad del servicio de pedidos.
 */
public interface ServicioFactura {

    /**
     * Crea y guarda una nueva factura asociada a un pedido que ha sido pagado.
     * Esta es la operación de ESCRITURA.
     * @param pedido El pedido ya pagado para el cual se generará la factura.
     * @return La entidad Factura recién creada y guardada.
     */
    Factura crearFacturaParaPedido(Pedido pedido);

    /**
     * Busca una factura por su ID.
     * @param idFactura El ID de la factura a buscar.
     * @return Un Optional con la Factura si se encuentra.
     */
    Optional<Factura> buscarPorId(int idFactura);

    /**
     * Busca una factura asociada a un ID de pedido específico.
     * @param idPedido El ID del pedido.
     * @return Un Optional con la Factura si se encuentra.
     */
    Optional<Factura> buscarPorIdPedido(int idPedido);

    /**
     * Lista todas las facturas del sistema.
     * @return Una lista de todas las entidades Factura.
     */
    List<Factura> listarTodas();

    /**
     * Lista todas las facturas de un cliente específico.
     * @param idCliente El ID del cliente.
     * @return Una lista de las facturas del cliente.
     */
    List<Factura> listarPorCliente(int idCliente);
}
