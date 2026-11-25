package com.itson.negocios_administradorempleados.fachada;

import com.itson.negocios_gestorempleados.GestorEmpleados;
import com.itson.negocios_gestorempleados.IGestorEmpleados;
import dtos.EmpleadoConsultaDTO;
import dtos.UsuarioDTO;
import java.util.ArrayList;
import java.util.List;

public class AdministradorEmpleadosFacade implements IAdministradorEmpleadosFacade {

    private final IGestorEmpleados gestorEmpleados = new GestorEmpleados();

    @Override
    public List<EmpleadoConsultaDTO> listarEmpleados() throws Exception {
        List<EmpleadoConsultaDTO> empleados = new ArrayList<>();
        for (UsuarioDTO usuario : this.gestorEmpleados.listarEmpleados()) {
            empleados.add(adaptarEmpleado(usuario));
        }
        return empleados;
    }

    @Override
    public EmpleadoConsultaDTO obtenerEmpleadoPorId(int id) {
        return adaptarEmpleado(this.gestorEmpleados.obtenerPorId(id));
    }

    @Override
    public boolean autenticarAdministradora(String usuario, String clave) {
        UsuarioDTO empleado = gestorEmpleados.autenticar(usuario, clave);
        return empleado != null && "ADMINISTRADORA".equals(empleado.getRolEmpleado());
    }

    @Override
    public void agregarEmpleado(UsuarioDTO empleado) throws Exception {
        gestorEmpleados.agregarEmpleado(empleado);
    }

    @Override
    public void modificarEmpleado(UsuarioDTO empleado) throws Exception {
        gestorEmpleados.modificarEmpleado(empleado);
    }

    @Override
    public void eliminarEmpleado(Long id) throws Exception {
        gestorEmpleados.eliminarEmpleado(id);
    }

    private EmpleadoConsultaDTO adaptarEmpleado(UsuarioDTO usuario) {
        if (usuario == null) {
            return null;
        }
        EmpleadoConsultaDTO empleado = new EmpleadoConsultaDTO(usuario.getId(), usuario.getUsuario(),
                usuario.getNombres(), usuario.getPrimerApellido(),
                usuario.getSegundoApellido(), usuario.getRolEmpleado());
        empleado.setActivo(usuario.isActivo());
        return empleado;
    }
}
