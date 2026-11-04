/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_gestorempleados;

import bos.UsuarioBO;
import interfaces.IUsuarioDTO;
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
    public IUsuarioDTO autenticar(String usuario, String contrasenia) {
        try {
            return this.usuarioBO.autenticar(usuario, contrasenia);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo autenticar al empleado.", e);
        }
    }

    @Override
    public IUsuarioDTO obtenerPorId(int id) {
        try {
            return this.usuarioBO.buscarPorId((long) id);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo consultar al empleado.", e);
        }
    }

    @Override
    public IUsuarioDTO obtenerPorUsuario(String usuario) {
        try {
            return this.usuarioBO.buscarPorUsuario(usuario);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo consultar al empleado.", e);
        }
    }
}
