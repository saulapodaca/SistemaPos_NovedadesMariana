/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_gestorempleados;

import interfaces.IUsuarioDTO;

/**
 *
 * @author saula
 */
public interface IGestorEmpleados {
    IUsuarioDTO autenticar(String usuario, String contrasenia);

    IUsuarioDTO obtenerPorId(int id);

    IUsuarioDTO obtenerPorUsuario(String usuario);
}
