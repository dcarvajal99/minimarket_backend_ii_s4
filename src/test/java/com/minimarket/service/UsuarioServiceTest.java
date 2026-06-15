package com.minimarket.service;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de usuarios. Se usa Mockito para simular el
 * UsuarioRepository (dependencia externa), de modo que las pruebas queden
 * aisladas de la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository; // dependencia simulada

    @InjectMocks
    private UsuarioServiceImpl usuarioService;   // clase bajo prueba

    private Usuario usuarioCompleto;

    @BeforeEach
    void setUp() {
        usuarioCompleto = new Usuario();
        usuarioCompleto.setId(1L);
        usuarioCompleto.setUsername("jperez");
        usuarioCompleto.setPassword("secret123");
        usuarioCompleto.setNombre("Juan");
        usuarioCompleto.setApellido("Perez");
        usuarioCompleto.setEmail("juan.perez@minimarket.cl");
        usuarioCompleto.setDireccion("Av. Siempre Viva 742");
        usuarioCompleto.setRoles(Set.of(new Rol("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Un usuario con todos sus datos es considerado completo")
    void datosCompletos_usuarioConTodosLosDatos_retornaTrue() {
        assertTrue(usuarioService.datosCompletos(usuarioCompleto));
    }

    @Test
    @DisplayName("Un usuario sin email NO es considerado completo")
    void datosCompletos_usuarioSinEmail_retornaFalse() {
        usuarioCompleto.setEmail(null);
        assertFalse(usuarioService.datosCompletos(usuarioCompleto));
    }

    @Test
    @DisplayName("Un usuario con campos en blanco NO es considerado completo")
    void datosCompletos_camposEnBlanco_retornaFalse() {
        usuarioCompleto.setDireccion("   ");
        assertFalse(usuarioService.datosCompletos(usuarioCompleto));
    }

    @Test
    @DisplayName("registrarUsuario guarda en el repositorio cuando los datos estan completos")
    void registrarUsuario_datosCompletos_persisteYRetorna() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioCompleto);

        Usuario guardado = usuarioService.registrarUsuario(usuarioCompleto);

        assertNotNull(guardado);
        assertEquals("jperez", guardado.getUsername());
        // Verifica que la dependencia simulada fue invocada exactamente una vez.
        verify(usuarioRepository, times(1)).save(usuarioCompleto);
    }

    @Test
    @DisplayName("registrarUsuario lanza excepcion y NO guarda si faltan datos")
    void registrarUsuario_datosIncompletos_lanzaExcepcion() {
        usuarioCompleto.setNombre(null); // dato obligatorio faltante

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrarUsuario(usuarioCompleto));

        assertTrue(ex.getMessage().contains("incompletos"));
        // El repositorio nunca debe invocarse si la validacion falla.
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("tieneRol detecta correctamente el rol asignado al usuario")
    void tieneRol_usuarioConRol_retornaTrue() {
        assertTrue(usuarioService.tieneRol(usuarioCompleto, "ROLE_ADMIN"));
        assertFalse(usuarioService.tieneRol(usuarioCompleto, "ROLE_CLIENTE"));
    }

    @Test
    @DisplayName("findById delega en el repositorio y retorna el usuario")
    void findById_existente_retornaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioCompleto));

        Optional<Usuario> resultado = usuarioService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("jperez", resultado.get().getUsername());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("findByUsername delega en el repositorio")
    void findByUsername_existente_retornaUsuario() {
        when(usuarioRepository.findByUsername("jperez")).thenReturn(Optional.of(usuarioCompleto));

        assertTrue(usuarioService.findByUsername("jperez").isPresent());
        verify(usuarioRepository).findByUsername("jperez");
    }

    @Test
    @DisplayName("findAll retorna la lista de usuarios del repositorio")
    void findAll_retornaLista() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioCompleto));

        List<Usuario> usuarios = usuarioService.findAll();

        assertEquals(1, usuarios.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("save delega la persistencia en el repositorio")
    void save_delegaEnRepositorio() {
        when(usuarioRepository.save(usuarioCompleto)).thenReturn(usuarioCompleto);

        assertEquals(usuarioCompleto, usuarioService.save(usuarioCompleto));
        verify(usuarioRepository).save(usuarioCompleto);
    }

    @Test
    @DisplayName("deleteById invoca la eliminacion en el repositorio")
    void deleteById_invocaRepositorio() {
        usuarioService.deleteById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
