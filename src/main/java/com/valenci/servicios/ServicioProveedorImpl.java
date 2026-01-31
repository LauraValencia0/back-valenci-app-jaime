package com.valenci.servicios;

import com.valenci.entidades.Proveedor;
import com.valenci.repositorios.RepositorioProveedor;
import com.valenci.repositorios.RepositorioUsuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ServicioProveedorImpl implements ServicioProveedor {

    private final RepositorioProveedor repositorioProveedor;
    private final RepositorioUsuario repositorioUsuario; // Aún lo necesitamos para buscar por correo
    private final PasswordEncoder codificadorDeContrasena;

    public ServicioProveedorImpl(RepositorioProveedor repositorioProveedor, RepositorioUsuario repositorioUsuario, PasswordEncoder codificadorDeContrasena) {
        this.repositorioProveedor = repositorioProveedor;
        this.repositorioUsuario = repositorioUsuario;
        this.codificadorDeContrasena = codificadorDeContrasena;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> listarTodos() {
        log.info("Listando todos los proveedores desde la base de datos.");
        // ¡Mucho más eficiente! La consulta se hace directamente en la base de datos.
        return repositorioProveedor.findAllProveedores();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Proveedor> buscarPorId(int id) {
        log.debug("Buscando proveedor por ID: {}", id);
        return repositorioProveedor.findById(id);
    }

    @Override
    @Transactional
    public Proveedor crear(Proveedor proveedor) {
        log.info("Iniciando creación de proveedor con correo: {}", proveedor.getCorreo());
        repositorioUsuario.findByCorreo(proveedor.getCorreo()).ifPresent(u -> {
            log.warn("Conflicto: El correo {} ya está registrado.", proveedor.getCorreo());
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        });
        proveedor.setContrasena(codificadorDeContrasena.encode(proveedor.getContrasena()));
        Proveedor proveedorGuardado = repositorioProveedor.save(proveedor);
        log.info("Proveedor '{}' creado con ID: {}", proveedorGuardado.getNombreEmpresa(), proveedorGuardado.getId());
        return proveedorGuardado;
    }

    @Override
    @Transactional
    public Proveedor actualizar(int id, Proveedor datosNuevos) {
        log.info("Iniciando actualización del proveedor con ID: {}", id);
        Proveedor proveedorExistente = repositorioProveedor.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró el proveedor con ID: {} para actualizar.", id);
                    return new IllegalArgumentException("Proveedor no encontrado con ID: " + id);
                });

        proveedorExistente.setNombre(datosNuevos.getNombre());
        proveedorExistente.setCorreo(datosNuevos.getCorreo());
        proveedorExistente.setNombreEmpresa(datosNuevos.getNombreEmpresa());

        if (datosNuevos.getContrasena() != null && !datosNuevos.getContrasena().isEmpty()) {
            proveedorExistente.setContrasena(codificadorDeContrasena.encode(datosNuevos.getContrasena()));
        }

        return repositorioProveedor.save(proveedorExistente);
    }

    @Override
    @Transactional
    public void eliminarPorId(int id) {
        log.info("Iniciando eliminación del proveedor con ID: {}", id);
        if (!repositorioProveedor.existsById(id)) {
            log.warn("Intento de eliminar un proveedor no existente con ID: {}", id);
            throw new IllegalArgumentException("Proveedor no encontrado con ID: " + id);
        }
        repositorioProveedor.deleteById(id);
        log.info("Proveedor con ID: {} eliminado correctamente.", id);
    }
}