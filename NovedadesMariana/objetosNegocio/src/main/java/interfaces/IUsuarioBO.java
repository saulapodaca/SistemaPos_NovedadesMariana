/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import dtos.UsuarioDTO;

/**
 *
 * @author saula
 */
public interface IUsuarioBO {
    UsuarioDTO autenticar(String usuario, String contrasenia);

    UsuarioDTO obtenerPorId(int id);

    UsuarioDTO obtenerPorUsuario(String usuario);
}
