package com.valenci.servicios;

import com.valenci.entidades.Producto;
import com.valenci.repositorios.RepositorioProducto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ServicioProductoImpl implements ServicioProducto {

    private final RepositorioProducto repositorioProducto;

    public ServicioProductoImpl(RepositorioProducto repositorioProducto) {
        this.repositorioProducto = repositorioProducto;
    }

    @Override
    @Transactional
    public Producto crear(Producto producto) {
        // --- ¡CORRECCIÓN! ---
        // Primero, validamos que los datos de entrada no sean nulos.
        if (producto == null || producto.getNombreProducto() == null || producto.getNombreProducto().isEmpty()) {
            log.warn("Intento de crear un producto con datos nulos o nombre vacío.");
            throw new IllegalArgumentException("El producto o su nombre no pueden ser nulos.");
        }

        // Ahora que sabemos que 'producto' y 'nombreProducto' no son nulos, podemos registrar de forma segura.
        log.info("Iniciando proceso para crear un nuevo producto con nombre: '{}'", producto.getNombreProducto());

        Producto productoGuardado = repositorioProducto.save(producto);
        log.info("Producto '{}' creado exitosamente con ID: {}", productoGuardado.getNombreProducto(), productoGuardado.getIdProducto());
        return productoGuardado;
    }

    @Override
    @Transactional
    public Producto actualizar(int idProducto, Producto productoConDatosNuevos) {
        log.info("Iniciando actualización para el producto con ID: {}", idProducto);

        Producto productoExistente = repositorioProducto.findById(idProducto)
                .orElseThrow(() -> {
                    log.warn("No se encontró el producto con ID: {} para actualizar.", idProducto);
                    return new IllegalArgumentException("El producto con ID " + idProducto + " no existe.");
                });

        log.debug("Producto encontrado: {}. Actualizando con nuevos datos...", productoExistente);
        productoExistente.setNombreProducto(productoConDatosNuevos.getNombreProducto());
        productoExistente.setDescripcion(productoConDatosNuevos.getDescripcion());
        productoExistente.setPrecio(productoConDatosNuevos.getPrecio());
        productoExistente.setCantidad(productoConDatosNuevos.getCantidad());
        productoExistente.setProveedor(productoConDatosNuevos.getProveedor());

        Producto productoActualizado = repositorioProducto.save(productoExistente);
        log.info("Producto con ID: {} actualizado correctamente.", idProducto);
        return productoActualizado;
    }

    @Override
    @Transactional
    public void eliminarPorId(int idProducto) {
        log.info("Iniciando eliminación del producto con ID: {}", idProducto);
        if (!repositorioProducto.existsById(idProducto)) {
            log.warn("Intento de eliminar un producto no existente con ID: {}", idProducto);
            throw new IllegalArgumentException("El producto con ID " + idProducto + " no existe.");
        }
        repositorioProducto.deleteById(idProducto);
        log.info("Producto con ID: {} eliminado correctamente.", idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(int idProducto) {
        log.debug("Buscando producto por ID: {}", idProducto);
        return repositorioProducto.findById(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        log.debug("Listando todos los productos desde el servicio.");
        return repositorioProducto.findAll();
    }
}

