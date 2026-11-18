package vistas;

import controles.ControlGestionarInventario;
import controles.ControlNavegacion;
import dtos.ProductoDTO;
import utilerias.Estilo;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Modal para agregar una nueva talla con existencias iniciales a un producto existente.
 */
public final class PanelModalNuevaTalla extends JPanel {

    private final ProductoDTO producto;
    private final ControlGestionarInventario control;
    private final ControlNavegacion navegacion;
    private final Runnable alTerminar;

    private final JTextField txtTalla = new JTextField();
    private final JTextField txtStockInicial = new JTextField("10");
    private final JButton btnGuardar = Estilo.boton("Agregar Talla", true);
    private final JButton btnCancelar = Estilo.boton("Cancelar", false);

    public PanelModalNuevaTalla(ProductoDTO producto,
                                ControlGestionarInventario control,
                                ControlNavegacion navegacion,
                                Runnable alTerminar) {
        super(new BorderLayout());
        this.producto = producto;
        this.control = control;
        this.navegacion = navegacion;
        this.alTerminar = alTerminar;

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(460, 360));

        add(crearCabecera(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout(10, 4));
        cabecera.setBackground(Estilo.AZUL);
        cabecera.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel titulo = new JLabel("Agregar Nueva Talla al Producto");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(producto.getNombre() + " · SKU: " + producto.getSku());
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(219, 234, 254));

        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(subtitulo, BorderLayout.SOUTH);
        return cabecera;
    }

    private JPanel crearCuerpo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel lblTalla = new JLabel("Identificador de Talla (ej. 2T, 4T, 6T, CH, M, G, 12M):");
        lblTalla.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTalla.setForeground(Estilo.TEXTO_VENTA);
        panel.add(lblTalla);
        panel.add(Box.createVerticalStrut(6));

        txtTalla.setFont(new Font("SansSerif", Font.BOLD, 14));
        txtTalla.setBackground(Color.WHITE);
        txtTalla.setForeground(Estilo.TEXTO_OSCURO);
        txtTalla.setCaretColor(Estilo.TEXTO_OSCURO);
        txtTalla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        txtTalla.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        panel.add(txtTalla);
        panel.add(Box.createVerticalStrut(14));

        JLabel lblStock = new JLabel("Stock o Existencias Iniciales:");
        lblStock.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStock.setForeground(Estilo.TEXTO_VENTA);
        panel.add(lblStock);
        panel.add(Box.createVerticalStrut(6));

        txtStockInicial.setFont(new Font("SansSerif", Font.BOLD, 16));
        txtStockInicial.setHorizontalAlignment(JTextField.CENTER);
        txtStockInicial.setBackground(Color.WHITE);
        txtStockInicial.setForeground(Estilo.TEXTO_OSCURO);
        txtStockInicial.setCaretColor(Estilo.TEXTO_OSCURO);
        txtStockInicial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        txtStockInicial.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        panel.add(txtStockInicial);

        for (Component c : panel.getComponents()) {
            if (c instanceof JComponent jc) jc.setAlignmentX(LEFT_ALIGNMENT);
        }

        return panel;
    }

    private JPanel crearBarraInferior() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        footer.setBackground(Estilo.FONDO);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE_TARJETA));

        btnCancelar.addActionListener(e -> navegacion.cerrarModal());
        btnGuardar.addActionListener(e -> guardar());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        return footer;
    }

    private void guardar() {
        String tallaNom = txtTalla.getText().trim();
        String stockStr = txtStockInicial.getText().trim();

        if (tallaNom.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La talla y el stock inicial son requeridos.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa un número válido mayor o igual a cero.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        navegacion.ejecutar(() -> {
            control.agregarTalla(producto.getId().intValue(), tallaNom, stock);
            return true;
        }, exito -> {
            navegacion.cerrarModal();
            if (alTerminar != null) alTerminar.run();
            JOptionPane.showMessageDialog(this,
                    "Talla " + tallaNom + " agregada correctamente al inventario.",
                    "Talla Registrada", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
