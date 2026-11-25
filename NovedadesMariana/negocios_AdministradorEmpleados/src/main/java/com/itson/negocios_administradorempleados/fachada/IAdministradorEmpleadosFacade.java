package com.itson.negocios_administradorempleados.fachada;

import dtos.EmpleadoConsultaDTO;
import dtos.UsuarioDTO;
import java.util.List;

public interface IAdministradorEmpleadosFacade {

    List<EmpleadoConsultaDTO> listarEmpleados() throws Exception;

    EmpleadoConsultaDTO obtenerEmpleadoPorId(int id);
    boolean autenticarAdministradora(String usuario, String clave);
    void agregarEmpleado(UsuarioDTO empleado) throws Exception;
    void modificarEmpleado(UsuarioDTO empleado) throws Exception;
    void eliminarEmpleado(Long id) throws Exception;
}
