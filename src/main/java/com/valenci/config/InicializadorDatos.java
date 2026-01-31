package com.valenci.config;

import com.valenci.entidades.Administrador;
import com.valenci.servicios.ServicioUsuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para inicializar datos esenciales al arrancar la aplicación.
 */
@Configuration
@Slf4j
public class InicializadorDatos {

    /**
     * Este Bean se ejecuta automáticamente al iniciar la aplicación.
     * Su propósito es verificar si el usuario administrador por defecto existe y,
     * si no, crearlo con una contraseña segura (hasheada).
     *
     * @param servicioUsuario El servicio para interactuar con los usuarios.
     * @return Un CommandLineRunner con la lógica de inicialización.
     */
    @Bean
    CommandLineRunner inicializarBaseDeDatos(ServicioUsuario servicioUsuario) {
        return args -> {
            String adminCorreo = "admin@valenci.com";
            log.info("Verificando existencia del usuario administrador por defecto...");

            if (servicioUsuario.buscarPorCorreo(adminCorreo).isEmpty()) {
                log.info("Usuario administrador no encontrado. Creando cuenta por defecto...");

                Administrador nuevoAdmin = new Administrador();
                nuevoAdmin.setNombre("Admin Principal");
                nuevoAdmin.setCorreo(adminCorreo);
                nuevoAdmin.setContrasena("admin123"); // El servicio se encargará de hashearla

                try {
                    servicioUsuario.registrar(nuevoAdmin);
                    log.info("¡Cuenta de administrador por defecto creada exitosamente!");
                } catch (Exception e) {
                    log.error("Error crítico al intentar crear la cuenta de administrador.", e);
                }
            } else {
                log.info("La cuenta de administrador ya existe. No se requieren acciones.");
            }
        };
    }
}
