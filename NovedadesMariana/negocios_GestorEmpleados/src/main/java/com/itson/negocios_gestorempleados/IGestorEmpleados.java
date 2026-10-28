/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_gestorempleados;

import dtos.UsuarioDTO;

/**
 *
 * @author saula
 */
public interface IGestorEmpleados {
    UsuarioDTO autenticar(String usuario, String contrasenia);

    UsuarioDTO obtenerPorId(int id);

    UsuarioDTO obtenerPorUsuario(String usuario);
}
