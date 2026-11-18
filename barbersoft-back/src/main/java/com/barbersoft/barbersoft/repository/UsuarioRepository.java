package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar un administrador mediante su correo electrónico.
    Optional<Usuario> findByEmail(String email);

    // Comprobar si ya existe un usuario con ese correo.
    boolean existsByEmail(String email);
}