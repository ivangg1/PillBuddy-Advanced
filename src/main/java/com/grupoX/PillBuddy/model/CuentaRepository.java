package com.grupoX.PillBuddy.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    // Método clave para el Login
    Optional<Cuenta> findByUsername(String username);
}