package com.minimarket.service;

import com.minimarket.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByUsername(String username);
    Usuario save(Usuario usuario);
    void deleteById(Long id);

    /**
     * Indica si el usuario tiene todos sus datos obligatorios completos
     * (nombre, apellido, email y direccion no vacios).
     */
    boolean datosCompletos(Usuario usuario);

    /**
     * Registra un usuario validando previamente que sus datos esten completos.
     * @throws IllegalArgumentException si faltan datos obligatorios.
     */
    Usuario registrarUsuario(Usuario usuario);

    /** Indica si el usuario posee el rol indicado (por nombre). */
    boolean tieneRol(Usuario usuario, String nombreRol);
}