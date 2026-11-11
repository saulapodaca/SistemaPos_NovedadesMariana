package vistas;

import controles.ControlNavegacion;
import controles.ControlRealizarVenta;
import utilerias.Estilo;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class PanelModalCobro extends JPanel {
    private final JTextField recibido = new JTextField();
    private final JLabel cambioDisplay = new JLabel();
    private final JLabel lblDesgloseTotal = new JLabel();
    private final JLabel lblDesgloseRecibido = new JLabel();
    private final JLabel lblDesgloseCambio = new JLabel();
    private final JLabel lblRecibido = new JLabel("EFECTIVO RECIBIDO");
    private final JLabel lblConceptoRecibido = new JLabel("Efectivo recibido");
    private final JButton confirmar = new JButton("Confirmar Pago e Imprimir Ticket");
    private final List<AbstractButton> controlesEfectivo = new ArrayList<>();
    private final List<JToggleButton> pestañas = new ArrayList<>();
    private final ControlRealizarVenta control;
    private final ControlNavegacion navegacion;
    private final Timer espera;
    private String metodoSeleccionado = "EFECTIVO";
    private boolean procesando;
    private boolean importeValido;

    public PanelModalCobro(ControlRealizarVenta control, ControlNavegacion navegacion) {
        super(new BorderLayout());
        this.control = control;
        this.navegacion = navegacion;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(740, 630));
        add(crearCabecera(), BorderLayout.NORTH);

        JPanel cuerpoPrincipal = new JPanel(new BorderLayout(0, 16));
        cuerpoPrincipal.setBackground(Color.WHITE);
        cuerpoPrincipal.setBorder(new EmptyBorder(16, 24, 20, 24));
        cuerpoPrincipal.add(crearBarraMetodos(), BorderLayout.NORTH);
        JPanel gridCentral = new JPanel(new GridBagLayout());
        gridCentral.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.weightx = .35;
        gbc.insets = new Insets(0, 0, 0, 16);
        gridCentral.add(crearColumnaIzquierda(), gbc);
        gbc.gridx = 1;
        gbc.weightx = .65;
        gbc.insets = new Insets(0, 0, 0, 0);
        gridCentral.add(crearColumnaDerecha(), gbc);
        cuerpoPrincipal.add(gridCentral, BorderLayout.CENTER);
        add(cuerpoPrincipal, BorderLayout.CENTER);

        espera = new Timer(150, evento -> {
            if (!navegacion.estaOcupado()) {
                ((Timer) evento.getSource()).stop();
                setProcesando(false);
            }
        });
        recibido.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { actualizarCalculos(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizarCalculos(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizarCalculos(); }
        });
        recibido.setText(importeExacto());
        recibido.selectAll();
        actualizarCalculos();
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "confirmar");
        getActionMap().put("confirmar", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evento) { confirmar.doClick(); }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "regresar");
        getActionMap().put("regresar", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent evento) { navegacion.cerrarModal(); }
        });
    }

    private JPanel crearCabecera() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Estilo.CABECERA_COBRO);
        header.setBorder(new EmptyBorder(14, 24, 14, 24));
        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulos.setOpaque(false);
        JLabel titulo = new JLabel("Procesar Cobro");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        titulo.setForeground(Color.WHITE);
        JLabel subtitulo = new JLabel("Cobro de venta en curso");
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
        cerrar.setToolTipText("Cerrar cobro");
        cerrar.getAccessibleContext().setAccessibleName("Cerrar cobro");
        cerrar.addActionListener(e -> navegacion.cerrarModal());
        header.add(cerrar, BorderLayout.EAST);
        return header;
    }

    private JPanel crearBarraMetodos() {
        JPanel tabs = new JPanel(new GridLayout(1, 3, 10, 0));
        tabs.setOpaque(false);
        String[] nombres = {"Efectivo", "Tarjeta", "Transferencia"};
        String[] valores = {"EFECTIVO", "TARJETA", "TRANSFERENCIA"};
        ButtonGroup grupo = new ButtonGroup();
        for (int i = 0; i < nombres.length; i++) {
            String valor = valores[i];
            JToggleButton tab = new JToggleButton(nombres[i]);
            tab.setFont(new Font("SansSerif", Font.BOLD, 13));
            tab.setFocusPainted(false);
            tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tab.addItemListener(e -> aplicarEstiloPestaña(tab));
            tab.addActionListener(e -> {
                metodoSeleccionado = valor;
                actualizarControles();
                recibido.setText(importeExacto());
                recibido.selectAll();
                actualizarCalculos();
            });
            grupo.add(tab);
            pestañas.add(tab);
            tabs.add(tab);
            tab.setSelected(i == 0);
            aplicarEstiloPestaña(tab);
        }
        return tabs;
    }

    private void aplicarEstiloPestaña(JToggleButton tab) {
        tab.setBackground(tab.isSelected() ? Estilo.FONDO_TECLAS : Color.WHITE);
        tab.setForeground(tab.isSelected() ? Estilo.TEXTO_OSCURO : Estilo.TEXTO_SECUNDARIO);
        tab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(tab.isSelected() ? Estilo.BORDE_ACTIVO : Estilo.BORDE_TARJETA),
                new EmptyBorder(8, 12, 8, 12)));
    }

    private JPanel crearColumnaIzquierda() {
        JPanel col = columna();
        col.setPreferredSize(new Dimension(215, 450));
        JPanel cardTotal = new JPanel(new GridLayout(3, 1, 0, 2));
        cardTotal.setBackground(Estilo.CABECERA_COBRO);
        cardTotal.setBorder(new EmptyBorder(12, 14, 12, 14));
        cardTotal.setAlignmentX(LEFT_ALIGNMENT);
        cardTotal.setMaximumSize(new Dimension(Short.MAX_VALUE, 95));
        JLabel tag = new JLabel("TOTAL A PAGAR", SwingConstants.CENTER);
        tag.setFont(new Font("SansSerif", Font.BOLD, 11));
        tag.setForeground(Estilo.AZUL_CLARO);
        JLabel monto = new JLabel(Estilo.dinero(control.getVenta().getTotal()), SwingConstants.CENTER);
        monto.setFont(new Font("SansSerif", Font.BOLD, 24));
        monto.setForeground(Color.WHITE);
        JLabel iva = new JLabel("MXN · IVA incluido", SwingConstants.CENTER);
        iva.setFont(new Font("SansSerif", Font.PLAIN, 10));
        iva.setForeground(Estilo.AZUL_CLARO);
        cardTotal.add(tag);
        cardTotal.add(monto);
        cardTotal.add(iva);
        col.add(cardTotal);
        col.add(Box.createVerticalStrut(10));

        JPanel teclado = new JPanel(new GridLayout(4, 3, 6, 6));
        teclado.setOpaque(false);
        teclado.setAlignmentX(LEFT_ALIGNMENT);
        teclado.setPreferredSize(new Dimension(215, 160));
        teclado.setMaximumSize(new Dimension(Short.MAX_VALUE, 160));
        for (String tecla : new String[]{"7", "8", "9", "4", "5", "6", "1", "2", "3", ".", "0", "⌫"}) {
            JButton boton = botonSecundario(tecla);
            boton.setFocusable(false);
            boton.addActionListener(e -> pulsarTecla(tecla));
            controlesEfectivo.add(boton);
            teclado.add(boton);
        }
        col.add(teclado);
        col.add(Box.createVerticalStrut(6));
        JButton limpiar = botonSecundario("Limpiar");
        limpiar.addActionListener(e -> recibido.setText(""));
        controlesEfectivo.add(limpiar);
        col.add(limpiar);
        col.add(Box.createVerticalStrut(10));
        JLabel billetes = etiqueta("BILLETES RÁPIDOS");
        col.add(billetes);
        col.add(Box.createVerticalStrut(6));
        JPanel gridBilletes = new JPanel(new GridLayout(2, 3, 6, 6));
        gridBilletes.setOpaque(false);
        gridBilletes.setAlignmentX(LEFT_ALIGNMENT);
        gridBilletes.setPreferredSize(new Dimension(215, 70));
        gridBilletes.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
        for (int valor : new int[]{20, 50, 100, 200, 500, 1000}) {
            JButton boton = botonSecundario("+$" + valor);
            boton.addActionListener(e -> agregarBillete(valor));
            controlesEfectivo.add(boton);
            gridBilletes.add(boton);
        }
        col.add(gridBilletes);
        col.add(Box.createVerticalStrut(6));
        JButton exacto = botonSecundario("Monto exacto");
        exacto.setBackground(Estilo.FONDO_EXACTO);
        exacto.setForeground(Estilo.TEXTO_EXACTO);
        exacto.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_EXACTO));
        exacto.addActionListener(e -> recibido.setText(importeExacto()));
        controlesEfectivo.add(exacto);
        col.add(exacto);
        return col;
    }

    private JPanel crearColumnaDerecha() {
        JPanel col = columna();
        col.setPreferredSize(new Dimension(380, 450));
        lblRecibido.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblRecibido.setForeground(Estilo.TEXTO_SECUNDARIO);
        lblRecibido.setLabelFor(recibido);
        col.add(lblRecibido);
        col.add(Box.createVerticalStrut(4));
        JPanel input = tarjeta(new BorderLayout(8, 0), Color.WHITE, Estilo.BORDE_TARJETA, 48);
        JLabel signo = new JLabel("$");
        signo.setFont(new Font("SansSerif", Font.BOLD, 20));
        signo.setForeground(Estilo.TEXTO_SECUNDARIO);
        input.add(signo, BorderLayout.WEST);
        recibido.setFont(new Font("SansSerif", Font.BOLD, 22));
        recibido.setHorizontalAlignment(JTextField.RIGHT);
        recibido.setBorder(null);
        input.add(recibido, BorderLayout.CENTER);
        col.add(input);
        col.add(Box.createVerticalStrut(12));
        JPanel cardCambio = tarjeta(new BorderLayout(8, 0), Estilo.VERDE_SUAVE, Estilo.BORDE_VERDE, 65);
        JLabel tituloCambio = etiqueta("CAMBIO A ENTREGAR");
        tituloCambio.setForeground(Estilo.VERDE_OSCURO);
        cambioDisplay.setFont(new Font("Monospaced", Font.BOLD, 24));
        cambioDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
        cardCambio.add(tituloCambio, BorderLayout.WEST);
        cardCambio.add(cambioDisplay, BorderLayout.CENTER);
        col.add(cardCambio);
        col.add(Box.createVerticalStrut(12));
        JPanel desglose = tarjeta(new BorderLayout(0, 12), Estilo.FONDO_TECLAS, Estilo.BORDE_TARJETA, 140);
        desglose.add(etiqueta("DESGLOSE DEL PAGO"), BorderLayout.NORTH);
        JPanel filas = new JPanel(new GridLayout(3, 2, 0, 8));
        filas.setOpaque(false);
        JLabel[] conceptos = {new JLabel("Total de venta"), lblConceptoRecibido, new JLabel("Cambio")};
        JLabel[] importes = {lblDesgloseTotal, lblDesgloseRecibido, lblDesgloseCambio};
        for (int i = 0; i < conceptos.length; i++) {
            conceptos[i].setForeground(Estilo.TEXTO_SECUNDARIO);
            conceptos[i].setFont(new Font("SansSerif", Font.PLAIN, 12));
            importes[i].setHorizontalAlignment(SwingConstants.RIGHT);
            importes[i].setFont(new Font("SansSerif", Font.BOLD, 13));
            filas.add(conceptos[i]);
            filas.add(importes[i]);
        }
        desglose.add(filas, BorderLayout.CENTER);
        col.add(desglose);
        col.add(Box.createVerticalGlue());
        confirmar.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmar.setBackground(Estilo.VERDE_COBRO);
        confirmar.setForeground(Color.WHITE);
        confirmar.setFocusPainted(false);
        confirmar.setAlignmentX(LEFT_ALIGNMENT);
        confirmar.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));
        confirmar.setBorder(new EmptyBorder(12, 12, 12, 12));
        confirmar.addActionListener(e -> confirmarPago());
        col.add(confirmar);
        col.add(Box.createVerticalStrut(8));
        JButton volver = botonSecundario("Regresar");
        volver.addActionListener(e -> navegacion.cerrarModal());
        col.add(volver);
        return col;
    }

    private JPanel columna() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        return panel;
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(Estilo.TEXTO_SECUNDARIO);
        return label;
    }

    private JPanel tarjeta(LayoutManager layout, Color fondo, Color borde, int alto) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(fondo);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde), new EmptyBorder(10, 14, 10, 14)));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, alto));
        return panel;
    }

    private JButton botonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setBackground(Estilo.FONDO_TECLAS);
        boton.setForeground(Estilo.TEXTO_SECUNDARIO);
        boton.setFocusPainted(false);
        boton.setAlignmentX(LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA), new EmptyBorder(5, 3, 5, 3)));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private String importeExacto() {
        return String.format(Locale.ROOT, "%.2f", control.getVenta().getTotal());
    }

    private void pulsarTecla(String tecla) {
        if (!recibido.isEditable() || navegacion.estaOcupado()) return;
        if (tecla.equals("⌫")) {
            if (recibido.getSelectionStart() != recibido.getSelectionEnd()) {
                recibido.replaceSelection("");
            } else if (recibido.getCaretPosition() > 0) {
                int posicion = recibido.getCaretPosition();
                recibido.select(posicion - 1, posicion);
                recibido.replaceSelection("");
            }
        } else {
            recibido.replaceSelection(tecla);
        }
        recibido.requestFocusInWindow();
    }

    private void agregarBillete(int valor) {
        if (!recibido.isEditable() || navegacion.estaOcupado()) return;
        float actual;
        try {
            actual = Float.parseFloat(recibido.getText().trim());
            if (!Float.isFinite(actual) || actual < 0) actual = 0;
        } catch (NumberFormatException ex) {
            actual = 0;
        }
        recibido.setText(String.format(Locale.ROOT, "%.2f", actual + valor));
    }

    private void actualizarControles() {
        boolean efectivo = metodoSeleccionado.equals("EFECTIVO");
        recibido.setEditable(efectivo && !procesando);
        lblRecibido.setText(efectivo ? "EFECTIVO RECIBIDO" : "IMPORTE DEL PAGO");
        lblConceptoRecibido.setText(efectivo ? "Efectivo recibido" : "Importe pagado");
        for (AbstractButton boton : controlesEfectivo) boton.setEnabled(efectivo && !procesando);
        for (JToggleButton tab : pestañas) tab.setEnabled(!procesando);
        confirmar.setEnabled(!procesando && importeValido);
    }

    private void actualizarCalculos() {
        lblDesgloseTotal.setText(Estilo.dinero(control.getVenta().getTotal()));
        importeValido = false;
        float monto;
        try {
            monto = Float.parseFloat(recibido.getText().trim());
            if (!Float.isFinite(monto) || monto < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            mostrarImporteInvalido("Importe inválido");
            lblDesgloseRecibido.setText("—");
            actualizarControles();
            return;
        }
        lblDesgloseRecibido.setText(Estilo.dinero(monto));
        try {
            float cambio = control.calcularCambio(monto);
            cambioDisplay.setForeground(Estilo.VERDE_COBRO);
            cambioDisplay.setText(Estilo.dinero(cambio));
            lblDesgloseCambio.setForeground(Estilo.VERDE_COBRO);
            lblDesgloseCambio.setText(Estilo.dinero(cambio));
            importeValido = true;
        } catch (IllegalArgumentException ex) {
            mostrarImporteInvalido("Insuficiente");
        }
        actualizarControles();
    }

    private void mostrarImporteInvalido(String mensaje) {
        cambioDisplay.setForeground(Estilo.ERROR);
        cambioDisplay.setText(Estilo.dinero(0));
        lblDesgloseCambio.setForeground(Estilo.ERROR);
        lblDesgloseCambio.setText(mensaje);
    }

    private void setProcesando(boolean valor) {
        procesando = valor;
        actualizarControles();
    }

    private void confirmarPago() {
        if (navegacion.estaOcupado() || procesando || !importeValido) return;
        float monto = Float.parseFloat(recibido.getText().trim());
        String metodo = metodoSeleccionado;
        setProcesando(true);
        espera.start();
        navegacion.ejecutar(() -> control.procesarVenta(metodo, monto), venta -> {
            navegacion.abrirPantallaTicket();
            navegacion.ejecutar(() -> {
                try {
                    return control.imprimirTicket() ? "Ticket enviado a impresión."
                            : "La venta está guardada. El servicio de impresión aún no está disponible.";
                } catch (RuntimeException ex) {
                    return "La venta está guardada. No se pudo imprimir el ticket. Puedes reintentarlo desde esta pantalla.";
                }
            }, navegacion::informar);
        });
    }

    @Override public void removeNotify() {
        espera.stop();
        super.removeNotify();
    }
}