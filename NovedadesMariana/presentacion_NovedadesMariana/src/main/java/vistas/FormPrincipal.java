package vistas;

import utilerias.Estilo;

import java.awt.*;
import javax.swing.*;

public final class FormPrincipal extends JFrame {
    private final JLabel empleado = new JLabel("Empleado sin identificar");

    public FormPrincipal() {
        super("Novedades Mariana · Punto de venta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setSize(1200, 800);
        setLocationRelativeTo(null);
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Estilo.AZUL);
        cabecera.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        JLabel nombre = new JLabel("Novedades Mariana  /  Ropa infantil y accesorios");
        nombre.setForeground(Color.WHITE);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 17));
        empleado.setForeground(Color.WHITE);
        cabecera.add(nombre, BorderLayout.WEST);
        cabecera.add(empleado, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);
    }

    public void mostrarPanel(JPanel panel) {
        BorderLayout layout = (BorderLayout) getContentPane().getLayout();
        Component anterior = layout.getLayoutComponent(BorderLayout.CENTER);
        if (anterior != null) remove(anterior);
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void mostrarEmpleado(String nombre) { empleado.setText(nombre); }
}