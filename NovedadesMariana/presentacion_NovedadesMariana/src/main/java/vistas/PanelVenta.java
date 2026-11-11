package vistas;

import controles.*;
import dtos.*;
import utilerias.Estilo;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class PanelVenta extends JPanel {

private final java.util.Map<Integer, String> skuPorTalla = new java.util.HashMap<>();
    private String categoriaSeleccionada;
    private final ControlRealizarVenta control;
    private final ControlNavegacion navegacion;

    private final PanelBuscador buscador = new PanelBuscador();
    private final DefaultListModel<ProductoDTO> resultados = new DefaultListModel<>();
    private final JList<ProductoDTO> lista = new JList<>(resultados);
    private final JScrollPane scrollLista = new JScrollPane(lista);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"CANT.", "PRODUCTO", "TALLA", "P. UNITARIO", "TOTAL", ""}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) {
            return !navegacion.estaOcupado() && (columna == 0 || columna == 5); // Solo botones de cantidad y eliminar
        }
    };
    private final JTable tabla = new JTable(modelo);

    // Indicadores del resumen lateral
    private final JLabel lblValSubtotal = new JLabel("$0.00", SwingConstants.RIGHT);
    private final JLabel lblValIva = new JLabel("$0.00", SwingConstants.RIGHT);
    private final JLabel lblValDescuento = new JLabel("-$0.00", SwingConstants.RIGHT);
    private final JLabel lblValTotal = new JLabel("$0.00", SwingConstants.RIGHT);
    private final JLabel lblContadorArticulos = new JLabel("0 artículos en esta venta");
    private final JLabel estadoBusqueda = new JLabel("");

    private final Timer debounce;
    private long revision;
    private boolean activa = true;

    public PanelVenta(ControlRealizarVenta control, ControlNavegacion navegacion) {
        super(new BorderLayout(20, 0));
        this.control = control;
        this.navegacion = navegacion;

        setBackground(Estilo.FONDO_VENTA);
        setBorder(new EmptyBorder(16, 20, 14, 20));

        // Construcción de zonas
        add(crearColumnaIzquierda(), BorderLayout.CENTER);
        add(crearColumnaDerecha(), BorderLayout.EAST);
        add(crearBarraInferior(), BorderLayout.SOUTH);

        // Control del debounce de búsqueda
        debounce = new Timer(300, e -> buscar());
        debounce.setRepeats(false);

        configurarBuscadorYLista();
        buscador.setOnConfirmar(this::seleccionarTalla);
        configurarTabla();

        // Atajos de teclado solicitados
        registrarTecla("F12", navegacion::abrirModalCobro);
        registrarTecla("F4", navegacion::abrirModalDescuento);
        registrarTecla("ESCAPE", navegacion::cancelarVenta);
        registrarTecla("F5", () -> {
            revision++;
            buscar();
        });

        refrescar();
        SwingUtilities.invokeLater(buscador::enfocar);
    }

    private JPanel crearColumnaIzquierda() {
        JPanel col = new JPanel(new BorderLayout(0, 12));
        col.setOpaque(false);

        // Zona Superior: Buscador y Categorías Rápidas (Chips)
        JPanel topBusqueda = new JPanel();
        topBusqueda.setLayout(new BoxLayout(topBusqueda, BoxLayout.Y_AXIS));
        topBusqueda.setOpaque(false);

        // Barra con buscador integrado
        JPanel wrapBuscador = new JPanel(new BorderLayout(8, 0));
        wrapBuscador.setOpaque(false);
        wrapBuscador.add(buscador, BorderLayout.CENTER);

        JButton btnManual = new JButton("+ Código manual");
        btnManual.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnManual.setBackground(Color.WHITE);
        btnManual.setForeground(Estilo.TEXTO_VENTA);
        btnManual.setFocusPainted(false);
        btnManual.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btnManual.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnManual.addActionListener(e -> {
            String codigo = JOptionPane.showInputDialog(this, "Ingresa código de barras o SKU:", "Búsqueda Manual", JOptionPane.QUESTION_MESSAGE);
            if (codigo != null && !codigo.isBlank()) {
                buscador.setTextoBusqueda(codigo.trim());
            }
        });
        wrapBuscador.add(btnManual, BorderLayout.EAST);
        topBusqueda.add(wrapBuscador);
        topBusqueda.add(Box.createVerticalStrut(10));

        // Chips de categorías rápidas
        JPanel panelChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelChips.setOpaque(false);
        String[] categorias = {"Ropa bebé", "Niña", "Niño", "Accesorios"};
        Color[] fondos = {Estilo.FONDO_BEBE, Estilo.FONDO_NINA, Estilo.FONDO_DESCUENTO, Estilo.FONDO_ACCESORIOS};
        Color[] textos = {Estilo.TEXTO_BEBE, Estilo.TEXTO_NINA, Estilo.AZUL_DESCUENTO, Estilo.TEXTO_ACCESORIOS};
        for (int i = 0; i < categorias.length; i++) {
            String categoria = categorias[i];
            JButton chip = new JButton(categoria);
            chip.setFont(new Font("SansSerif", Font.BOLD, 12));
            chip.setBackground(fondos[i]);
            chip.setForeground(textos[i]);
            chip.setFocusPainted(false);
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Estilo.BORDE_TARJETA), new EmptyBorder(6, 12, 6, 12)));
            chip.addActionListener(e -> {
                buscador.setTextoBusqueda("");
                categoriaSeleccionada = categoria;
                revision++;
                debounce.stop();
                scrollLista.setVisible(true);
                resultados.clear();
                estadoBusqueda.setText("Buscando en " + categoria + "…");
                revalidate();
                buscar();
            });
            panelChips.add(chip);
        }
        topBusqueda.add(panelChips);
        topBusqueda.add(Box.createVerticalStrut(6));

        // Panel emergente con resultados de lista
        scrollLista.setPreferredSize(new Dimension(0, 95));
        scrollLista.setVisible(false);
        scrollLista.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1));
        topBusqueda.add(scrollLista);
        estadoBusqueda.setFont(new Font("SansSerif", Font.PLAIN, 12));
        estadoBusqueda.setForeground(Estilo.TEXTO_DESCUENTO);
        topBusqueda.add(estadoBusqueda);
        for (Component componente : topBusqueda.getComponents()) {
            if (componente instanceof JComponent item) item.setAlignmentX(LEFT_ALIGNMENT);
        }

        col.add(topBusqueda, BorderLayout.NORTH);

        // Tabla de prendas seleccionadas
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.getViewport().setBackground(Color.WHITE);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true));
        scrollTabla.setColumnHeaderView(tabla.getTableHeader());
        col.add(scrollTabla, BorderLayout.CENTER);

        return col;
    }

    private JPanel crearColumnaDerecha() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(Color.WHITE);
        col.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));
        col.setPreferredSize(new Dimension(310, 0));

        JLabel tituloResumen = new JLabel("RESUMEN DE VENTA");
        tituloResumen.setFont(new Font("SansSerif", Font.BOLD, 12));
        tituloResumen.setForeground(Estilo.TEXTO_DESCUENTO);
        tituloResumen.setAlignmentX(Component.LEFT_ALIGNMENT);
        col.add(tituloResumen);
        col.add(Box.createVerticalStrut(14));

        // Desglose de importes
        col.add(crearFilaImporte("Subtotal", lblValSubtotal, false));
        col.add(Box.createVerticalStrut(8));
        col.add(crearFilaImporte("IVA (16%)", lblValIva, false));
        col.add(Box.createVerticalStrut(8));
        col.add(crearFilaImporte("Descuento", lblValDescuento, false));
        col.add(Box.createVerticalStrut(14));

        // Separador fino
        JSeparator sep = new JSeparator();
        sep.setForeground(Estilo.BORDE_TARJETA);
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        col.add(sep);
        col.add(Box.createVerticalStrut(14));

        // Total grande destacado
        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setOpaque(false);
        panelTotal.setMaximumSize(new Dimension(Short.MAX_VALUE, 65));

        JLabel lblTxtTotal = new JLabel("Total");
        lblTxtTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTxtTotal.setForeground(Estilo.TEXTO_OSCURO);

        JPanel panelMontoTotal = new JPanel(new BorderLayout(0, 2));
        panelMontoTotal.setOpaque(false);

        lblValTotal.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblValTotal.setForeground(Estilo.CABECERA_COBRO);

        JLabel lblIvaInc = new JLabel("MXN · IVA incluido", SwingConstants.RIGHT);
        lblIvaInc.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblIvaInc.setForeground(Estilo.TEXTO_DESCUENTO);

        panelMontoTotal.add(lblValTotal, BorderLayout.CENTER);
        panelMontoTotal.add(lblIvaInc, BorderLayout.SOUTH);

        panelTotal.add(lblTxtTotal, BorderLayout.WEST);
        panelTotal.add(panelMontoTotal, BorderLayout.EAST);
        col.add(panelTotal);
        col.add(Box.createVerticalStrut(16));

        // Tarjeta de Cliente
        JPanel cardCliente = new JPanel(new BorderLayout(8, 0));
        cardCliente.setBackground(Estilo.FONDO_TECLAS);
        cardCliente.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        cardCliente.setMaximumSize(new Dimension(Short.MAX_VALUE, 54));

        JLabel iconCliente = new JLabel("👤");
        iconCliente.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel txtCliente = new JPanel(new GridLayout(2, 1, 0, 1));
        txtCliente.setOpaque(false);
        JLabel lblNombreCliente = new JLabel("Cliente general");
        lblNombreCliente.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNombreCliente.setForeground(Estilo.TEXTO_OSCURO);
        JLabel lblSocio = new JLabel("Sin número de socio");
        lblSocio.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSocio.setForeground(Estilo.TEXTO_DESCUENTO);
        txtCliente.add(lblNombreCliente);
        txtCliente.add(lblSocio);

        JButton btnAsignar = new JButton("Asignar");
        btnAsignar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAsignar.setForeground(Estilo.AZUL_DESCUENTO);
        btnAsignar.setContentAreaFilled(false);
        btnAsignar.setBorderPainted(false);
        btnAsignar.setFocusPainted(false);
        btnAsignar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cardCliente.add(iconCliente, BorderLayout.WEST);
        cardCliente.add(txtCliente, BorderLayout.CENTER);
        btnAsignar.setEnabled(false);
        btnAsignar.setToolTipText("La asignación de clientes está pendiente de implementación.");
        cardCliente.add(btnAsignar, BorderLayout.EAST);
        col.add(cardCliente);

        col.add(Box.createVerticalGlue());

        // Botón Cobrar (F12)
        JButton btnCobrar = new JButton("Cobrar   [F12]");
        btnCobrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCobrar.setBackground(Estilo.VERDE_COBRO);
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFocusPainted(false);
        btnCobrar.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));
        btnCobrar.setPreferredSize(new Dimension(270, 46));
        btnCobrar.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        btnCobrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCobrar.addActionListener(e -> navegacion.abrirModalCobro());
        col.add(btnCobrar);
        col.add(Box.createVerticalStrut(10));

        // Botón Aplicar Descuento (F4)
        JButton btnDescuento = new JButton("Aplicar Descuento   [F4]");
        btnDescuento.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDescuento.setBackground(Estilo.FONDO_TECLAS);
        btnDescuento.setForeground(Estilo.TEXTO_VENTA);
        btnDescuento.setFocusPainted(false);
        btnDescuento.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        btnDescuento.setPreferredSize(new Dimension(270, 40));
        btnDescuento.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true));
        btnDescuento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDescuento.addActionListener(e -> navegacion.abrirModalDescuento());
        col.add(btnDescuento);
        col.add(Box.createVerticalStrut(10));

        JButton quitarDescuento = new JButton("Quitar descuento");
        quitarDescuento.setBackground(Estilo.FONDO_TECLAS);
        quitarDescuento.setForeground(Estilo.TEXTO_VENTA);
        quitarDescuento.setFocusPainted(false);
        quitarDescuento.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
        quitarDescuento.setPreferredSize(new Dimension(270, 32));
        quitarDescuento.addActionListener(e ->
                navegacion.ejecutar(control::quitarDescuento, venta -> refrescar()));
        col.add(quitarDescuento);
        col.add(Box.createVerticalStrut(10));

        // Botón Cancelar Venta (ESC)
        JButton btnCancelar = new JButton("✕  Cancelar Venta   [ESC]");
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCancelar.setBackground(Estilo.FONDO_CANCELAR);
        btnCancelar.setForeground(Estilo.ERROR);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        btnCancelar.setPreferredSize(new Dimension(270, 40));
        btnCancelar.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_CANCELAR, 1, true));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> navegacion.cancelarVenta());
        col.add(btnCancelar);
        for (Component componente : col.getComponents()) {
            if (componente instanceof JComponent item) item.setAlignmentX(LEFT_ALIGNMENT);
        }

        return col;
    }

    private JPanel crearBarraInferior() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 4, 4, 4));

        lblContadorArticulos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblContadorArticulos.setForeground(Estilo.TEXTO_DESCUENTO);
        footer.add(lblContadorArticulos, BorderLayout.WEST);

        JLabel infoTerminal = new JLabel("F5 Actualizar búsqueda · Enter Agregar producto");
        infoTerminal.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoTerminal.setForeground(Estilo.TEXTO_DESCUENTO);
        footer.add(infoTerminal, BorderLayout.EAST);

        return footer;
    }

    private JPanel crearFilaImporte(String titulo, JLabel valorLabel, boolean bold) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Short.MAX_VALUE, 22));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 13));
        lblTit.setForeground(Estilo.TEXTO_VENTA);

        valorLabel.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 13));
        valorLabel.setForeground(Estilo.TEXTO_OSCURO);

        fila.add(lblTit, BorderLayout.WEST);
        fila.add(valorLabel, BorderLayout.EAST);
        return fila;
    }

    private void configurarBuscadorYLista() {
        lista.setVisibleRowCount(3);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                ProductoDTO prod = (ProductoDTO) value;
                label.setText(prod.getNombre() + "   |   SKU: " + prod.getSku() + "   |   " + Estilo.dinero(prod.getPrecio()));
                label.setFont(new Font("SansSerif", Font.PLAIN, 13));
                label.setBorder(new EmptyBorder(8, 12, 8, 12));
                return label;
            }
        });

        buscador.setOnBusquedaChange(texto -> {
            categoriaSeleccionada = null;
            debounce.stop();
            revision++;
            resultados.clear();
            if (texto.isBlank()) {
                scrollLista.setVisible(false);
                estadoBusqueda.setText("");
            } else {
                scrollLista.setVisible(true);
                estadoBusqueda.setText("Buscando…");
                debounce.restart();
            }
            revalidate();
            repaint();
        });

        lista.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent evento) {
                if (evento.getClickCount() == 2) seleccionarTalla();
            }
        });

        lista.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "agregar");
        lista.getActionMap().put("agregar", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { seleccionarTalla(); }
        });
    }

    private void configurarTabla() {
        tabla.setRowHeight(52);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setMinWidth(112);
        tabla.getColumnModel().getColumn(5).setMinWidth(40);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(Estilo.BORDE_TARJETA);

        tabla.getTableHeader().setBackground(Estilo.CABECERA_COBRO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 38));

        // Renderers y Editors personalizados por columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(120); // Cantidad [- 1 +]
        tabla.getColumnModel().getColumn(1).setPreferredWidth(280); // Producto + SKU
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);  // Talla Badge
        tabla.getColumnModel().getColumn(3).setPreferredWidth(90);  // P. Unitario
        tabla.getColumnModel().getColumn(4).setPreferredWidth(90);  // Total
        tabla.getColumnModel().getColumn(5).setPreferredWidth(45);  // Botón Eliminar [✕]

        tabla.getColumnModel().getColumn(0).setCellRenderer(new CeldaCantidadRenderer());
        tabla.getColumnModel().getColumn(0).setCellEditor(new CeldaCantidadEditor());

        tabla.getColumnModel().getColumn(1).setCellRenderer(new CeldaProductoRenderer());
        tabla.getColumnModel().getColumn(2).setCellRenderer(new CeldaTallaRenderer());

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tabla.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tabla.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        tabla.getColumnModel().getColumn(5).setCellRenderer(new CeldaEliminarRenderer());
        tabla.getColumnModel().getColumn(5).setCellEditor(new CeldaEliminarEditor());
    }

    private void registrarTecla(String tecla, Runnable accion) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla), tecla);
        getActionMap().put(tecla, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent evento) { accion.run(); }
        });
    }

    private void buscar() {
        String texto = buscador.getTextoBusqueda();
        String categoria = categoriaSeleccionada;
        if (!activa || (texto.isBlank() && categoria == null)) return;
        long solicitud = revision;

        new SwingWorker<List<ProductoDTO>, Void>() {
            @Override protected List<ProductoDTO> doInBackground() { return categoria == null ? control.buscarProductos(texto) : control.buscarProductosPorCategoria(categoria); }
            @Override protected void done() {
                if (!activa || solicitud != revision) return;
                try {
                    resultados.clear();
                    for (ProductoDTO producto : get()) resultados.addElement(producto);
                    estadoBusqueda.setText(resultados.isEmpty() ? "Sin coincidencias" : resultados.size() + " productos encontrados");
                    scrollLista.setVisible(!resultados.isEmpty());
                    if (!resultados.isEmpty()) lista.setSelectedIndex(0);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    estadoBusqueda.setText("Error al consultar productos.");
                }
            }
        }.execute();
    }

    private void seleccionarTalla() {
        ProductoDTO producto = lista.getSelectedValue();
        if (producto == null || navegacion.estaOcupado()) return;

        navegacion.ejecutar(() -> control.obtenerTallas(producto.getId().intValue()), tallas -> {
            if (tallas.isEmpty()) {
                navegacion.informar("El producto no cuenta con existencias ni tallas registradas.");
                return;
            }
            JComboBox<ProductoTallaDTO> opciones = new JComboBox<>(tallas.toArray(ProductoTallaDTO[]::new));
            opciones.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                    if (value instanceof ProductoTallaDTO talla) {
                        label.setText(talla.getTalla() + "  (Stock: " + talla.getStockActual() + ")");
                    }
                    return label;
                }
            });

            if (JOptionPane.showConfirmDialog(this, opciones, "Selecciona la talla",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                ProductoTallaDTO talla = (ProductoTallaDTO) opciones.getSelectedItem();
                navegacion.ejecutar(() -> control.agregarProductoAVenta(producto, talla, 1), venta -> {
                    skuPorTalla.put(talla.getId(), producto.getSku());
                    scrollLista.setVisible(false);
                    buscador.setTextoBusqueda("");
                    refrescar();
                });
            }
        });
    }

    private void cambiarCantidad(int indexFila, int incremento) {
        if (navegacion.estaOcupado() || indexFila < 0 || indexFila >= control.getVenta().getDetalles().size()) return;
        DetalleVentaDTO detalle = control.getVenta().getDetalles().get(indexFila);
        int nuevaCantidad = detalle.getCantidad() + incremento;

        if (nuevaCantidad <= 0) {
            eliminarFila(indexFila);
        } else {
            navegacion.ejecutar(() -> control.modificarCantidadProducto(detalle.getIdProductoTalla(), nuevaCantidad),
                    venta -> refrescar());
        }
    }

    private void eliminarFila(int indexFila) {
        if (navegacion.estaOcupado() || indexFila < 0 || indexFila >= control.getVenta().getDetalles().size()) return;
        int id = control.getVenta().getDetalles().get(indexFila).getIdProductoTalla();
        navegacion.ejecutar(() -> control.eliminarProductoDeVenta(id), venta -> refrescar());
    }

    public void refrescar() {
        if (tabla.isEditing()) tabla.getCellEditor().cancelCellEditing();
        modelo.setRowCount(0);
        VentaDTO venta = control.getVenta();

        int totalPrendas = 0;
        for (DetalleVentaDTO d : venta.getDetalles()) {
            totalPrendas += d.getCantidad();
            modelo.addRow(new Object[]{
                    d.getCantidad(),
                    new String[]{d.getNombreProducto(), skuPorTalla.getOrDefault(d.getIdProductoTalla(), "")},
                    d.getTalla(),
                    Estilo.dinero(d.getPrecioUnitario()),
                    Estilo.dinero(d.getSubtotal()),
                    "✕"
            });
        }

        lblContadorArticulos.setText(totalPrendas + " artículos en esta venta");
        lblValSubtotal.setText(Estilo.dinero(venta.getSubtotal()));
        lblValIva.setText(Estilo.dinero(venta.getIva()));
        lblValDescuento.setText("-" + Estilo.dinero(control.getDescuento()));
        lblValTotal.setText(Estilo.dinero(venta.getTotal()));
    }

    public void detenerBusqueda() {
        activa = false;
        revision++;
        debounce.stop();
    }

    // ==========================================
    // RENDERERS Y EDITORES DE COMPONENTES INTERNOS
    // ==========================================

    private class CeldaCantidadRenderer implements javax.swing.table.TableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 10));
        private final JButton btnMenos = new JButton("−");
        private final JButton btnMas = new JButton("+");
        private final JLabel lblCant = new JLabel("1", SwingConstants.CENTER);

        public CeldaCantidadRenderer() {
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            formatearBoton(btnMenos);
            formatearBoton(btnMas);
            lblCant.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblCant.setPreferredSize(new Dimension(24, 24));
            panel.add(btnMenos);
            panel.add(lblCant);
            panel.add(btnMas);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            panel.setBackground(isSelected ? Estilo.FONDO_DESCUENTO : Color.WHITE);
            lblCant.setText(String.valueOf(value));
            return panel;
        }

        private void formatearBoton(JButton btn) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setBackground(Estilo.FONDO_VENTA);
            btn.setForeground(Estilo.TEXTO_VENTA);
            btn.setPreferredSize(new Dimension(26, 26));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true));
        }
    }

    private class CeldaCantidadEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 10));
        private final JButton btnMenos = new JButton("−");
        private final JButton btnMas = new JButton("+");
        private final JLabel lblCant = new JLabel("1", SwingConstants.CENTER);
        private int filaActual;

        public CeldaCantidadEditor() {
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            configurarBoton(btnMenos, -1);
            configurarBoton(btnMas, 1);
            lblCant.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblCant.setPreferredSize(new Dimension(24, 24));
            panel.add(btnMenos);
            panel.add(lblCant);
            panel.add(btnMas);
        }

        private void configurarBoton(JButton btn, int dir) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setBackground(Estilo.FONDO_VENTA);
            btn.setForeground(Estilo.TEXTO_VENTA);
            btn.setPreferredSize(new Dimension(26, 26));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true));
            btn.addActionListener(e -> {
                int fila = filaActual;
                fireEditingCanceled();
                cambiarCantidad(fila, dir);
            });
        }

        @Override public Object getCellEditorValue() { return Integer.valueOf(lblCant.getText()); }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            this.filaActual = table.convertRowIndexToModel(row);
            panel.setBackground(isSelected ? Estilo.FONDO_DESCUENTO : Color.WHITE);
            lblCant.setText(String.valueOf(value));
            return panel;
        }
    }

    private class CeldaProductoRenderer extends DefaultTableCellRenderer {
        private final JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
        private final JLabel lblNom = new JLabel();
        private final JLabel lblSku = new JLabel();

        public CeldaProductoRenderer() {
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(8, 10, 8, 10));

            lblNom.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblNom.setForeground(Estilo.TEXTO_OSCURO);

            lblSku.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblSku.setForeground(Estilo.TEXTO_DESCUENTO);

            panel.add(lblNom);
            panel.add(lblSku);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            panel.setBackground(isSelected ? Estilo.FONDO_DESCUENTO : Color.WHITE);
            if (value instanceof String[] arr) {
                lblNom.setText(arr[0]);
                lblSku.setText(arr[1]);
            }
            return panel;
        }
    }

    private class CeldaTallaRenderer extends DefaultTableCellRenderer {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private final JLabel badge = new JLabel("", SwingConstants.CENTER);

        public CeldaTallaRenderer() {
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            badge.setOpaque(true);
            badge.setBackground(Estilo.FONDO_TALLA);
            badge.setForeground(Estilo.TEXTO_TALLA);
            badge.setFont(new Font("SansSerif", Font.BOLD, 11));
            badge.setPreferredSize(new Dimension(44, 24));
            badge.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_DESCUENTO, 1, true));
            panel.add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            panel.setBackground(isSelected ? Estilo.FONDO_DESCUENTO : Color.WHITE);
            badge.setText(value != null ? value.toString() : "-");
            return panel;
        }
    }

    private class CeldaEliminarRenderer extends DefaultTableCellRenderer {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private final JLabel lblCruz = new JLabel("✕", SwingConstants.CENTER);

        public CeldaEliminarRenderer() {
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            lblCruz.setOpaque(true);
            lblCruz.setBackground(Estilo.FONDO_CANCELAR);
            lblCruz.setForeground(Estilo.ERROR);
            lblCruz.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblCruz.setPreferredSize(new Dimension(24, 24));
            panel.add(lblCruz);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            return panel;
        }
    }

    private class CeldaEliminarEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private final JButton btnEliminar = new JButton("✕");
        private int filaActual;

        public CeldaEliminarEditor() {
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            btnEliminar.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnEliminar.setBackground(Estilo.FONDO_CANCELAR);
            btnEliminar.setForeground(Estilo.ERROR);
            btnEliminar.setPreferredSize(new Dimension(24, 24));
            btnEliminar.setFocusPainted(false);
            btnEliminar.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_CANCELAR, 1, true));
            btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEliminar.addActionListener(e -> {
                int fila = filaActual;
                fireEditingCanceled();
                eliminarFila(fila);

            });
            panel.add(btnEliminar);
        }

        @Override public Object getCellEditorValue() { return "✕"; }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            this.filaActual = table.convertRowIndexToModel(row);
            return panel;
        }
    }
}