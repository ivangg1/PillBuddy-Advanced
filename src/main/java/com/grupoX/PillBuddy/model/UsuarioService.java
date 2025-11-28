package com.grupoX.PillBuddy.service;


import com.grupoX.PillBuddy.model.Usuario;
import com.grupoX.PillBuddy.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    
    public Usuario registrarUsuario(Usuario usuario) {
        
        return usuarioRepository.save(usuario);
    }

    
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    
    public Usuario actualizarUsuario(Long id, Usuario nuevosDatos) {
        return usuarioRepository.findById(id)
            .map(usuario -> {
                usuario.setNombre(nuevosDatos.getNombre());
                usuario.setEdad(nuevosDatos.getEdad());
                usuario.setUsername(nuevosDatos.getUsername());
                return usuarioRepository.save(usuario);
            }).orElse(null);
    }
}
