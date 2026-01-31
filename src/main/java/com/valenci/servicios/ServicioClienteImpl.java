package com.valenci.servicios;

import com.valenci.entidades.Cliente;
import com.valenci.repositorios.RepositorioCliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ServicioClienteImpl implements ServicioCliente {

    private final RepositorioCliente repositorioCliente;
    private final PasswordEncoder codificadorDeContrasena;

    public ServicioClienteImpl(RepositorioCliente repositorioCliente, PasswordEncoder passwordEncoder) {
        this.repositorioCliente = repositorioCliente;
        this.codificadorDeContrasena = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        log.info("Listando todos los clientes desde el servicio.");
        return repositorioCliente.findAllClientes();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(int id) {
        log.debug("Buscando cliente por ID: {}", id);
        return repositorioCliente.findById(id);
    }

    @Override
    @Transactional
    public Cliente actualizar(int id, Cliente datosNuevos) {
        log.info("Actualizando cliente con ID: {}", id);
        Cliente clienteExistente = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));

        clienteExistente.setNombre(datosNuevos.getNombre());
        clienteExistente.setCorreo(datosNuevos.getCorreo());
        clienteExistente.setDireccionEnvio(datosNuevos.getDireccionEnvio());

        // Solo actualiza la contraseña si se proporciona una nueva.
        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isEmpty()) {
            clienteExistente.setContrasena(codificadorDeContrasena.encode(datosNuevos.getPassword()));
        }

        return repositorioCliente.save(clienteExistente);
    }

    @Override
    @Transactional
    public void eliminarPorId(int id) {
        log.info("Eliminando cliente con ID: {}", id);
        if (!repositorioCliente.existsById(id)) {
            throw new IllegalArgumentException("Cliente no encontrado con ID: " + id);
        }
        repositorioCliente.deleteById(id);
    }

    // El método 'crear' se maneja a través del 'registrar' del ServicioUsuario, por lo que no es necesario aquí.
    // Los métodos de historial y pago se han omitido ya que pertenecen a otra lógica de negocio.
}