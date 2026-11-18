/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_gestorempleados;

import bos.UsuarioBO;
import dtos.UsuarioDTO;
import interfaces.IUsuarioBO;
import java.util.List;

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
        try {
            return this.usuarioBO.autenticar(usuario, contrasenia);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo autenticar al empleado.", e);
        }
    }

    @Override
    public UsuarioDTO obtenerPorId(int id) {
        try {
            return this.usuarioBO.buscarPorId((long) id);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo consultar al empleado.", e);
        }
    }

    @Override
    public UsuarioDTO obtenerPorUsuario(String usuario) {
        try {
            return this.usuarioBO.buscarPorUsuario(usuario);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo consultar al empleado.", e);
        }
    }

    @Override
    public List<UsuarioDTO> listarEmpleados() throws Exception {
        return this.usuarioBO.obtenerTodos();
    }

    @Override
    public void agregarEmpleado(UsuarioDTO usuario) throws Exception {
        this.usuarioBO.guardar(usuario);
    }

    @Override
    public void modificarEmpleado(UsuarioDTO usuario) throws Exception {
        this.usuarioBO.actualizar(usuario);
    }

    @Override
    public void eliminarEmpleado(Long id) throws Exception {
        this.usuarioBO.eliminar(id);
    }
}
