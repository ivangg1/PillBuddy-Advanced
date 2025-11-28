package com.grupoX.PillBuddy.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    // Lógica para Login
    public Optional<Cuenta> buscarPorUsername(String username) {
        return cuentaRepository.findByUsername(username);
    }

    // Lógica para RF5
    public boolean cambiarPassword(Long idCuenta, String nuevaPassword) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(idCuenta);
        if (cuentaOpt.isPresent()) {
            Cuenta cuenta = cuentaOpt.get();
            cuenta.setPassword(nuevaPassword); 
            cuentaRepository.save(cuenta);
            return true;
        }
        return false;
    }
}