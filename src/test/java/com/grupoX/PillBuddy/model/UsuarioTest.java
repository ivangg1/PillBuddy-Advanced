package com.grupoX.PillBuddy.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    @DisplayName("Test: Crear usuario vacío y asignar valores (Setters/Getters)")
    void UsuarioSettersAndGetters() {
        Usuario usuario = new Usuario();

        usuario.setId(1L);
        usuario.setUsername("juan123");
        usuario.setPassword("pass123");
        usuario.setNombre("Juan Perez");
        usuario.setEdad(30);

        assertEquals(1L, usuario.getId());
        assertEquals("juan123", usuario.getUsername());
        assertEquals("pass123", usuario.getPassword());
        assertEquals("Juan Perez", usuario.getNombre());
        assertEquals(30, usuario.getEdad());
    }

    @Test
    @DisplayName("Test: Crear usuario con constructor completo")
    void UsuarioConstructorCompleto() {
        //constructor: username, password, nombre, edad
        Usuario usuario = new Usuario("maria456", "secret", "Maria Lopez", 25);

        assertNull(usuario.getId()); // ID es null hasta persistencia
        assertEquals("maria456", usuario.getUsername());
        assertEquals("secret", usuario.getPassword());
        assertEquals("Maria Lopez", usuario.getNombre());
        assertEquals(25, usuario.getEdad());
    }
}