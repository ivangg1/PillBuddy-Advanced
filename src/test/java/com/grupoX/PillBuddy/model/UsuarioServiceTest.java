package com.grupoX.PillBuddy.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Registrar un usuario correctamente")
    void registrarUsuario() {
        Usuario usuario = new Usuario("user1", "pass", "Test", 20);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.registrarUsuario(usuario);

        assertNotNull(resultado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Listar todos los usuarios")
    void listarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(new Usuario(), new Usuario()));

        List<Usuario> lista = usuarioService.listarUsuarios();

        assertEquals(2, lista.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Obtener usuario por ID existente")
    void obtenerPorId_Existente() {
        Usuario u = new Usuario();
        u.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

        Optional<Usuario> res = usuarioService.obtenerPorId(1L);

        assertTrue(res.isPresent());
        assertEquals(1L, res.get().getId());
    }

    @Test
    @DisplayName("Actualizar usuario existente")
    void actualizarUsuario_Existente() {
        Long id = 1L;
        Usuario existente = new Usuario("oldUser", "pass", "OldName", 20);
        Usuario nuevosDatos = new Usuario("newUser", "pass", "NewName", 25);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario actualizado = usuarioService.actualizarUsuario(id, nuevosDatos);

        assertEquals("NewName", actualizado.getNombre());
        assertEquals(25, actualizado.getEdad());
        assertEquals("newUser", actualizado.getUsername());
    }

    @Test
    @DisplayName("Intentar actualizar usuario inexistente devuelve null")
    void actualizarUsuario_NoExistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Usuario res = usuarioService.actualizarUsuario(99L, new Usuario());

        assertNull(res);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar usuario")
    void eliminarUsuario() {
        usuarioService.eliminarUsuario(1L);
        verify(usuarioRepository).deleteById(1L);
    }
}