package vistas;

import controles.ControlGestionarInventario;
import controles.ControlNavegacion;
import dtos.CategoriaDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import utilerias.Estilo;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Pantalla principal del caso de uso Gestionar Inventario.
 * Proporciona al encargado del punto de venta la visualización global del catálogo,
 * indicadores de existencias, filtros rápidos y herramientas para modificar el stock y productos.
 */
public final class PanelInventario extends JPanel {

    private final ControlGestionarInventario control;
    private final ControlNavegacion navegacion;

    // Métricas / KPIs
    private final JLabel lblTotalProductos = new JLabel("0");
    private final JLabel lblTotalUnidades = new JLabel("0");
    private final JLabel lblStockBajo = new JLabel("0");
    private final JLabel lblAgotados = new JLabel("0");

    // Filtros
    private final JTextField txtBuscar = new JTextField();
    private final JComboBox<String> comboCategorias = new JComboBox<>();
    private final JComboBox<String> comboEstadoStock = new JComboBox<>(new String[]{
            "Todos los estados",
            "Con existencias (> 5)",
            "Stock bajo (1 a 5)",
            "Agotados (0)"
    });

    // Tabla principal
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"SKU", "CÓDIGO", "PRODUCTO", "TIPO PRENDA", "PRECIO", "TALLAS Y STOCK", "TOTAL", "ESTADO"}, 0) {
        @Override public boolean isCellEditable(int fila, int col) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    // Detalle lateral
    private final JPanel panelDetalle = new JPanel();
    private final JLabel lblDetalleTitulo = new JLabel("Selecciona un producto");
    private final JLabel lblDetalleSub = new JLabel("SKU: - · Código: -");
    private final JLabel lblDetallePrecio = new JLabel("$0.00");
    private final JLabel lblDetalleStockTotal = new JLabel("0 unidades");
    private final JPanel panelListaTallas = new JPanel();

    // Botones laterales con renderizado propio para garantizar contraste absoluto
    private final JButton btnModificarStock = Estilo.botonPersonalizado(
            "Modificar Stock / Reabastecer", Estilo.VERDE_COBRO, Estilo.VERDE_OSCURO, Color.WHITE);
    private final JButton btnAgregarTalla = Estilo.botonPersonalizado(
            "Agregar Talla", new Color(238, 242, 255), Estilo.BORDE_EXACTO, Estilo.TEXTO_EXACTO);
    private final JButton btnEditarProducto = Estilo.botonPersonalizado(
            "Editar Datos", new Color(241, 245, 249), new Color(203, 213, 225), Estilo.TEXTO_OSCURO);
    private final JButton btnEliminarProducto = Estilo.botonPersonalizado(
            "Eliminar Producto", new Color(254, 226, 226), new Color(252, 165, 165), new Color(185, 28, 28));

    private final JLabel lblEstadoBarra = new JLabel("Cargando inventario...");

    // Caché local para renderizado rápido
    private final List<ProductoDTO> productosTodos = new ArrayList<>();
    private final Map<Long, List<ProductoTallaDTO>> tallasPorProducto = new HashMap<>();
    private final Map<Long, Integer> stockTotalPorProducto = new HashMap<>();
    private final List<CategoriaDTO> categorias = new ArrayList<>();

    private ProductoDTO productoSeleccionado;
    private final Timer debounce;

    public PanelInventario(ControlGestionarInventario control, ControlNavegacion navegacion) {
        super(new BorderLayout(0, 14));
        this.control = control;
        this.navegacion = navegacion;

        setBackground(Estilo.FONDO_VENTA);
        setBorder(new EmptyBorder(14, 20, 14, 20));

        // Debounce para búsqueda en vivo
        debounce = new Timer(300, e -> filtrarTabla());
        debounce.setRepeats(false);

        // Estructura general
        add(crearZonaSuperior(), BorderLayout.NORTH);
        add(crearZonaCentral(), BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);

        configurarTabla();
        configurarEventos();

        cargarDatosCompletos();
    }

    private JPanel crearZonaSuperior() {
        JPanel zona = new JPanel();
        zona.setLayout(new BoxLayout(zona, BoxLayout.Y_AXIS));
        zona.setOpaque(false);

        // Barra de título y botones principales
        JPanel barraTitulo = new JPanel(new BorderLayout(12, 0));
        barraTitulo.setOpaque(false);

        JPanel textosTitulo = new JPanel(new GridLayout(2, 1, 0, 2));
        textosTitulo.setOpaque(false);
        JLabel titulo = new JLabel("Gestión de Inventario y Catálogo");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Estilo.AZUL);

        JLabel sub = new JLabel("Consulta existencias, reabastece prendas, ajusta stock y actualiza productos.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(Estilo.TEXTO_DESCUENTO);
        textosTitulo.add(titulo);
        textosTitulo.add(sub);

        JPanel accionesTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        accionesTop.setOpaque(false);

        JButton btnActualizar = Estilo.botonPersonalizado(
                "Actualizar", Color.WHITE, Estilo.BORDE_TARJETA, Estilo.AZUL);
        btnActualizar.addActionListener(e -> cargarDatosCompletos());

        JButton btnNuevo = Estilo.botonPersonalizado(
                "+ Registrar Producto", Estilo.VERDE_COBRO, Estilo.VERDE_OSCURO, Color.WHITE);
        btnNuevo.addActionListener(e -> abrirModalNuevoProducto());
        accionesTop.add(btnActualizar);
        accionesTop.add(btnNuevo);

        barraTitulo.add(textosTitulo, BorderLayout.CENTER);
        barraTitulo.add(accionesTop, BorderLayout.EAST);
        zona.add(barraTitulo);
        zona.add(Box.createVerticalStrut(14));

        // Fila de tarjetas métricas / KPIs
        JPanel filaKpis = new JPanel(new GridLayout(1, 4, 14, 0));
        filaKpis.setOpaque(false);
        filaKpis.setMaximumSize(new Dimension(Short.MAX_VALUE, 75));

        filaKpis.add(crearCardKpi("Total Productos", lblTotalProductos, "En catálogo", Estilo.AZUL));
        filaKpis.add(crearCardKpi("Total Unidades", lblTotalUnidades, "Existencia física", Estilo.CABECERA_COBRO));
        filaKpis.add(crearCardKpi("Stock Bajo (<= 5)", lblStockBajo, "Requiere reabastecer", new Color(194, 65, 12)));
        filaKpis.add(crearCardKpi("Agotados", lblAgotados, "Sin existencias", Estilo.ERROR));

        zona.add(filaKpis);
        zona.add(Box.createVerticalStrut(14));

        // Barra de búsqueda y filtros
        JPanel barraFiltros = new JPanel(new BorderLayout(12, 0));
        barraFiltros.setBackground(Color.WHITE);
        barraFiltros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        barraFiltros.setMaximumSize(new Dimension(Short.MAX_VALUE, 54));

        // Buscador
        JPanel panelSearch = new JPanel(new BorderLayout(8, 0));
        panelSearch.setOpaque(false);
        JLabel lblRotuloBuscar = new JLabel("Buscar:");
        lblRotuloBuscar.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRotuloBuscar.setForeground(Estilo.TEXTO_VENTA);

        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtBuscar.setBackground(Color.WHITE);
        txtBuscar.setForeground(Estilo.TEXTO_OSCURO);
        txtBuscar.setCaretColor(Estilo.TEXTO_OSCURO);
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Escribe nombre, código de barras o SKU...");

        panelSearch.add(lblRotuloBuscar, BorderLayout.WEST);
        panelSearch.add(txtBuscar, BorderLayout.CENTER);

        // Combos
        JPanel panelCombos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelCombos.setOpaque(false);

        JLabel lblFiltroCat = new JLabel("Categoría:");
        lblFiltroCat.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblFiltroCat.setForeground(Estilo.TEXTO_DESCUENTO);

        comboCategorias.setPreferredSize(new Dimension(160, 32));
        comboCategorias.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboCategorias.setBackground(Color.WHITE);
        comboCategorias.setForeground(Estilo.TEXTO_OSCURO);

        JLabel lblFiltroEst = new JLabel("Estado:");
        lblFiltroEst.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblFiltroEst.setForeground(Estilo.TEXTO_DESCUENTO);

        comboEstadoStock.setPreferredSize(new Dimension(170, 32));
        comboEstadoStock.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboEstadoStock.setBackground(Color.WHITE);
        comboEstadoStock.setForeground(Estilo.TEXTO_OSCURO);

        panelCombos.add(lblFiltroCat);
        panelCombos.add(comboCategorias);
        panelCombos.add(lblFiltroEst);
        panelCombos.add(comboEstadoStock);

        barraFiltros.add(panelSearch, BorderLayout.CENTER);
        barraFiltros.add(panelCombos, BorderLayout.EAST);
        zona.add(barraFiltros);

        for (Component c : zona.getComponents()) {
            if (c instanceof JComponent jc) jc.setAlignmentX(LEFT_ALIGNMENT);
        }

        return zona;
    }

    private JPanel crearCardKpi(String titulo, JLabel valor, String pie, Color colorValor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTit.setForeground(Estilo.TEXTO_DESCUENTO);

        valor.setFont(new Font("SansSerif", Font.BOLD, 22));
        valor.setForeground(colorValor);

        JLabel lblPie = new JLabel(pie);
        lblPie.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblPie.setForeground(Estilo.TEXTO_SECUNDARIO);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);
        card.add(lblPie, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearZonaCentral() {
        JPanel zona = new JPanel(new BorderLayout(14, 0));
        zona.setOpaque(false);

        // Tabla en ScrollPane
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true));
        zona.add(scroll, BorderLayout.CENTER);

        // Panel lateral de detalle y acciones
        zona.add(crearPanelLateralDetalle(), BorderLayout.EAST);
        return zona;
    }

    private JPanel crearPanelLateralDetalle() {
        panelDetalle.setLayout(new BoxLayout(panelDetalle, BoxLayout.Y_AXIS));
        panelDetalle.setBackground(Color.WHITE);
        panelDetalle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        panelDetalle.setPreferredSize(new Dimension(320, 0));

        JLabel titSeccion = new JLabel("DETALLE DE ARTÍCULO");
        titSeccion.setFont(new Font("SansSerif", Font.BOLD, 11));
        titSeccion.setForeground(Estilo.TEXTO_DESCUENTO);
        panelDetalle.add(titSeccion);
        panelDetalle.add(Box.createVerticalStrut(10));

        lblDetalleTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblDetalleTitulo.setForeground(Estilo.AZUL);
        panelDetalle.add(lblDetalleTitulo);
        panelDetalle.add(Box.createVerticalStrut(2));

        lblDetalleSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDetalleSub.setForeground(Estilo.TEXTO_DESCUENTO);
        panelDetalle.add(lblDetalleSub);
        panelDetalle.add(Box.createVerticalStrut(12));

        // Precio y Stock Total
        JPanel filaInfo = new JPanel(new GridLayout(1, 2, 8, 0));
        filaInfo.setOpaque(false);
        filaInfo.setMaximumSize(new Dimension(Short.MAX_VALUE, 48));

        JPanel colPrecio = new JPanel(new BorderLayout());
        colPrecio.setOpaque(false);
        JLabel lblTP = new JLabel("Precio unitario:");
        lblTP.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTP.setForeground(Estilo.TEXTO_SECUNDARIO);
        lblDetallePrecio.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblDetallePrecio.setForeground(Estilo.CABECERA_COBRO);
        colPrecio.add(lblTP, BorderLayout.NORTH);
        colPrecio.add(lblDetallePrecio, BorderLayout.CENTER);

        JPanel colStock = new JPanel(new BorderLayout());
        colStock.setOpaque(false);
        JLabel lblTS = new JLabel("Stock total:");
        lblTS.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTS.setForeground(Estilo.TEXTO_SECUNDARIO);
        lblDetalleStockTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblDetalleStockTotal.setForeground(Estilo.VERDE_COBRO);
        colStock.add(lblTS, BorderLayout.NORTH);
        colStock.add(lblDetalleStockTotal, BorderLayout.CENTER);

        filaInfo.add(colPrecio);
        filaInfo.add(colStock);
        panelDetalle.add(filaInfo);
        panelDetalle.add(Box.createVerticalStrut(14));

        JSeparator sep = new JSeparator();
        sep.setForeground(Estilo.BORDE_TARJETA);
        sep.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        panelDetalle.add(sep);
        panelDetalle.add(Box.createVerticalStrut(12));

        // Tallas registradas
        JLabel lblTitTallas = new JLabel("DESGLOSE POR TALLAS");
        lblTitTallas.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitTallas.setForeground(Estilo.TEXTO_DESCUENTO);
        panelDetalle.add(lblTitTallas);
        panelDetalle.add(Box.createVerticalStrut(6));

        panelListaTallas.setLayout(new BoxLayout(panelListaTallas, BoxLayout.Y_AXIS));
        panelListaTallas.setOpaque(false);
        JScrollPane scrollTallas = new JScrollPane(panelListaTallas);
        scrollTallas.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1));
        scrollTallas.getViewport().setBackground(Color.WHITE);
        scrollTallas.setPreferredSize(new Dimension(Short.MAX_VALUE, 150));
        panelDetalle.add(scrollTallas);
        panelDetalle.add(Box.createVerticalStrut(14));

        // Listeners de los botones de acción
        btnModificarStock.addActionListener(e -> abrirModalAjusteStock(null));
        btnAgregarTalla.addActionListener(e -> abrirModalNuevaTalla());
        btnEditarProducto.addActionListener(e -> abrirModalEditarProducto());
        btnEliminarProducto.addActionListener(e -> confirmarEliminarProducto());

        deshabilitarAccionesDetalle();

        panelDetalle.add(btnModificarStock);
        panelDetalle.add(Box.createVerticalStrut(8));
        panelDetalle.add(btnAgregarTalla);
        panelDetalle.add(Box.createVerticalStrut(8));
        panelDetalle.add(btnEditarProducto);
        panelDetalle.add(Box.createVerticalStrut(8));
        panelDetalle.add(btnEliminarProducto);

        for (Component c : panelDetalle.getComponents()) {
            if (c instanceof JComponent jc) jc.setAlignmentX(LEFT_ALIGNMENT);
        }

        return panelDetalle;
    }

    private JPanel crearBarraInferior() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 4, 4, 4));

        lblEstadoBarra.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblEstadoBarra.setForeground(Estilo.TEXTO_DESCUENTO);
        footer.add(lblEstadoBarra, BorderLayout.WEST);

        JLabel hints = new JLabel("Doble clic en un producto para modificar existencias · F5 para actualizar");
        hints.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hints.setForeground(Estilo.TEXTO_DESCUENTO);
        footer.add(hints, BorderLayout.EAST);

        return footer;
    }

    private void configurarTabla() {
        tabla.setRowHeight(42);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(Estilo.BORDE_TARJETA);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        tabla.getTableHeader().setBackground(Estilo.FONDO_VENTA);
        tabla.getTableHeader().setForeground(Estilo.TEXTO_DESCUENTO);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Anchos de columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(90);  // SKU
        tabla.getColumnModel().getColumn(1).setPreferredWidth(110); // Código
        tabla.getColumnModel().getColumn(2).setPreferredWidth(210); // Producto
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100); // Tipo
        tabla.getColumnModel().getColumn(4).setPreferredWidth(85);  // Precio
        tabla.getColumnModel().getColumn(5).setPreferredWidth(200); // Tallas y stock
        tabla.getColumnModel().getColumn(6).setPreferredWidth(70);  // Total
        tabla.getColumnModel().getColumn(7).setPreferredWidth(105); // Estado

        // Renderers personalizados
        tabla.getColumnModel().getColumn(5).setCellRenderer(new CeldaTallasRenderer());
        tabla.getColumnModel().getColumn(7).setCellRenderer(new CeldaEstadoRenderer());
    }

    private void configurarEventos() {
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { debounce.restart(); }
            @Override public void removeUpdate(DocumentEvent e) { debounce.restart(); }
            @Override public void changedUpdate(DocumentEvent e) { debounce.restart(); }
        });

        comboCategorias.addActionListener(e -> filtrarTabla());
        comboEstadoStock.addActionListener(e -> filtrarTabla());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    int modelRow = tabla.convertRowIndexToModel(fila);
                    String sku = (String) modeloTabla.getValueAt(modelRow, 0);
                    seleccionarProductoPorSku(sku);
                } else {
                    deshabilitarAccionesDetalle();
                }
            }
        });

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && productoSeleccionado != null) {
                    abrirModalAjusteStock(null);
                }
            }
        });
    }

    public void cargarDatosCompletos() {
        lblEstadoBarra.setText("Actualizando catálogo y existencias...");
        navegacion.ejecutar(() -> {
            List<ProductoDTO> productos = control.cargarInventario();
            List<CategoriaDTO> cats = control.obtenerCategorias();
            Map<Long, List<ProductoTallaDTO>> tallas = new HashMap<>();
            Map<Long, Integer> totales = new HashMap<>();

            for (ProductoDTO p : productos) {
                if (p.getId() != null) {
                    List<ProductoTallaDTO> pt = control.obtenerTallas(p.getId().intValue());
                    tallas.put(p.getId(), pt);
                    int tot = 0;
                    for (ProductoTallaDTO t : pt) tot += t.getStockActual();
                    totales.put(p.getId(), tot);
                }
            }
            return new Object[]{productos, cats, tallas, totales};
        }, resultado -> {
            Object[] data = (Object[]) resultado;
            productosTodos.clear();
            productosTodos.addAll((List<ProductoDTO>) data[0]);

            categorias.clear();
            categorias.addAll((List<CategoriaDTO>) data[1]);

            tallasPorProducto.clear();
            tallasPorProducto.putAll((Map<Long, List<ProductoTallaDTO>>) data[2]);

            stockTotalPorProducto.clear();
            stockTotalPorProducto.putAll((Map<Long, Integer>) data[3]);

            actualizarComboCategorias();
            actualizarMetricas();
            filtrarTabla();
            lblEstadoBarra.setText("Inventario actualizado. " + productosTodos.size() + " productos en tienda.");
        });
    }

    private void actualizarComboCategorias() {
        comboCategorias.removeAllItems();
        comboCategorias.addItem("Todas las categorías");
        for (CategoriaDTO c : categorias) {
            comboCategorias.addItem(c.getNombre());
        }
    }

    private void actualizarMetricas() {
        int totalProds = productosTodos.size();
        int totalUnits = 0;
        int bajo = 0;
        int agotado = 0;

        for (ProductoDTO p : productosTodos) {
            int tot = stockTotalPorProducto.getOrDefault(p.getId(), 0);
            totalUnits += tot;
            if (tot == 0) agotado++;
            else if (tot <= 5) bajo++;
        }

        lblTotalProductos.setText(String.valueOf(totalProds));
        lblTotalUnidades.setText(String.valueOf(totalUnits));
        lblStockBajo.setText(String.valueOf(bajo));
        lblAgotados.setText(String.valueOf(agotado));
    }

    private void filtrarTabla() {
        String criterio = txtBuscar.getText().trim().toLowerCase();
        String catSel = (String) comboCategorias.getSelectedItem();
        String estadoSel = (String) comboEstadoStock.getSelectedItem();

        modeloTabla.setRowCount(0);
        int contador = 0;

        for (ProductoDTO p : productosTodos) {
            // Filtro por texto (SKU, código o nombre)
            boolean coincideTexto = criterio.isEmpty()
                    || (p.getNombre() != null && p.getNombre().toLowerCase().contains(criterio))
                    || (p.getSku() != null && p.getSku().toLowerCase().contains(criterio))
                    || (p.getCodigoBarras() != null && p.getCodigoBarras().toLowerCase().contains(criterio));

            if (!coincideTexto) continue;

            // Filtro por categoría
            if (catSel != null && !catSel.equals("Todas las categorías")) {
                boolean tieneCat = false;
                for (CategoriaDTO c : categorias) {
                    if (c.getNombre().equalsIgnoreCase(catSel) && p.getCategoriasIds() != null
                            && p.getCategoriasIds().contains(c.getId())) {
                        tieneCat = true;
                        break;
                    }
                }
                if (!tieneCat) continue;
            }

            // Filtro por estado de stock
            int stockTotal = stockTotalPorProducto.getOrDefault(p.getId(), 0);
            if ("Con existencias (> 5)".equals(estadoSel) && stockTotal <= 5) continue;
            if ("Stock bajo (1 a 5)".equals(estadoSel) && (stockTotal <= 0 || stockTotal > 5)) continue;
            if ("Agotados (0)".equals(estadoSel) && stockTotal > 0) continue;

            // Formatear tallas
            List<ProductoTallaDTO> tallas = tallasPorProducto.getOrDefault(p.getId(), Collections.emptyList());

            String estadoStr;
            if (stockTotal == 0) estadoStr = "AGOTADO";
            else if (stockTotal <= 5) estadoStr = "BAJO";
            else estadoStr = "EN STOCK";

            modeloTabla.addRow(new Object[]{
                    p.getSku(),
                    p.getCodigoBarras(),
                    p.getNombre(),
                    p.getTipoPrenda() != null ? p.getTipoPrenda().name() : "-",
                    Estilo.dinero(p.getPrecio()),
                    tallas,
                    stockTotal,
                    estadoStr
            });
            contador++;
        }

        lblEstadoBarra.setText("Mostrando " + contador + " de " + productosTodos.size() + " productos.");
    }

    private void seleccionarProductoPorSku(String sku) {
        productoSeleccionado = null;
        for (ProductoDTO p : productosTodos) {
            if (p.getSku() != null && p.getSku().equalsIgnoreCase(sku)) {
                productoSeleccionado = p;
                break;
            }
        }

        if (productoSeleccionado == null) {
            deshabilitarAccionesDetalle();
            return;
        }

        lblDetalleTitulo.setText(productoSeleccionado.getNombre());
        lblDetalleSub.setText("SKU: " + productoSeleccionado.getSku() + " · Cód: " + productoSeleccionado.getCodigoBarras());
        lblDetallePrecio.setText(Estilo.dinero(productoSeleccionado.getPrecio()));

        int tot = stockTotalPorProducto.getOrDefault(productoSeleccionado.getId(), 0);
        lblDetalleStockTotal.setText(tot + " unidades");
        lblDetalleStockTotal.setForeground(tot == 0 ? Estilo.ERROR : (tot <= 5 ? new Color(194, 65, 12) : Estilo.VERDE_COBRO));

        // Cargar panel de tallas con botones de modificación rápida
        panelListaTallas.removeAll();
        List<ProductoTallaDTO> tallas = tallasPorProducto.getOrDefault(productoSeleccionado.getId(), Collections.emptyList());

        if (tallas.isEmpty()) {
            JLabel lblSin = new JLabel("Sin tallas registradas");
            lblSin.setFont(new Font("SansSerif", Font.ITALIC, 12));
            lblSin.setForeground(Estilo.TEXTO_SECUNDARIO);
            panelListaTallas.add(lblSin);
        } else {
            for (ProductoTallaDTO t : tallas) {
                JPanel item = new JPanel(new BorderLayout(6, 0));
                item.setOpaque(false);
                item.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
                item.setBorder(new EmptyBorder(3, 4, 3, 4));

                JLabel lblT = new JLabel("Talla " + t.getTalla());
                lblT.setFont(new Font("SansSerif", Font.BOLD, 12));
                lblT.setForeground(Estilo.TEXTO_OSCURO);

                JLabel lblS = new JLabel(t.getStockActual() + " pzas");
                lblS.setFont(new Font("SansSerif", Font.BOLD, 12));
                lblS.setForeground(t.getStockActual() == 0 ? Estilo.ERROR : (t.getStockActual() <= 5 ? new Color(194, 65, 12) : Estilo.TEXTO_VENTA));

                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
                btns.setOpaque(false);

                JButton btnAjusteRapido = Estilo.botonPersonalizado(
                        "Modificar", new Color(241, 245, 249), new Color(203, 213, 225), Estilo.TEXTO_OSCURO);
                btnAjusteRapido.setFont(new Font("SansSerif", Font.BOLD, 11));
                btnAjusteRapido.setBorder(new EmptyBorder(4, 10, 4, 10));
                btnAjusteRapido.addActionListener(e -> abrirModalAjusteStock(t));

                btns.add(lblS);
                btns.add(btnAjusteRapido);

                item.add(lblT, BorderLayout.WEST);
                item.add(btns, BorderLayout.EAST);
                panelListaTallas.add(item);
                panelListaTallas.add(new JSeparator(SwingConstants.HORIZONTAL));
            }
        }

        panelListaTallas.revalidate();
        panelListaTallas.repaint();

        btnModificarStock.setEnabled(true);
        btnAgregarTalla.setEnabled(true);
        btnEditarProducto.setEnabled(true);
        btnEliminarProducto.setEnabled(true);
    }

    private void deshabilitarAccionesDetalle() {
        productoSeleccionado = null;
        lblDetalleTitulo.setText("Selecciona un producto");
        lblDetalleSub.setText("SKU: - · Código: -");
        lblDetallePrecio.setText("$0.00");
        lblDetalleStockTotal.setText("0 unidades");
        panelListaTallas.removeAll();
        panelListaTallas.revalidate();
        panelListaTallas.repaint();

        btnModificarStock.setEnabled(false);
        btnAgregarTalla.setEnabled(false);
        btnEditarProducto.setEnabled(false);
        btnEliminarProducto.setEnabled(false);
    }

    private void abrirModalAjusteStock(ProductoTallaDTO tallaPreseleccionada) {
        if (productoSeleccionado == null) return;
        List<ProductoTallaDTO> tallas = tallasPorProducto.getOrDefault(productoSeleccionado.getId(), Collections.emptyList());
        if (tallas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El producto no tiene tallas asignadas. Agrega una talla primero.",
                    "Sin Tallas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PanelModalAjusteStock modal = new PanelModalAjusteStock(
                productoSeleccionado, tallas, tallaPreseleccionada, control, navegacion, this::cargarDatosCompletos);
        navegacion.mostrarModal("Modificar Existencias de Inventario", modal, 560, 640);
    }

    private void abrirModalNuevaTalla() {
        if (productoSeleccionado == null) return;
        PanelModalNuevaTalla modal = new PanelModalNuevaTalla(
                productoSeleccionado, control, navegacion, this::cargarDatosCompletos);
        navegacion.mostrarModal("Agregar Nueva Talla", modal, 480, 380);
    }
     private void abrirModalNuevoProducto() {
        PanelModalEditarProducto modal = new PanelModalEditarProducto(
                null, control, navegacion, this::cargarDatosCompletos);
        navegacion.mostrarModal("Registrar Nuevo Producto", modal, 620, 720);
     }
    

    private void abrirModalEditarProducto() {
        if (productoSeleccionado == null) return;
        PanelModalEditarProducto modal = new PanelModalEditarProducto(
                productoSeleccionado, control, navegacion, this::cargarDatosCompletos);
        navegacion.mostrarModal("Modificar Producto", modal, 620, 600);
    }

    private void confirmarEliminarProducto() {
        if (productoSeleccionado == null) return;

        int op = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar el producto '" + productoSeleccionado.getNombre() + "'?\n" +
                        "Esta acción solo procederá si el producto no tiene ventas registradas.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (op == JOptionPane.YES_OPTION) {
            navegacion.ejecutar(() -> {
                control.eliminarProducto(productoSeleccionado.getId());
                return true;
            }, exito -> {
                cargarDatosCompletos();
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente del catálogo.",
                        "Producto Eliminado", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }

    // ==========================================
    // RENDERERS DE CELDA PARA LA TABLA
    // ==========================================

    private static class CeldaTallasRenderer extends DefaultTableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 6));

        public CeldaTallasRenderer() {
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            panel.removeAll();
            panel.setBackground(isSelected ? Estilo.FONDO_DESCUENTO : Color.WHITE);

            if (value instanceof List<?> lista) {
                for (Object item : lista) {
                    if (item instanceof ProductoTallaDTO t) {
                        JLabel pill = new JLabel(t.getTalla() + " (" + t.getStockActual() + ")");
                        pill.setFont(new Font("SansSerif", Font.BOLD, 11));
                        pill.setOpaque(true);

                        if (t.getStockActual() == 0) {
                            pill.setBackground(Estilo.FONDO_CANCELAR);
                            pill.setForeground(Estilo.ERROR);
                        } else if (t.getStockActual() <= 5) {
                            pill.setBackground(new Color(254, 243, 199));
                            pill.setForeground(new Color(180, 83, 9));
                        } else {
                            pill.setBackground(Estilo.FONDO_TALLA);
                            pill.setForeground(Estilo.TEXTO_TALLA);
                        }
                        pill.setBorder(new EmptyBorder(2, 6, 2, 6));
                        panel.add(pill);
                    }
                }
            }
            return panel;
        }
    }

    private static class CeldaEstadoRenderer extends DefaultTableCellRenderer {
        private final JLabel badge = new JLabel("", SwingConstants.CENTER);
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));

        public CeldaEstadoRenderer() {
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            badge.setOpaque(true);
            badge.setFont(new Font("SansSerif", Font.BOLD, 10));
            badge.setPreferredSize(new Dimension(86, 24));
            panel.add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            panel.setBackground(isSelected ? Estilo.FONDO_DESCUENTO : Color.WHITE);
            String est = String.valueOf(value);
            if ("AGOTADO".equalsIgnoreCase(est)) {
                badge.setText("AGOTADO");
                badge.setBackground(Estilo.FONDO_CANCELAR);
                badge.setForeground(Estilo.ERROR);
            } else if ("BAJO".equalsIgnoreCase(est)) {
                badge.setText("STOCK BAJO");
                badge.setBackground(new Color(254, 243, 199));
                badge.setForeground(new Color(180, 83, 9));
            } else {
                badge.setText("EN STOCK");
                badge.setBackground(Estilo.VERDE_SUAVE);
                badge.setForeground(Estilo.VERDE_OSCURO);
            }
            return panel;
        }
    }
}
