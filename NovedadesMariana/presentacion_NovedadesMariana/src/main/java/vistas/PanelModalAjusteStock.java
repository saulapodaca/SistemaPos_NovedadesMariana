package vistas;

import controles.ControlGestionarInventario;
import controles.ControlNavegacion;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import utilerias.Estilo;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Diálogo modal para que el encargado del punto de venta modifique el inventario:
 * permite tanto reabastecer stock (entrada de mercancía) como ajustar a un conteo físico exacto.
 */
public final class PanelModalAjusteStock extends JPanel {

    private final ProductoDTO producto;
    private final ControlGestionarInventario control;
    private final ControlNavegacion navegacion;
    private final Runnable alTerminar;

    private final JComboBox<ItemTalla> comboTallas = new JComboBox<>();
    private final JRadioButton rbReabastecer = new JRadioButton("Entrada / Reabastecer (+ unidades)");
    private final JRadioButton rbAjusteFisico = new JRadioButton("Ajuste Físico (Fijar existencia exacta)");
    private final JTextField txtCantidad = new JTextField();
    private final JLabel lblResultado = new JLabel("-", SwingConstants.CENTER);
    private final JLabel lblStockActual = new JLabel("0");
    private final JComboBox<String> comboMotivos = new JComboBox<>(new String[]{
            "Recepción de mercancía / Proveedor",
            "Ajuste por conteo físico en tienda",
            "Merma / Prenda dañada",
            "Devolución de cliente",
            "Corrección de captura"
    });
    private final JButton btnGuardar = Estilo.boton("Guardar Modificación", true);
    private final JButton btnCancelar = Estilo.boton("Cancelar", false);

    public static class ItemTalla {
        public final ProductoTallaDTO talla;

        public ItemTalla(ProductoTallaDTO talla) {
            this.talla = talla;
        }

        @Override
        public String toString() {
            return "Talla: " + talla.getTalla() + "  (Stock actual: " + talla.getStockActual() + " pzas)";
        }
    }

    public PanelModalAjusteStock(ProductoDTO producto,
                                 List<ProductoTallaDTO> tallas,
                                 ProductoTallaDTO tallaPreseleccionada,
                                 ControlGestionarInventario control,
                                 ControlNavegacion navegacion,
                                 Runnable alTerminar) {
        super(new BorderLayout());
        this.producto = producto;
        this.control = control;
        this.navegacion = navegacion;
        this.alTerminar = alTerminar;

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(540, 620));

        // Llenar combo de tallas
        ItemTalla seleccionado = null;
        for (ProductoTallaDTO t : tallas) {
            ItemTalla item = new ItemTalla(t);
            comboTallas.addItem(item);
            if (tallaPreseleccionada != null && t.getId() == tallaPreseleccionada.getId()) {
                seleccionado = item;
            }
        }
        if (seleccionado != null) {
            comboTallas.setSelectedItem(seleccionado);
        }

        add(crearCabecera(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);

        configurarEventos();
        actualizarCalculos();
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout(10, 4));
        cabecera.setBackground(Estilo.AZUL);
        cabecera.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel titulo = new JLabel("Modificar Inventario de Prenda");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(producto.getNombre() + " · SKU: " + producto.getSku() + " · " + Estilo.dinero(producto.getPrecio()));
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

        // Selector de talla
        panel.add(seccionEtiqueta("1. SELECCIONAR TALLA A MODIFICAR"));
        panel.add(Box.createVerticalStrut(6));
        comboTallas.setFont(new Font("SansSerif", Font.BOLD, 13));
        comboTallas.setBackground(Color.WHITE);
        comboTallas.setForeground(Estilo.TEXTO_OSCURO);
        comboTallas.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        panel.add(comboTallas);
        panel.add(Box.createVerticalStrut(14));

