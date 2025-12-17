package com.grupoX.PillBuddy.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private CuentaService cuentaService;

    @Test
    @DisplayName("Buscar cuenta por username")
    void buscarPorUsername() {
        String username = "admin";
        Usuario cuenta = new Usuario(); //usamos Usuario ya que cuenta es abstracta
        cuenta.setUsername(username);
        
        when(cuentaRepository.findByUsername(username)).thenReturn(Optional.of(cuenta));

        Optional<Cuenta> res = cuentaService.buscarPorUsername(username);

        assertTrue(res.isPresent());
        assertEquals(username, res.get().getUsername());
    }

    @Test
    @DisplayName("Cambiar password exitosamente")
    void cambiarPassword_Exito() {
        Long id = 1L;
        Usuario cuenta = new Usuario();
        cuenta.setPassword("old");
        
        when(cuentaRepository.findById(id)).thenReturn(Optional.of(cuenta));

        boolean resultado = cuentaService.cambiarPassword(id, "newPass");

        assertTrue(resultado);
        assertEquals("newPass", cuenta.getPassword());
        verify(cuentaRepository).save(cuenta);
    }

    @Test
    @DisplayName("Fallo al cambiar password si ID no existe")
    void cambiarPassword_Fallo() {
        when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

        boolean resultado = cuentaService.cambiarPassword(99L, "newPass");

        assertFalse(resultado);
        verify(cuentaRepository, never()).save(any());
    }
}