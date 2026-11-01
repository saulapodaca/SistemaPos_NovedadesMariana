/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import dtos.UsuarioDTO;
import interfaces.IUsuarioBO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saula
 */
public class UsuarioBO implements IUsuarioBO{

    private static UsuarioBO instance;
    private final List<UsuarioDTO> usuariosMock;

    private UsuarioBO() {
        usuariosMock = new ArrayList<>();
        // Mock inicial de usuarios
        usuariosMock.add(new UsuarioDTO(1, "mariana", "admin123", "Mariana", "López", "García", "ADMINISTRADORA"));
        usuariosMock.add(new UsuarioDTO(2, "cajera1", "pass123", "Ana", "Martínez", "Ruiz", "EMPLEADO"));
    }

    public static synchronized UsuarioBO getInstance() {
        if (instance == null) {
            instance = new UsuarioBO();
        }
        return instance;
    }

    @Override

    public UsuarioDTO autenticar(String usuario, String contrasenia) {
        if (usuario == null || usuario.isBlank()
                || contrasenia == null || contrasenia.isEmpty()) {
            throw new IllegalArgumentException("El usuario y la contraseña son obligatorios.");
        }
        String nombreUsuario = usuario.trim();
        return usuariosMock.stream()
                .filter(u -> u.getUsuario().equalsIgnoreCase(nombreUsuario) && u.getContrasenia().equals(contrasenia))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UsuarioDTO obtenerPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser mayor a cero.");
        }
        return usuariosMock.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public UsuarioDTO obtenerPorUsuario(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        String nombreUsuario = usuario.trim();
        return usuariosMock.stream()
                .filter(u -> u.getUsuario().equalsIgnoreCase(nombreUsuario))
                .findFirst()
                .orElse(null);
    }
}
