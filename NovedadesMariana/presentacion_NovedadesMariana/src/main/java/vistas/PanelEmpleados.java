package vistas;

import com.itson.negocios_administradorempleados.fachada.IAdministradorEmpleadosFacade;
import controles.ControlNavegacion;
import dtos.EmpleadoConsultaDTO;
import dtos.UsuarioDTO;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import utilerias.Estilo;

public final class PanelEmpleados extends JPanel {
    private final IAdministradorEmpleadosFacade fachada;
    private final ControlNavegacion navegacion;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Usuario", "Nombres", "Primer apellido", "Segundo apellido", "Rol", "Estado"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JButton editar = Estilo.boton("Editar", false);
    private final JButton eliminar = Estilo.botonPersonalizado("Desactivar", Estilo.FONDO_CANCELAR,
            Estilo.BORDE_CANCELAR, Estilo.ERROR);
    private final JLabel estado = new JLabel();
    private List<EmpleadoConsultaDTO> empleados = List.of();

    public PanelEmpleados(IAdministradorEmpleadosFacade fachada, ControlNavegacion navegacion) {
        super(new BorderLayout(12, 12));
        this.fachada = fachada;
        this.navegacion = navegacion;
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(Estilo.titulo("Gestión de empleados"), BorderLayout.WEST);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.setOpaque(false);
        JButton actualizar = Estilo.boton("Actualizar", false);
        JButton agregar = Estilo.boton("Nuevo empleado", true);
        actualizar.addActionListener(e -> cargar());
        agregar.addActionListener(e -> abrirFormulario(null));
        editar.addActionListener(e -> abrirFormulario(seleccionado()));
        eliminar.addActionListener(e -> eliminarSeleccionado());
        for (JButton boton : List.of(actualizar, editar, eliminar, agregar)) acciones.add(boton);
        cabecera.add(acciones, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(32);
        tabla.setFillsViewportHeight(true);
        tabla.getSelectionModel().addListSelectionListener(e -> actualizarAcciones());
        actualizarAcciones();
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(estado, BorderLayout.SOUTH);
    }

    public void cargar() {
        navegacion.ejecutar(fachada::listarEmpleados, lista -> {
            tabla.clearSelection();
            modelo.setRowCount(0);
            empleados = lista;
            for (EmpleadoConsultaDTO e : empleados) {
                modelo.addRow(new Object[]{e.getUsuario(), e.getNombres(), e.getPrimerApellido(),
                        e.getSegundoApellido(), e.getRolEmpleado(), e.isActivo() ? "Activo" : "Inactivo"});
            }
            estado.setText(lista.isEmpty() ? "Sin empleados registrados" : "Empleados registrados: " + lista.size());
        });
    }

    private void actualizarAcciones() {
        boolean seleccion = tabla.getSelectedRow() >= 0;
        editar.setEnabled(seleccion);
        eliminar.setEnabled(seleccion && seleccionado().isActivo());
    }

    private EmpleadoConsultaDTO seleccionado() {
        return empleados.get(tabla.convertRowIndexToModel(tabla.getSelectedRow()));
    }

    private void eliminarSeleccionado() {
        if (navegacion.estaOcupado()) return;
        EmpleadoConsultaDTO empleado = seleccionado();
        if (JOptionPane.showConfirmDialog(this, "¿Desactivar al usuario " + empleado.getUsuario() + "?",
                "Desactivar empleado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                != JOptionPane.YES_OPTION) return;
        navegacion.ejecutar(() -> {
            fachada.eliminarEmpleado(empleado.getId());
            return true;
        }, eliminado -> cargar());
    }

    private void abrirFormulario(EmpleadoConsultaDTO empleado) {
        if (navegacion.estaOcupado()) return;
        boolean nuevo = empleado == null;
        String titulo = nuevo ? "Nuevo empleado" : "Editar empleado";
        JPanel formulario = Estilo.vertical();
        formulario.add(Estilo.titulo(titulo));
        JPanel campos = new JPanel(new GridLayout(0, 1, 0, 3));
        campos.setOpaque(false);
        JTextField usuario = campo(campos, "Usuario", nuevo ? "" : empleado.getUsuario());
        JTextField nombres = campo(campos, "Nombres", nuevo ? "" : empleado.getNombres());
        JTextField primero = campo(campos, "Primer apellido", nuevo ? "" : empleado.getPrimerApellido());
        JTextField segundo = campo(campos, "Segundo apellido (opcional)", nuevo ? "" : empleado.getSegundoApellido());
        JComboBox<String> rol = new JComboBox<>(new String[]{"EMPLEADO", "ADMINISTRADORA"});
        if (!nuevo) rol.setSelectedItem(empleado.getRolEmpleado());
        campos.add(new JLabel("Rol"));
        campos.add(rol);
        JPasswordField clave = new JPasswordField();
        campos.add(new JLabel(nuevo ? "Contraseña" : "Nueva contraseña (vacía para conservar la actual)"));
        campos.add(clave);
        formulario.add(campos);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.setOpaque(false);
        JButton cancelar = Estilo.boton("Cancelar", false);
        cancelar.addActionListener(e -> navegacion.cerrarModal());
        JButton guardar = Estilo.boton("Guardar", true);
        guardar.addActionListener(e -> {
            if (navegacion.estaOcupado()) return;
            char[] caracteres = clave.getPassword();
            String password = !nuevo && caracteres.length == 0 ? null : new String(caracteres);
            Arrays.fill(caracteres, '\0');
            UsuarioDTO datos = new UsuarioDTO(nuevo ? null : empleado.getId(), usuario.getText().trim(),
                    password, nombres.getText().trim(), primero.getText().trim(),
                    segundo.getText().trim(), (String) rol.getSelectedItem());
            navegacion.ejecutar(() -> {
                if (nuevo) fachada.agregarEmpleado(datos);
                else fachada.modificarEmpleado(datos);
                return true;
            }, guardado -> {
                clave.setText("");
                navegacion.cerrarModal();
                cargar();
            });
        });
        acciones.add(cancelar);
        acciones.add(guardar);
        formulario.add(acciones);
        navegacion.mostrarModal(titulo, formulario, 480, 540);
    }

    private JTextField campo(JPanel campos, String etiqueta, String valor) {
        JTextField texto = new JTextField(valor);
        JLabel label = new JLabel(etiqueta);
        label.setLabelFor(texto);
        campos.add(label);
        campos.add(texto);
        return texto;
    }
}
