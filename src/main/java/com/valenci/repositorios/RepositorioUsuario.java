package com.valenci.repositorios;

import com.valenci.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioUsuario extends JpaRepository<Usuario, Integer> {

    /**
     * Spring Data JPA genera automáticamente la consulta "WHERE correo = ?"
     * basándose en el nombre de este método.
     */
    Optional<Usuario> findByCorreo(String correo);
}

