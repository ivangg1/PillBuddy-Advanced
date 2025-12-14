package com.grupoX.PillBuddy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
@PrimaryKeyJoinColumn(name = "cuenta_id") 
public class Usuario extends Cuenta 
{ 
    
    @Column(nullable = false)
    private String nombre;

    private int edad;

   
    public Usuario() {
        super();
    }

    
    public Usuario(String username, String password, String nombre, int edad) {
        super(username, password); 
        this.nombre = nombre;
        this.edad = edad;
    }

    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
}