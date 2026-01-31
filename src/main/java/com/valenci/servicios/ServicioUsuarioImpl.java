package com.valenci.servicios;

import com.valenci.entidades.Usuario;
import com.valenci.repositorios.RepositorioUsuario;
import lombok.extern.slf4j.Slf4j; // ¡Importante! La anotación para logging.
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j // <-- ¡LA MAGIA ESTÁ AQUÍ! Esta anotación de Lombok crea un objeto 'log' para nosotros.
public class ServicioUsuarioImpl implements ServicioUsuario {

    private final RepositorioUsuario repositorioUsuario;
    private final PasswordEncoder codificadorDeContrasena;

    public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario, PasswordEncoder codificadorDeContrasena) {
        this.repositorioUsuario = repositorioUsuario;
        this.codificadorDeContrasena = codificadorDeContrasena;
    }

    @Override
    @Transactional
    public void registrar(Usuario usuario) {
        log.info("Iniciando proceso de registro para el correo: {}", usuario.getCorreo());
        repositorioUsuario.findByCorreo(usuario.getCorreo()).ifPresent(u -> {
            log.warn("Intento de registro fallido: el correo {} ya existe.", usuario.getCorreo());
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        });

        usuario.setContrasena(codificadorDeContrasena.encode(usuario.getContrasena()));
        Usuario usuarioGuardado = repositorioUsuario.save(usuario);
        log.info("Usuario registrado exitosamente con ID: {}", usuarioGuardado.getId());
    }

    @Override
    @Transactional
    public void actualizar(Usuario usuario) {
        if (usuario == null || usuario.getId() == 0) {
            log.warn("Intento de actualización con datos nulos.");
            throw new IllegalArgumentException("El usuario o su ID no pueden ser nulos para actualizar.");
        }
        log.info("Iniciando actualización para el usuario con ID: {}", usuario.getId());
        Usuario usuarioExistente = repositorioUsuario.findById(usuario.getId())
                .orElseThrow(() -> {
                    log.warn("Intento de actualización fallido: Usuario con ID {} no encontrado.", usuario.getId());
                    return new IllegalArgumentException("El usuario con ID " + usuario.getId() + " no existe.");
                });

        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setCorreo(usuario.getCorreo());

        if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
            log.debug("Actualizando contraseña para el usuario ID: {}", usuario.getId());
            usuarioExistente.setContrasena(codificadorDeContrasena.encode(usuario.getContrasena()));
        }

        repositorioUsuario.save(usuarioExistente);
        log.info("Usuario con ID: {} actualizado correctamente.", usuarioExistente.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(int id) {
        log.debug("Buscando usuario por ID: {}", id);
        return repositorioUsuario.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        log.debug("Listando todos los usuarios.");
        return repositorioUsuario.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        log.debug("Buscando usuario por correo: {}", correo);
        return repositorioUsuario.findByCorreo(correo);
    }

    @Override
    @Transactional
    public void eliminarPorId(int id) {
        log.info("Iniciando eliminación del usuario con ID: {}", id);
        if (!repositorioUsuario.existsById(id)) {
            log.warn("Intento de eliminación fallido: Usuario con ID {} no encontrado.", id);
            throw new IllegalArgumentException("El usuario con ID " + id + " no existe.");
        }
        repositorioUsuario.deleteById(id);
        log.info("Usuario con ID: {} eliminado correctamente.", id);
    }

    @Override
    @Transactional
    public void cambiarContrasena(Usuario usuario, String contrasenaActual, String nuevaContrasena) {
        log.info("Iniciando cambio de contraseña para el usuario ID: {}", usuario.getId());

        // 1. Verificamos que la contraseña actual proporcionada coincida con la de la BD.
        if (!codificadorDeContrasena.matches(contrasenaActual, usuario.getPassword())) {
            log.warn("Intento fallido de cambio de contraseña para el usuario ID: {}. Contraseña actual incorrecta.", usuario.getId());
            throw new BadCredentialsException("La contraseña actual es incorrecta.");
        }

        // 2. Encriptamos y guardamos la nueva contraseña.
        usuario.setContrasena(codificadorDeContrasena.encode(nuevaContrasena));
        repositorioUsuario.save(usuario);
        log.info("Contraseña actualizada exitosamente para el usuario ID: {}", usuario.getId());
    }

    @Override
    @Transactional
    public void adminResetearContrasena(int idUsuario, String nuevaContrasena) {
        log.info("Administrador reseteando contraseña para el usuario ID: {}", idUsuario);

        // 1. Buscamos al usuario que se va a modificar.
        Usuario usuario = repositorioUsuario.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario));

        // 2. Encriptamos y guardamos la nueva contraseña directamente.
        usuario.setContrasena(codificadorDeContrasena.encode(nuevaContrasena));
        repositorioUsuario.save(usuario);
        log.info("Contraseña reseteada por admin exitosamente para el usuario ID: {}", idUsuario);
    }
}
