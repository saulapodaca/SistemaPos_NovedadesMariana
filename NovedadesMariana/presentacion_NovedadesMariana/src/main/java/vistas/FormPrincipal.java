package vistas;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import utilerias.Estilo;

public final class FormPrincipal extends JFrame {
    private final JLabel empleado = new JLabel("Empleado sin identificar");
    private final JButton btnAgregar = new JButton("+ Agregar");

    public FormPrincipal() {
        super("Novedades Mariana · Punto de venta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Estilo.AZUL);
        cabecera.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel nombre = new JLabel("Novedades Mariana  /  Ropa infantil y accesorios");
        nombre.setForeground(Color.WHITE);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 17));
        cabecera.add(nombre, BorderLayout.WEST);

        // Panel derecho: contiene el botón Agregar y el nombre del empleado
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        panelDerecho.setOpaque(false);

        estilizarBotonCabecera(btnAgregar);
        panelDerecho.add(btnAgregar);

        empleado.setForeground(Color.WHITE);
        empleado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panelDerecho.add(empleado);

        cabecera.add(panelDerecho, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);
    }

    private void estilizarBotonCabecera(JButton boton) {
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBackground(Estilo.VERDE_COBRO);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(8, 16, 8, 16));
        boton.setToolTipText("Registrar nuevo producto");
    }

    public void setAccionAgregarProducto(Runnable accion) {
        // Limpia listeners previos por si se vuelve a configurar
        for (var l : btnAgregar.getActionListeners()) {
            btnAgregar.removeActionListener(l);
        }
        if (accion != null) {
            btnAgregar.addActionListener(e -> accion.run());
        }
    }

    public void mostrarPanel(JPanel panel) {
        BorderLayout layout = (BorderLayout) getContentPane().getLayout();
        Component anterior = layout.getLayoutComponent(BorderLayout.CENTER);
        if (anterior != null) remove(anterior);
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void mostrarEmpleado(String nombre) {
        empleado.setText(nombre);
    }
}