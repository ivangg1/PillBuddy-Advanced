package com.grupoX.PillBuddy.controller;

import com.grupoX.PillBuddy.model.Usuario;
import com.grupoX.PillBuddy.model.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller 
public class UsuarioWebController {

    @Autowired
    private UsuarioService usuarioService;

    // 1. Mostrar la lista de usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("listaUsuarios", usuarioService.listarUsuarios());
        return "usuarios"; // Busca el archivo usuarios.html
    }

    // 2. Mostrar formulario para crear un nuevo usuario
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        return "formulario_usuario"; // Busca formulario_usuario.html
    }

    // 3. Guardar usuario (Crear o Actualizar)
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario) {
        usuarioService.registrarUsuario(usuario);
        return "redirect:/usuarios"; // Redirige a la lista
    }

    // 4. Mostrar formulario para editar
    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        // Buscamos el usuario, si existe lo pasamos al modelo
        usuarioService.obtenerPorId(id).ifPresent(usuario -> {
            model.addAttribute("usuario", usuario);
        });
        return "formulario_usuario"; // Reutilizamos el mismo formulario
    }

    // 5. Eliminar usuario
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios";
    }
}