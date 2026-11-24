package com.itson.negocios_administradorempleados.fachada;

import dtos.EmpleadoConsultaDTO;
import java.util.List;

public interface IAdministradorEmpleadosFacade {

    List<EmpleadoConsultaDTO> listarEmpleados() throws Exception;

    EmpleadoConsultaDTO obtenerEmpleadoPorId(int id);
}
