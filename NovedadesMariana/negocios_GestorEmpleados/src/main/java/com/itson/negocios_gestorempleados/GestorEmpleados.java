/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_gestorempleados;

import bos.UsuarioBO;
import dtos.UsuarioDTO;
import interfaces.IUsuarioBO;

/**
 *
 * @author saula
 */
public class GestorEmpleados implements IGestorEmpleados {

    private final IUsuarioBO usuarioBO = UsuarioBO.getInstance();

    public GestorEmpleados() {
    }

    @Override
    public UsuarioDTO autenticar(String usuario, String contrasenia) {
        return this.usuarioBO.autenticar(usuario, contrasenia);
    }

    @Override
    public UsuarioDTO obtenerPorId(int id) {
        return this.usuarioBO.obtenerPorId(id);
    }

    @Override
    public UsuarioDTO obtenerPorUsuario(String usuario) {
        return this.usuarioBO.obtenerPorUsuario(usuario);
    }
}
