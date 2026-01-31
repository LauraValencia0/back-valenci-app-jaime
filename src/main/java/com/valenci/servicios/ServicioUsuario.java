package com.valenci.servicios;

import com.valenci.entidades.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define el contrato para la lógica de negocio
 * relacionada con la gestión de usuarios.
 */
public interface ServicioUsuario {

    /**
     * Registra un nuevo usuario en el sistema.
     * La contraseña se hashea antes de guardarse.
     * @param usuario El nuevo usuario a registrar.
     */
    void registrar(Usuario usuario);

    /**
     * Actualiza la información de un usuario existente.
     * @param usuario El usuario con los datos actualizados.
     */
    void actualizar(Usuario usuario);

    /**
     * Busca un usuario por su ID.
     * @param id El ID del usuario.
     * @return Un Optional con el usuario si se encuentra.
     */
    Optional<Usuario> buscarPorId(int id);

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param correo El correo del usuario.
     * @return Un Optional con el usuario si se encuentra.
     */
    Optional<Usuario> buscarPorCorreo(String correo);

    /**
     * Obtiene una lista de todos los usuarios registrados.
     * @return Una lista de entidades Usuario.
     */
    List<Usuario> listarTodos();

    /**
     * Elimina un usuario por su ID.
     * @param id El ID del usuario a eliminar.
     */
    void eliminarPorId(int id);

    // --- ¡NUEVOS MÉTODOS! ---

    /**
     * Permite a un usuario cambiar su propia contraseña verificando la actual.
     */
    void cambiarContrasena(Usuario usuario, String contrasenaActual, String nuevaContrasena);

    /**
     * Permite a un administrador resetear la contraseña de cualquier usuario.
     */
    void adminResetearContrasena(int idUsuario, String nuevaContrasena);
}
