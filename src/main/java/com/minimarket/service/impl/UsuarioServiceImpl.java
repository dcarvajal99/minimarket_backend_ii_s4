package com.minimarket.service.impl;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean datosCompletos(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        return tieneTexto(usuario.getNombre())
                && tieneTexto(usuario.getApellido())
                && tieneTexto(usuario.getEmail())
                && tieneTexto(usuario.getDireccion());
    }

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        if (!datosCompletos(usuario)) {
            throw new IllegalArgumentException(
                    "Datos de usuario incompletos: nombre, apellido, email y direccion son obligatorios");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean tieneRol(Usuario usuario, String nombreRol) {
        if (usuario == null || usuario.getRoles() == null) {
            return false;
        }
        return usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals(nombreRol));
    }

    /** Verdadero si el texto no es null ni esta en blanco. */
    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
}
