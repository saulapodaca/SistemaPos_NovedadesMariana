package vistas;

import controles.ControlNavegacion;
import controles.ControlRealizarVenta;
import utilerias.Estilo;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class PanelModalDescuento extends JPanel {
    private final JTextField txtMontoFijo = new JTextField();
    private final ButtonGroup grupoPorcentajes = new ButtonGroup();
    private final ButtonGroup grupoMotivos = new ButtonGroup();
    private final List<AbstractButton> opciones = new ArrayList<>();
    private final JButton aplicar = new JButton("✓ Aplicar rebaja");
    private final ControlRealizarVenta control;
    private final ControlNavegacion navegacion;
    private final Timer espera;

    public PanelModalDescuento(ControlRealizarVenta control, ControlNavegacion navegacion) {
        super(new BorderLayout());
        this.control = control;
        this.navegacion = navegacion;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(460, 540));
        add(crearCabecera(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(Color.WHITE);
        cuerpo.setBorder(new EmptyBorder(16, 20, 18, 20));
        cuerpo.add(etiqueta("DESCUENTO RÁPIDO POR PORCENTAJE"));
        cuerpo.add(Box.createVerticalStrut(8));
        cuerpo.add(crearPorcentajes());
        cuerpo.add(Box.createVerticalStrut(14));
        JLabel fijo = etiqueta("O MONTO FIJO EN MXN");
        fijo.setLabelFor(txtMontoFijo);
        cuerpo.add(fijo);
        cuerpo.add(Box.createVerticalStrut(8));
        cuerpo.add(crearMontoFijo());
        cuerpo.add(Box.createVerticalStrut(14));
        cuerpo.add(etiqueta("MOTIVO DEL DESCUENTO"));
        cuerpo.add(Box.createVerticalStrut(8));
        String[] motivos = {"Promoción de temporada", "Cliente frecuente", "Defecto menor", "Precio acordado"};
        for (int i = 0; i < motivos.length; i++) {
            cuerpo.add(crearMotivo(motivos[i], i == 0));
            cuerpo.add(Box.createVerticalStrut(4));
        }
        cuerpo.add(Box.createVerticalGlue());
        add(cuerpo, BorderLayout.CENTER);
        add(crearAcciones(), BorderLayout.SOUTH);

        espera = new Timer(150, evento -> {
            if (!navegacion.estaOcupado()) {
                ((Timer) evento.getSource()).stop();
                setProcesando(false);
            }
        });
        txtMontoFijo.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                if (!txtMontoFijo.getText().trim().isEmpty()) grupoPorcentajes.clearSelection();
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "aplicar");
        getActionMap().put("aplicar", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evento) { aplicar.doClick(); }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cerrar");
        getActionMap().put("cerrar", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evento) { navegacion.cerrarModal(); }
        });
    }

    private JPanel crearCabecera() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Estilo.CABECERA_COBRO);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 3));
        titulos.setOpaque(false);
        JLabel titulo = new JLabel("Aplicar Descuento a la Venta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        JLabel subtitulo = new JLabel("Subtotal actual: "
                + Estilo.dinero(control.getVenta().getSubtotal()) + " MXN");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(Estilo.AZUL_CLARO);
        titulos.add(titulo);
        titulos.add(subtitulo);
        header.add(titulos, BorderLayout.WEST);
        JButton cerrar = new JButton("×");
        cerrar.setFont(new Font("SansSerif", Font.BOLD, 22));
        cerrar.setForeground(Color.WHITE);
        cerrar.setContentAreaFilled(false);
        cerrar.setBorderPainted(false);
        cerrar.setFocusPainted(false);
        cerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrar.getAccessibleContext().setAccessibleName("Cerrar descuento");
        cerrar.addActionListener(e -> navegacion.cerrarModal());
        header.add(cerrar, BorderLayout.EAST);
        return header;
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(Estilo.TEXTO_DESCUENTO);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JPanel crearPorcentajes() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        for (int porcentaje : new int[]{10, 15, 20, 50}) {
            JToggleButton boton = new JToggleButton(porcentaje + "%");
            boton.setActionCommand(Integer.toString(porcentaje));
            boton.setFont(new Font("SansSerif", Font.BOLD, 15));
            boton.setForeground(Estilo.AZUL_DESCUENTO);
            boton.setBackground(Estilo.FONDO_DESCUENTO);
            boton.setFocusPainted(false);
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Estilo.BORDE_DESCUENTO), new EmptyBorder(8, 0, 8, 0)));
            boton.addItemListener(e -> {
                boton.setForeground(Estilo.AZUL_DESCUENTO);
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(boton.isSelected()
                                ? Estilo.BORDE_ACTIVO : Estilo.BORDE_DESCUENTO),
                        new EmptyBorder(8, 0, 8, 0)));
                boton.setBackground(boton.isSelected() ? Estilo.AZUL_DESCUENTO : Estilo.FONDO_DESCUENTO);
            });
            boton.addActionListener(e -> txtMontoFijo.setText(""));
            grupoPorcentajes.add(boton);
            opciones.add(boton);
            panel.add(boton);
        }
        return panel;
    }

    private JPanel crearMontoFijo() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA), new EmptyBorder(8, 12, 8, 12)));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel signo = new JLabel("$");
        signo.setFont(new Font("Monospaced", Font.BOLD, 15));
        signo.setForeground(Estilo.TEXTO_DESCUENTO);
        panel.add(signo, BorderLayout.WEST);
        txtMontoFijo.setFont(new Font("Monospaced", Font.PLAIN, 15));
        txtMontoFijo.setForeground(Estilo.TEXTO_OSCURO);
        txtMontoFijo.setBorder(null);
        panel.add(txtMontoFijo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearMotivo(String motivo, boolean seleccionado) {
        JPanel item = new JPanel(new BorderLayout());
        item.setAlignmentX(LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        JRadioButton radio = new JRadioButton(motivo);
        radio.setActionCommand(motivo);
        radio.setFont(new Font("SansSerif", Font.PLAIN, 13));
        radio.setForeground(Estilo.TEXTO_OSCURO);
        radio.setOpaque(false);
        radio.setFocusPainted(false);
        radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        radio.addItemListener(e -> pintarMotivo(item, radio.isSelected()));
        grupoMotivos.add(radio);
        opciones.add(radio);
        item.add(radio, BorderLayout.CENTER);
        radio.setSelected(seleccionado);
        pintarMotivo(item, seleccionado);
        return item;
    }

    private void pintarMotivo(JPanel panel, boolean seleccionado) {
        panel.setBackground(seleccionado ? Estilo.FONDO_DESCUENTO : Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(seleccionado ? Estilo.BORDE_DESCUENTO : Color.WHITE),
                new EmptyBorder(4, 10, 4, 10)));
    }

    private JPanel crearAcciones() {
        JPanel acciones = new JPanel(new GridLayout(1, 2, 12, 0));
        acciones.setOpaque(false);
        acciones.setBorder(new EmptyBorder(12, 20, 18, 20));
        JButton cancelar = new JButton("Cancelar");
        cancelar.setFont(new Font("SansSerif", Font.BOLD, 13));
        cancelar.setBackground(Estilo.FONDO_TECLAS);
        cancelar.setForeground(Estilo.TEXTO_DESCUENTO);
        cancelar.setFocusPainted(false);
        cancelar.setPreferredSize(new Dimension(0, 42));
        cancelar.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA));
        cancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelar.addActionListener(e -> navegacion.cerrarModal());
        aplicar.setFont(new Font("SansSerif", Font.BOLD, 13));
        aplicar.setBackground(Estilo.VERDE_DESCUENTO);
        aplicar.setForeground(Color.WHITE);
        aplicar.setFocusPainted(false);
        aplicar.setPreferredSize(new Dimension(0, 42));
        aplicar.setBorder(BorderFactory.createEmptyBorder());
        aplicar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        aplicar.addActionListener(e -> aplicarDescuento());
        acciones.add(cancelar);
        acciones.add(aplicar);
        return acciones;
    }

    private float obtenerPorcentaje() {
        float subtotal = control.getVenta().getSubtotal();
        if (!Float.isFinite(subtotal) || subtotal <= 0) {
            throw new IllegalArgumentException("La venta debe tener un subtotal mayor a cero.");
        }
        if (grupoPorcentajes.getSelection() != null) {
            return Float.parseFloat(grupoPorcentajes.getSelection().getActionCommand());
        }
        String texto = txtMontoFijo.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Selecciona un porcentaje o introduce un monto.");
        }
        float monto;
        try {
            monto = Float.parseFloat(texto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Escribe un importe numérico válido.");
        }
        if (!Float.isFinite(monto) || monto <= 0 || monto >= subtotal) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero y menor al subtotal.");
        }
        float porcentaje = (monto / subtotal) * 100f;
        if (porcentaje >= 100) {
            throw new IllegalArgumentException("El descuento debe dejar un importe por pagar.");
        }
        return porcentaje;
    }

    private void aplicarDescuento() {
        if (navegacion.estaOcupado()) return;
        try {
            float porcentaje = obtenerPorcentaje();
            String motivo = grupoMotivos.getSelection().getActionCommand();
            setProcesando(true);
            espera.start();
            navegacion.ejecutar(() -> control.aplicarDescuento(porcentaje, motivo), venta -> {
                navegacion.refrescarVenta();
                navegacion.cerrarModal();
            });
        } catch (IllegalArgumentException ex) {
            navegacion.error(ex);
        }
    }

    private void setProcesando(boolean procesando) {
        aplicar.setEnabled(!procesando);
        txtMontoFijo.setEditable(!procesando);
        for (AbstractButton opcion : opciones) opcion.setEnabled(!procesando);
    }

    @Override public void removeNotify() {
        espera.stop();
        super.removeNotify();
    }
}