package vistas;

import utilerias.Estilo;
import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Ventana principal del sistema POS Novedades Mariana.
 * Integra la cabecera institucional, información de sesión y botones de navegación rápida.
 */
public final class FormPrincipal extends JFrame {

    private final JLabel empleado = new JLabel("Empleado sin identificar");
    private final JButton btnNavVentas = crearBotonNav("Punto de Venta", "VENTAS");
    private final JButton btnNavInventario = crearBotonNav("Gestión de Inventario", "INVENTARIO");
    private Consumer<String> navegacionListener;
    private String moduloActual = "VENTAS";

    public FormPrincipal() {
        super("Novedades Mariana · Punto de venta e inventario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setSize(1280, 820);
        setLocationRelativeTo(null);

        JPanel cabecera = new JPanel(new BorderLayout(16, 0));
        cabecera.setBackground(Estilo.AZUL);
        cabecera.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        // Título de marca
        JPanel textosMarca = new JPanel(new GridLayout(2, 1, 0, 2));
        textosMarca.setOpaque(false);
        JLabel nombre = new JLabel("Novedades Mariana");
        nombre.setForeground(Color.WHITE);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitulo = new JLabel("Ropa infantil, accesorios y punto de venta");
        subtitulo.setForeground(new Color(199, 210, 254));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        textosMarca.add(nombre);
        textosMarca.add(subtitulo);

        // Barra de navegación de módulos
        JPanel panelNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelNav.setOpaque(false);
        panelNav.add(btnNavVentas);
        panelNav.add(btnNavInventario);

        // Zona de usuario / empleado
        JPanel panelEmpleado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelEmpleado.setOpaque(false);
        JLabel lblRotuloUser = new JLabel("Usuario:");
        lblRotuloUser.setForeground(new Color(199, 210, 254));
        lblRotuloUser.setFont(new Font("SansSerif", Font.PLAIN, 12));

        empleado.setForeground(Color.WHITE);
        empleado.setFont(new Font("SansSerif", Font.BOLD, 13));
        panelEmpleado.add(lblRotuloUser);
        panelEmpleado.add(empleado);

        cabecera.add(textosMarca, BorderLayout.WEST);
        cabecera.add(panelNav, BorderLayout.CENTER);
        cabecera.add(panelEmpleado, BorderLayout.EAST);

        add(cabecera, BorderLayout.NORTH);
        actualizarEstiloBotonesNav();
    }

    private JButton crearBotonNav(String texto, String modulo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean activo = modulo.equals(moduloActual);
                int w = getWidth();
                int h = getHeight();

                if (activo) {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(Color.WHITE);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                } else {
                    g2.setColor(new Color(30, 48, 92));
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(new Color(59, 130, 246));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addActionListener(e -> {
            if (navegacionListener != null && !modulo.equals(moduloActual)) {
                navegacionListener.accept(modulo);
            }
        });
        return btn;
    }

    public void setNavegacionListener(Consumer<String> listener) {
        this.navegacionListener = listener;
    }

    public void seleccionarModulo(String modulo) {
        this.moduloActual = modulo;
        actualizarEstiloBotonesNav();
    }

    private void actualizarEstiloBotonesNav() {
        btnNavVentas.setForeground("VENTAS".equals(moduloActual) ? Estilo.AZUL : Color.WHITE);
        btnNavInventario.setForeground("INVENTARIO".equals(moduloActual) ? Estilo.AZUL : Color.WHITE);
        btnNavVentas.repaint();
        btnNavInventario.repaint();
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
