package controles;

import vistas.*;
import com.itson.negocios_administradorempleados.fachada.AdministradorEmpleadosFacade;
import utilerias.EjecutorTareas;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.*;

/** Cambia pantallas y administra los diálogos de venta e inventario. */
public final class ControlNavegacion {
    private static ControlNavegacion instance;
    private final FormPrincipal formPrincipal;
    private final ControlRealizarVenta control;
    private final ControlGestionarInventario controlInventario;
    private PanelVenta panelVenta;
    private PanelInventario panelInventario;
    private JDialog modal;
    private final EjecutorTareas tareas = new EjecutorTareas();

    public ControlNavegacion(FormPrincipal formPrincipal, ControlRealizarVenta control) {
        this(formPrincipal, control, ControlGestionarInventario.getInstance());
    }

    public ControlNavegacion(FormPrincipal formPrincipal, ControlRealizarVenta control, ControlGestionarInventario controlInventario) {
        this.formPrincipal = formPrincipal;
        this.control = control;
        this.controlInventario = controlInventario != null ? controlInventario : ControlGestionarInventario.getInstance();
    }

    public static synchronized ControlNavegacion getInstance() {
        if (instance == null) {
            instance = new ControlNavegacion(new FormPrincipal(), ControlRealizarVenta.getInstance(), ControlGestionarInventario.getInstance());
        }
        return instance;
    }

    public void iniciar() {
        if (control.getUsuario() != null) {
            formPrincipal.mostrarEmpleado(control.getUsuario().getNombres()
                    + " " + control.getUsuario().getPrimerApellido());
        }
        formPrincipal.setNavegacionListener(modulo -> {
            if (estaOcupado()) return;
            if ("EMPLEADOS".equals(modulo)) {
                abrirMenuEmpleados();
            } else if ("VENTAS".equalsIgnoreCase(modulo)) {
                abrirMenuVentas();
            } else if ("INVENTARIO".equalsIgnoreCase(modulo)) {
                abrirMenuInventario();
            }
        });
        abrirMenuVentas();
        formPrincipal.setVisible(true);
    }

    private void abrirMenuEmpleados() {
        var fachada = new AdministradorEmpleadosFacade();
        JTextField usuario = new JTextField(20);
        JPasswordField clave = new JPasswordField(20);
        Object[] campos = {"Usuario de administradora", usuario, "Contraseña", clave};
        if (JOptionPane.showConfirmDialog(formPrincipal, campos, "Gestión de empleados",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        String nombre = usuario.getText().trim();
        char[] caracteres = clave.getPassword();
        String password = new String(caracteres);
        java.util.Arrays.fill(caracteres, '\0');
        clave.setText("");
        ejecutar(() -> fachada.autenticarAdministradora(nombre, password), permitido -> {
            if (!permitido) {
                error(new IllegalArgumentException("Ingresa las credenciales de una administradora."));
                return;
            }
            if (panelVenta != null) panelVenta.detenerBusqueda();
            PanelEmpleados panel = new PanelEmpleados(fachada, this);
            formPrincipal.seleccionarModulo("EMPLEADOS");
            formPrincipal.mostrarPanel(panel);
            panel.cargar();
        });
    }

    public void abrirMenuVentas() {
        cerrarModal();
        if (panelVenta != null) panelVenta.detenerBusqueda();
        panelVenta = new PanelVenta(control, this);
        formPrincipal.seleccionarModulo("VENTAS");
        formPrincipal.mostrarPanel(panelVenta);
    }

    public void abrirMenuInventario() {
        cerrarModal();
        if (panelVenta != null) panelVenta.detenerBusqueda();
        panelInventario = new PanelInventario(controlInventario, this);
        formPrincipal.seleccionarModulo("INVENTARIO");
        formPrincipal.mostrarPanel(panelInventario);
    }

    public void abrirModalCobro() {
        if (tareas.estaOcupado()) return;
        if (control.getVenta().getDetalles().isEmpty()) {
            error(new IllegalArgumentException("Agrega productos antes de cobrar."));
            return;
        }
        if (control.getUsuario() == null) {
            identificarEmpleado();
            return;
        }
        mostrarModal("Procesar cobro", new PanelModalCobro(control, this), 760, 680);
    }

    private void identificarEmpleado() {
        JTextField usuario = new JTextField(20);
        JPasswordField clave = new JPasswordField(20);
        JPanel formulario = new JPanel(new GridLayout(0, 1, 6, 6));
        formulario.add(new JLabel("Usuario del empleado"));
        formulario.add(usuario);
        formulario.add(new JLabel("Contraseña"));
        formulario.add(clave);
        int opcion = JOptionPane.showConfirmDialog(formPrincipal, formulario, "Identificar empleado",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;
        char[] caracteres = clave.getPassword();
        String password = new String(caracteres);
        java.util.Arrays.fill(caracteres, '\0');
        clave.setText("");
        ejecutar(() -> control.autenticar(usuario.getText(), password), autenticado -> {
            formPrincipal.mostrarEmpleado(autenticado.getNombres());
            abrirModalCobro();
        });
    }

    public void abrirModalDescuento() {
        if (tareas.estaOcupado()) return;
        if (control.getVenta().getDetalles().isEmpty()) {
            error(new IllegalArgumentException("Agrega productos antes de aplicar un descuento."));
            return;
        }
        mostrarModal("Aplicar descuento", new PanelModalDescuento(control, this), 480, 580);
    }

    public void abrirPantallaTicket() {
        cerrarModal();
        if (panelVenta != null) panelVenta.detenerBusqueda();
        formPrincipal.mostrarPanel(new PanelPantallaTicket(control, this));
    }

    public void nuevaVenta() {
        if (tareas.estaOcupado()) return;
        control.cancelarVentaActual();
        abrirMenuVentas();
    }

    public void cancelarVenta() {
        if (tareas.estaOcupado()) return;
        if (JOptionPane.showConfirmDialog(formPrincipal, "¿Cancelar la venta actual?",
                "Cancelar venta", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) nuevaVenta();
    }

    public void refrescarVenta() {
        if (panelVenta != null) panelVenta.refrescar();
    }

    public void cerrarModal() {
        if (tareas.estaOcupado()) return;
        if (modal != null) {
            modal.dispose();
            modal = null;
        }
    }

    public void mostrarModal(String titulo, JPanel contenido, int ancho, int alto) {
        modal = new JDialog(formPrincipal, titulo, true);
        modal.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        modal.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent evento) { cerrarModal(); }
        });
        modal.setContentPane(contenido);
        modal.setSize(ancho, alto);
        modal.setLocationRelativeTo(formPrincipal);
        modal.setVisible(true);
    }

    public <T> void ejecutar(Callable<T> tarea, Consumer<T> completado) {
        tareas.ejecutar(modal == null ? formPrincipal : modal, tarea, completado, this::error);
    }

    public void error(Throwable error) {
        String mensaje = error.getMessage();
        JOptionPane.showMessageDialog(modal == null ? formPrincipal : modal,
                mensaje == null ? "No se pudo completar la operación." : mensaje,
                "Revisar operación", JOptionPane.ERROR_MESSAGE);
    }

    public void informar(String mensaje) { JOptionPane.showMessageDialog(formPrincipal, mensaje); }
    public boolean estaOcupado() { return tareas.estaOcupado(); }
    public ControlGestionarInventario getControlInventario() { return controlInventario; }
    public FormPrincipal getFormPrincipal() { return formPrincipal; }
}