        // Tipo de modificación
        panel.add(seccionEtiqueta("2. TIPO DE OPERACIÓN EN INVENTARIO"));
        panel.add(Box.createVerticalStrut(6));

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbReabastecer);
        grupo.add(rbAjusteFisico);
        rbReabastecer.setSelected(true);

        rbReabastecer.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rbReabastecer.setBackground(Color.WHITE);
        rbReabastecer.setForeground(Estilo.TEXTO_OSCURO);

        rbAjusteFisico.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rbAjusteFisico.setBackground(Color.WHITE);
        rbAjusteFisico.setForeground(Estilo.TEXTO_OSCURO);

        JPanel panelRadios = new JPanel(new GridLayout(2, 1, 0, 4));
        panelRadios.setOpaque(false);
        panelRadios.add(rbReabastecer);
        panelRadios.add(rbAjusteFisico);
        panel.add(panelRadios);
        panel.add(Box.createVerticalStrut(14));

        // Campo de cantidad y presets rápidos
        panel.add(seccionEtiqueta("3. CANTIDAD DE UNIDADES"));
        panel.add(Box.createVerticalStrut(6));

        JPanel panelInput = new JPanel(new BorderLayout(8, 0));
        panelInput.setOpaque(false);
        panelInput.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));

        txtCantidad.setFont(new Font("SansSerif", Font.BOLD, 18));
        txtCantidad.setHorizontalAlignment(JTextField.CENTER);
        txtCantidad.setBackground(Color.WHITE);
        txtCantidad.setForeground(Estilo.TEXTO_OSCURO);
        txtCantidad.setCaretColor(Estilo.TEXTO_OSCURO);
        txtCantidad.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        txtCantidad.setText("10");
        panelInput.add(txtCantidad, BorderLayout.CENTER);

        panel.add(panelInput);
        panel.add(Box.createVerticalStrut(8));

        // Botones de presets rápidos con renderizado propio para alto contraste
        JPanel panelPresets = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelPresets.setOpaque(false);
        int[] presets = {1, 5, 10, 20, 50};
        for (int p : presets) {
            JButton btnPreset = Estilo.botonPersonalizado(
                    "+" + p,
                    new Color(241, 245, 249),
                    new Color(203, 213, 225),
                    Estilo.TEXTO_OSCURO
            );
            btnPreset.setFont(new Font("SansSerif", Font.BOLD, 11));
            btnPreset.setBorder(new EmptyBorder(5, 12, 5, 12));
            btnPreset.addActionListener(e -> {
                if (rbReabastecer.isSelected()) {
                    txtCantidad.setText(String.valueOf(p));
                } else {
                    ItemTalla sel = (ItemTalla) comboTallas.getSelectedItem();
                    int actual = sel != null ? sel.talla.getStockActual() : 0;
                    txtCantidad.setText(String.valueOf(actual + p));
                }
            });
            panelPresets.add(btnPreset);
        }
        panel.add(panelPresets);
        panel.add(Box.createVerticalStrut(16));

        // Tarjeta resumen del cambio proyectado
        JPanel cardCalculo = new JPanel(new GridLayout(2, 2, 8, 4));
        cardCalculo.setBackground(Estilo.FONDO_EXACTO);
        cardCalculo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_EXACTO, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        cardCalculo.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));

        JLabel lblTitActual = new JLabel("Stock en sistema:");
        lblTitActual.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTitActual.setForeground(Estilo.TEXTO_SECUNDARIO);

        JLabel lblTitNuevo = new JLabel("Nuevo Stock resultante:", SwingConstants.RIGHT);
        lblTitNuevo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTitNuevo.setForeground(Estilo.TEXTO_SECUNDARIO);

        lblStockActual.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblStockActual.setForeground(Estilo.TEXTO_OSCURO);

        lblResultado.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblResultado.setForeground(Estilo.VERDE_OSCURO);
        lblResultado.setHorizontalAlignment(SwingConstants.RIGHT);

        cardCalculo.add(lblTitActual);
        cardCalculo.add(lblTitNuevo);
        cardCalculo.add(lblStockActual);
        cardCalculo.add(lblResultado);

        panel.add(cardCalculo);
        panel.add(Box.createVerticalStrut(14));

        // Motivo del ajuste
        panel.add(seccionEtiqueta("4. MOTIVO / OBSERVACIÓN DE LA MODIFICACIÓN"));
        panel.add(Box.createVerticalStrut(6));
        comboMotivos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboMotivos.setBackground(Color.WHITE);
        comboMotivos.setForeground(Estilo.TEXTO_OSCURO);
        comboMotivos.setMaximumSize(new Dimension(Short.MAX_VALUE, 34));
        panel.add(comboMotivos);

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
        btnGuardar.addActionListener(e -> guardarModificacion());

        footer.add(btnCancelar);
        footer.add(btnGuardar);
        return footer;
    }

    private JLabel seccionEtiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(Estilo.TEXTO_DESCUENTO);
        return l;
    }

    private void configurarEventos() {
        comboTallas.addActionListener(e -> actualizarCalculos());
        rbReabastecer.addActionListener(e -> actualizarCalculos());
        rbAjusteFisico.addActionListener(e -> actualizarCalculos());

        txtCantidad.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { actualizarCalculos(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizarCalculos(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizarCalculos(); }
        });
    }

    private void actualizarCalculos() {
        ItemTalla sel = (ItemTalla) comboTallas.getSelectedItem();
        if (sel == null) {
            lblStockActual.setText("-");
            lblResultado.setText("-");
            btnGuardar.setEnabled(false);
            return;
        }

        int stockActual = sel.talla.getStockActual();
        lblStockActual.setText(stockActual + " unidades");

        String texto = txtCantidad.getText().trim();
        if (texto.isEmpty()) {
            lblResultado.setText("Ingresa cantidad");
            lblResultado.setForeground(Estilo.ERROR);
            btnGuardar.setEnabled(false);
            return;
        }

        try {
            int valor = Integer.parseInt(texto);
            if (rbReabastecer.isSelected()) {
                if (valor <= 0) {
                    lblResultado.setText("Debe ser mayor a 0");
                    lblResultado.setForeground(Estilo.ERROR);
                    btnGuardar.setEnabled(false);
                    return;
                }
                int nuevoStock = stockActual + valor;
                lblResultado.setText(nuevoStock + " unidades");
                lblResultado.setForeground(Estilo.VERDE_OSCURO);
                btnGuardar.setEnabled(true);
            } else {
                if (valor < 0) {
                    lblResultado.setText("No puede ser negativo");
                    lblResultado.setForeground(Estilo.ERROR);
                    btnGuardar.setEnabled(false);
                    return;
                }
                lblResultado.setText(valor + " unidades");
                lblResultado.setForeground(Estilo.TEXTO_EXACTO);
                btnGuardar.setEnabled(true);
            }
        } catch (NumberFormatException ex) {
            lblResultado.setText("Número no válido");
            lblResultado.setForeground(Estilo.ERROR);
            btnGuardar.setEnabled(false);
        }
    }

    private void guardarModificacion() {
        ItemTalla sel = (ItemTalla) comboTallas.getSelectedItem();
        if (sel == null) return;

        int idTalla = sel.talla.getId();
        int valor = Integer.parseInt(txtCantidad.getText().trim());
        boolean esReabastecer = rbReabastecer.isSelected();

        navegacion.ejecutar(() -> {
            if (esReabastecer) {
                return control.reabastecerStock(idTalla, valor);
            } else {
                return control.ajustarStock(idTalla, valor);
            }
        }, exito -> {
            navegacion.cerrarModal();
            if (alTerminar != null) alTerminar.run();
            JOptionPane.showMessageDialog(this,
                    "Existencias actualizadas correctamente para la talla " + sel.talla.getTalla() + ".",
                    "Inventario Actualizado", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
