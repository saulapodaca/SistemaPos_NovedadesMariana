/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_gestorempleados;

import dtos.UsuarioDTO;
import java.util.List;


/**
 *
 * @author saula
 */
public interface IGestorEmpleados {
    UsuarioDTO autenticar(String usuario, String contrasenia);

    UsuarioDTO obtenerPorId(int id);

    UsuarioDTO obtenerPorUsuario(String usuario);

    List<UsuarioDTO> listarEmpleados() throws Exception;

    void agregarEmpleado(UsuarioDTO usuario) throws Exception;

    void modificarEmpleado(UsuarioDTO usuario) throws Exception;

    void eliminarEmpleado(Long id) throws Exception;
}
