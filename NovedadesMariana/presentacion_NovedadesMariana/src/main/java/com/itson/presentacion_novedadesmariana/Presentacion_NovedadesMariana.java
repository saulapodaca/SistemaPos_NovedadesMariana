package com.itson.presentacion_novedadesmariana;

import com.itson.negocios_realizarventa.fachada.IRealizarVentaFacade;
import com.itson.negocios_realizarventa.fachada.RealizarVentaFacade;
import dtos.DetalleVentaDTO;
import dtos.PagoDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import dtos.ResumenVentaDTO;
import dtos.UsuarioDTO;
import dtos.VentaDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Presentacion_NovedadesMariana {

    private static final Color NAVY = new Color(15, 31, 67);
    private static final Color BLUE = new Color(38, 100, 202);
    private static final Color GREEN = new Color(18, 166, 78);
    private static final Color RED = new Color(207, 52, 65);
    private static final Color BG = new Color(242, 245, 250);
    private static final Color MUTED = new Color(117, 135, 166);
    private static final Color LINE = new Color(225, 231, 241);
    private static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private final IRealizarVentaFacade facade = new RealizarVentaFacade();
    private final List<DetalleVentaDTO> carrito = new ArrayList<>();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"CANT.", "PRODUCTO", "TALLA", "P. UNITARIO", "TOTAL", ""}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private JFrame frame;
    private JTable tabla;
    private JTextField busqueda;
    private JLabel subtotalLabel, ivaLabel, descuentoLabel, totalLabel, countLabel;
    private JLabel cajeroLabel, inicialesLabel;
    private UsuarioDTO cajero;
    private float descuentoPorcentaje;
    private boolean modoPreview;

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored) { }

        Presentacion_NovedadesMariana app = new Presentacion_NovedadesMariana();
        
        // 1. Inicializamos la ventana principal para tener el marco base
        app.mostrarVenta();

        // 2. Construimos datos de prueba para evitar NullPointerException
        List<DetalleVentaDTO> detalles = List.of(
            new DetalleVentaDTO(0, 0, 1, "Conjunto Pijama Unicornio", "2T", 1, 185.0f, 185.0f),
            new DetalleVentaDTO(0, 0, 2, "Vestido Floral Niña", "4A", 1, 220.0f, 220.0f)
        );

        PagoDTO pagoMock = new PagoDTO(0, 0, "EFECTIVO", 405.0f, 500.0f, 95.0f, LocalDateTime.now());
        VentaDTO ventaMock = new VentaDTO(1001, "NV-001001", LocalDateTime.now(), 405.0f, 64.8f, 469.8f, 1, detalles, List.of(pagoMock));
        ResumenVentaDTO resumenMock = new ResumenVentaDTO(405.0f, 64.8f, 0.0f, 469.8f);

        // 3. Mostramos el diálogo de éxito con los datos cargados
        app.mostrarExito(ventaMock, resumenMock, 95.0f);
    });
}

    private void mostrarPreview() {
        modoPreview = true;
        cajero = new UsuarioDTO(1L, "vista-previa", null, "Diana", "González", "", "EMPLEADO");
        carrito.add(new DetalleVentaDTO(0, 0, 1, "Conjunto Pijama Unicornio", "2T", 1, 185.0f, 185.0f));
        carrito.add(new DetalleVentaDTO(0, 0, 2, "Vestido Floral Niña", "4A", 1, 220.0f, 220.0f));
        carrito.add(new DetalleVentaDTO(0, 0, 3, "Diadema Lazo Rosa", "TA", 2, 45.0f, 90.0f));
        carrito.add(new DetalleVentaDTO(0, 0, 4, "Calcetines Antiderrapante x3", "12-18M", 1, 85.0f, 85.0f));
        mostrarVenta();
        refrescarCarrito();
        busqueda.setEnabled(false);
        frame.getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).remove(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
    }

    private void iniciar() {
        mostrarVenta();
    }

    private void mostrarVenta() {
        frame = new JFrame("Novedades Mariana · Punto de venta");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(980, 620));
        frame.setSize(1120, 720);
        frame.setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        root.add(crearHeader(), BorderLayout.NORTH);
        JPanel content = new JPanel(new BorderLayout(16, 0));
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        content.add(crearPanelCarrito(), BorderLayout.CENTER);
        content.add(crearResumen(), BorderLayout.EAST);
        root.add(content, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "cobrar");
        frame.getRootPane().getActionMap().put("cobrar", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cobrar();
            }
        });
        frame.setVisible(true);
        busqueda.requestFocusInWindow();
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Novedades Mariana");
        name.setForeground(Color.WHITE);
        name.setFont(BOLD);
        JLabel sub = new JLabel("Ropa Infantil & Accesorios");
        sub.setForeground(new Color(177, 207, 255));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        brand.add(name);
        brand.add(sub);
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.X_AXIS));
        JLabel online = new JLabel("●  En línea");
        online.setForeground(new Color(99, 255, 178));
        online.setFont(BODY);
        inicialesLabel = new JLabel("  " + iniciales(cajero) + "  ");
        inicialesLabel.setOpaque(true);
        inicialesLabel.setBackground(BLUE);
        inicialesLabel.setForeground(Color.WHITE);
        inicialesLabel.setFont(BOLD);
        cajeroLabel = new JLabel(etiquetaCajero());
        cajeroLabel.setForeground(Color.WHITE);
        cajeroLabel.setFont(BODY);
        JLabel date = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  ·  hh:mm a")));
        date.setForeground(new Color(205, 217, 240));
        date.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel terminal = new JLabel(" Terminal 01 ");
        terminal.setForeground(new Color(167, 201, 255));
        terminal.setBorder(BorderFactory.createLineBorder(new Color(54, 81, 126)));
        terminal.setFont(BODY);
        info.add(online);
        info.add(Box.createHorizontalStrut(20));
        info.add(inicialesLabel);
        info.add(Box.createHorizontalStrut(8));
        info.add(cajeroLabel);
        info.add(Box.createHorizontalStrut(22));
        info.add(date);
        info.add(Box.createHorizontalStrut(20));
        info.add(terminal);
        header.add(brand, BorderLayout.WEST);
        header.add(info, BorderLayout.EAST);
        return header;
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        busqueda = new JTextField();
        busqueda.setFont(BODY);
        busqueda.setToolTipText("Código de barras del producto");

        // Botón Buscar producto con fondo Azul y texto Blanco
        JButton buscar = button("Buscar producto", BLUE, Color.WHITE);
        searchRow.add(busqueda, BorderLayout.CENTER);
        searchRow.add(buscar, BorderLayout.EAST);
        JLabel note = new JLabel("  Escanea o escribe el código de barras y presiona Enter");
        note.setForeground(MUTED);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        top.add(searchRow);
        top.add(Box.createVerticalStrut(5));
        top.add(note);
        buscar.addActionListener(e -> buscarProducto());
        busqueda.addActionListener(e -> buscarProducto());

        tabla = new JTable(modelo);
        tabla.getTableHeader().setBackground(NAVY);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.setRowHeight(48);
        tabla.setFont(BODY);
        tabla.setForeground(NAVY);
        tabla.setBackground(Color.WHITE);
        tabla.setGridColor(new Color(239, 242, 248));
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(new Color(230, 239, 255));
        tabla.getTableHeader().setBackground(NAVY);
        tabla.getTableHeader().setForeground(new Color(201, 216, 243));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabla.getColumnModel().getColumn(0).setPreferredWidth(58);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(330);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(58);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(38);
        ((DefaultTableCellRenderer) tabla.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.LEFT);

        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(NAVY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setOpaque(true);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE));
                return label;
            }
        });
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(NAVY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
                return label;
            }
        });

        tabla.getColumnModel().getColumn(0).setPreferredWidth(58);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(330);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(58);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(38);

        // Botón Quitar artículo con fondo Rojo y texto Blanco
        JButton quitar = button("Quitar artículo", RED, Color.WHITE);
        quitar.addActionListener(e -> quitarSeleccionado());
        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        countLabel = new JLabel("  0 artículos en esta venta");
        countLabel.setForeground(MUTED);
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        actions.add(countLabel, BorderLayout.WEST);
        actions.add(quitar, BorderLayout.EAST);
        JPanel tableCard = cardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(tabla), BorderLayout.CENTER);
        tableCard.add(actions, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearResumen() {
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setPreferredSize(new Dimension(270, 10));
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        JPanel resumen = cardPanel();
        resumen.setLayout(new BoxLayout(resumen, BoxLayout.Y_AXIS));
        resumen.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        resumen.add(sectionTitle("RESUMEN DE VENTA"));
        resumen.add(Box.createVerticalStrut(17));
        subtotalLabel = valueRow(resumen, "Subtotal", "$0.00");
        ivaLabel = valueRow(resumen, "IVA (16%)", "$0.00");
        descuentoLabel = valueRow(resumen, "Descuento", "-$0.00");
        resumen.add(Box.createVerticalStrut(12));
        resumen.add(new javax.swing.JSeparator());
        resumen.add(Box.createVerticalStrut(12));
        JPanel total = new JPanel(new BorderLayout());
        total.setOpaque(false);
        JLabel t = new JLabel("Total");
        t.setFont(BOLD);
        t.setForeground(NAVY);
        totalLabel = new JLabel("$0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 27));
        totalLabel.setForeground(NAVY);
        total.add(t, BorderLayout.WEST);
        total.add(totalLabel, BorderLayout.EAST);
        resumen.add(total);
        JLabel currency = new JLabel("MXN · IVA incluido", SwingConstants.RIGHT);
        currency.setForeground(MUTED);
        currency.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        currency.setAlignmentX(Component.RIGHT_ALIGNMENT);
        resumen.add(currency);
        resumen.add(Box.createVerticalStrut(20));
        JPanel cliente = new JPanel(new BorderLayout());
        cliente.setBackground(new Color(239, 244, 255));
        cliente.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel customer = new JLabel("Cliente general");
        customer.setForeground(NAVY);
        customer.setFont(BOLD);
        cliente.add(customer, BorderLayout.CENTER);
        resumen.add(cliente);

        // Botones del resumen ajustados con fondo de color sólido y texto Blanco
        JButton cobrar = button("Cobrar      F12", GREEN, Color.WHITE);
        cobrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        cobrar.addActionListener(e -> cobrar());
        JButton descuento = button("Aplicar descuento", BLUE, Color.WHITE);
        descuento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        descuento.addActionListener(e -> aplicarDescuento());
        JButton cancelar = button("Cancelar venta", RED, Color.WHITE);
        cancelar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cancelar.addActionListener(e -> limpiarVenta());

        cobrar.setEnabled(!modoPreview);
        descuento.setEnabled(!modoPreview);
        cancelar.setEnabled(!modoPreview);
        JPanel status = cardPanel();
        status.setLayout(new BorderLayout());
        status.add(new JLabel(modoPreview ? "Vista previa · datos de ejemplo" : "●  Caja lista para vender", SwingConstants.CENTER), BorderLayout.CENTER);
        status.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        column.add(resumen);
        column.add(Box.createVerticalStrut(12));
        column.add(cobrar);
        column.add(Box.createVerticalStrut(8));
        column.add(descuento);
        column.add(Box.createVerticalStrut(8));
        column.add(cancelar);
        column.add(Box.createVerticalStrut(12));
        column.add(status);
        return column;
    }

    private void buscarProducto() {
        String codigo = busqueda.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }
        try {
            ProductoDTO producto = facade.buscarProductoPorCodigo(codigo);
            if (producto == null) {
                JOptionPane.showMessageDialog(frame, "No encontramos un producto con ese código de barras.", "Producto no encontrado", JOptionPane.INFORMATION_MESSAGE);
                busqueda.selectAll();
                busqueda.requestFocusInWindow();
                return;
            }
            if (producto.getId() == null) {
                return;
            }
            List<ProductoTallaDTO> tallas = facade.obtenerTallasPorProducto(producto.getId().intValue());
            if (tallas == null || tallas.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "El producto no tiene tallas registradas.", "Sin tallas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<ProductoTallaDTO> disponibles = new ArrayList<>();
            for (ProductoTallaDTO t : tallas) {
                if (t != null && t.getStockActual() > 0) {
                    disponibles.add(t);
                }
            }
            if (disponibles.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No hay existencias disponibles para este producto.", "Sin stock", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            ProductoTallaDTO talla = disponibles.get(0);
            if (disponibles.size() > 1) {
                JComboBox<ProductoTallaDTO> selector = new JComboBox<>(disponibles.toArray(ProductoTallaDTO[]::new));
                selector.setRenderer((list, value, index, selected, focus) -> new JLabel(value == null ? "" : value.getTalla() + "  ·  " + value.getStockActual() + " disponibles"));
                if (JOptionPane.showConfirmDialog(frame, selector, "Selecciona talla", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                    return;
                }
                talla = (ProductoTallaDTO) selector.getSelectedItem();
            }
            agregarAlCarrito(producto, talla);
            busqueda.setText("");
            busqueda.requestFocusInWindow();
        } catch (RuntimeException ex) {
            mostrarError(frame, ex);
        }
    }

    private void agregarAlCarrito(ProductoDTO p, ProductoTallaDTO talla) {
        for (DetalleVentaDTO d : carrito) {
            if (d.getIdProductoTalla() == talla.getId()) {
                if (!facade.validarStockDisponible(talla.getId(), d.getCantidad() + 1)) {
                    JOptionPane.showMessageDialog(frame, "No hay más unidades disponibles en esa talla.");
                    return;
                }
                d.setCantidad(d.getCantidad() + 1);
                d.setSubtotal(d.getCantidad() * d.getPrecioUnitario());
                refrescarCarrito();
                return;
            }
        }
        carrito.add(new DetalleVentaDTO(0, 0, talla.getId(), p.getNombre(), talla.getTalla(), 1, p.getPrecio(), p.getPrecio()));
        refrescarCarrito();
    }

    private void refrescarCarrito() {
        modelo.setRowCount(0);
        int unidades = 0;
        for (DetalleVentaDTO d : carrito) {
            unidades += d.getCantidad();
            modelo.addRow(new Object[]{"−  " + d.getCantidad() + "  +", d.getNombreProducto(), d.getTalla(), moneda(d.getPrecioUnitario()), moneda(d.getCantidad() * d.getPrecioUnitario()), "×"});
        }
        countLabel.setText("  " + unidades + " artículo" + (unidades == 1 ? "" : "s") + " en esta venta");
        if (carrito.isEmpty()) {
            subtotalLabel.setText("$0.00");
            ivaLabel.setText("$0.00");
            descuentoLabel.setText("-$0.00");
            totalLabel.setText("$0.00");
            return;
        }
        try {
            ResumenVentaDTO r = facade.calcularTotales(new ArrayList<>(carrito), descuentoPorcentaje);
            subtotalLabel.setText(moneda(r.getSubtotal()));
            ivaLabel.setText(moneda(r.getIva()));
            descuentoLabel.setText("-" + moneda(r.getDescuento()));
            totalLabel.setText(moneda(r.getTotal()));
        } catch (RuntimeException ex) {
            mostrarError(frame, ex);
        }
    }

    private void quitarSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            return;
        }
        carrito.remove(row);
        refrescarCarrito();
    }

    private void aplicarDescuento() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Agrega productos antes de aplicar un descuento.");
            return;
        }
        String v = JOptionPane.showInputDialog(frame, "Porcentaje de descuento (0 a 100):", String.valueOf(descuentoPorcentaje));
        if (v == null) {
            return;
        }
        try {
            float n = Float.parseFloat(v);
            if (n < 0 || n > 100) {
                throw new NumberFormatException();
            }
            descuentoPorcentaje = n;
            refrescarCarrito();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Escribe un porcentaje válido entre 0 y 100.");
        }
    }

    private void limpiarVenta() {
        if (carrito.isEmpty()) {
            return;
        }
        if (JOptionPane.showConfirmDialog(frame, "¿Cancelar y limpiar los artículos de esta venta?", "Cancelar venta", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        carrito.clear();
        descuentoPorcentaje = 0;
        refrescarCarrito();
    }

    private void cobrar() {
        if (modoPreview) {
            JOptionPane.showMessageDialog(frame, "Esta es una vista previa con datos de ejemplo.");
            return;
        }
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Agrega al menos un producto para cobrar.");
            return;
        }
        if (cajero == null && !seleccionarCajero()) {
            return;
        }
        try {
            ResumenVentaDTO r = facade.calcularTotales(new ArrayList<>(carrito), descuentoPorcentaje);
            JComboBox<String> metodo = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA", "TRANSFERENCIA"});
            JTextField recibido = new JTextField(monedaSinSigno(r.getTotal()), 12);
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(7, 7, 7, 7);
            g.anchor = GridBagConstraints.WEST;
            g.gridx = 0;
            g.gridy = 0;
            form.add(new JLabel("Total a cobrar"), g);
            g.gridx = 1;
            form.add(new JLabel(moneda(r.getTotal())), g);
            g.gridx = 0;
            g.gridy = 1;
            form.add(new JLabel("Método de pago"), g);
            g.gridx = 1;
            form.add(metodo, g);
            g.gridx = 0;
            g.gridy = 2;
            form.add(new JLabel("Recibido"), g);
            g.gridx = 1;
            form.add(recibido, g);
            if (JOptionPane.showConfirmDialog(frame, form, "Cobrar venta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }
            float recibidoNum = Float.parseFloat(recibido.getText().trim());
            String forma = (String) metodo.getSelectedItem();
            float cambio = forma.equals("EFECTIVO") ? facade.calcularCambio(r.getTotal(), recibidoNum) : 0;
            if (forma.equals("EFECTIVO") && recibidoNum < r.getTotal()) {
                JOptionPane.showMessageDialog(frame, "El monto recibido no cubre el total.");
                return;
            }
            if (!forma.equals("EFECTIVO")) {
                recibidoNum = r.getTotal();
            }
            PagoDTO pago = new PagoDTO(0, 0, forma, r.getTotal(), recibidoNum, cambio, LocalDateTime.now());
            VentaDTO venta = new VentaDTO(0, null, LocalDateTime.now(), r.getSubtotal(), r.getIva(), r.getTotal(), cajero.getId().intValue(), new ArrayList<>(carrito), List.of(pago));
            VentaDTO guardada = facade.registrarVenta(venta);
            if (guardada == null) {
                throw new IllegalStateException("No se recibió confirmación del registro.");
            }
            mostrarExito(guardada, r, cambio);
            carrito.clear();
            descuentoPorcentaje = 0;
            refrescarCarrito();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "El importe recibido no es válido.");
        } catch (RuntimeException ex) {
            mostrarError(frame, ex);
        }
    }

    private boolean seleccionarCajero() {
        String valor = JOptionPane.showInputDialog(frame, "Escribe el ID del cajero registrado para asociarlo a esta venta:", "Asignar cajero", JOptionPane.QUESTION_MESSAGE);
        if (valor == null) {
            return false;
        }
        try {
            int id = Integer.parseInt(valor.trim());
            if (id <= 0) {
                throw new NumberFormatException();
            }
            UsuarioDTO encontrado = facade.obtenerEmpleadoPorId(id);
            if (encontrado == null) {
                JOptionPane.showMessageDialog(frame, "No existe un cajero con ese ID.", "Cajero no encontrado", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            cajero = encontrado;
            cajeroLabel.setText(etiquetaCajero());
            inicialesLabel.setText("  " + iniciales(cajero) + "  ");
            return true;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "El ID debe ser un número entero válido.", "ID inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (RuntimeException ex) {
            mostrarError(frame, ex);
            return false;
        }
    }

    private void mostrarExito(VentaDTO venta, ResumenVentaDTO resumen, float cambio) {
        JDialog dialog = new JDialog(frame, "Venta registrada", true);
        dialog.setSize(760, 570);
        dialog.setLocationRelativeTo(frame);
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JPanel banner = new JPanel(new BorderLayout(15, 0));
        banner.setBackground(new Color(239, 252, 244));
        banner.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(118, 236, 165)), BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        JLabel check = new JLabel("✓", SwingConstants.CENTER);
        check.setOpaque(true);
        check.setBackground(GREEN);
        check.setForeground(Color.WHITE);
        check.setFont(new Font("Segoe UI", Font.BOLD, 22));
        banner.add(check, BorderLayout.WEST);
        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("¡Venta registrada con éxito!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(16, 112, 58));
        JLabel folio = new JLabel("Folio " + venta.getFolio() + " · Stock actualizado");
        folio.setForeground(GREEN);
        copy.add(title);
        copy.add(Box.createVerticalStrut(3));
        copy.add(folio);
        banner.add(copy, BorderLayout.CENTER);
        JLabel change = new JLabel("Cambio entregado  " + moneda(cambio), SwingConstants.CENTER);
        change.setForeground(new Color(13, 123, 60));
        change.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        banner.add(change, BorderLayout.EAST);
        root.add(banner, BorderLayout.NORTH);
        JPanel receipt = cardPanel();
        receipt.setLayout(new BorderLayout());
        JPanel receiptTitle = new JPanel(new BorderLayout());
        receiptTitle.setBackground(Color.WHITE);
        receiptTitle.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        receiptTitle.add(sectionTitle("VISTA PREVIA TICKET TÉRMICO"), BorderLayout.WEST);

        JButton print = button("Imprimir", BLUE, Color.WHITE);
        receiptTitle.add(print, BorderLayout.EAST);
        receipt.add(receiptTitle, BorderLayout.NORTH);
        StringBuilder txt = new StringBuilder("                  NOVEDADES MARIANA\n               Ropa Infantil & Accesorios\n\nFolio: " + venta.getFolio() + "\nFecha: " + venta.getFechaHora().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) + "\nCaja: " + nombreCajero() + "\n\nDETALLE DE VENTA\n");
        for (DetalleVentaDTO d : venta.getDetalles()) {
            txt.append(String.format("%-30s %2d x %8s   %9s%n", d.getNombreProducto() + " (" + d.getTalla() + ")", d.getCantidad(), monedaSinSigno(d.getPrecioUnitario()), moneda(d.getSubtotal())));
        }
        txt.append("\nSubtotal:                         ").append(moneda(resumen.getSubtotal())).append("\nIVA 16%:                          ").append(moneda(resumen.getIva())).append("\nTOTAL:                            ").append(moneda(resumen.getTotal())).append("\nEfectivo:                         ").append(moneda(venta.getPagos().get(0).getMontoRecibido())).append("\nCambio:                           ").append(moneda(cambio));
        javax.swing.JTextArea receiptText = new javax.swing.JTextArea(txt.toString());
        receiptText.setEditable(false);
        receiptText.setBackground(Color.WHITE);
        receiptText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        receiptText.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        receipt.add(new JScrollPane(receiptText), BorderLayout.CENTER);
        print.addActionListener(e -> {
            try {
                receiptText.print();
            } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(dialog, "No se pudo imprimir el ticket: " + ex.getMessage(), "Impresión", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton nueva = button("Nueva venta", GREEN, Color.WHITE);
        nueva.addActionListener(e -> dialog.dispose());
        root.add(nueva, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        return p;
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return l;
    }

    private JLabel valueRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 13, 0));
        JLabel l = new JLabel(label);
        l.setForeground(new Color(94, 117, 154));
        l.setFont(BODY);
        JLabel v = new JLabel(value, SwingConstants.RIGHT);
        v.setForeground(NAVY);
        v.setFont(BOLD);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        parent.add(row);
        return v;
    }

    private JButton button(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(BOLD);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);

        // Forzar a Swing a pintar el color de fondo personalizado bajo Look & Feel de Windows/Sistema
        b.setContentAreaFilled(false);
        b.setOpaque(true);

        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return b;
    }

    private String moneda(float value) {
        return String.format(java.util.Locale.forLanguageTag("es-MX"), "$%,.2f", value);
    }

    private String monedaSinSigno(float value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String nombreCajero() {
        return cajero == null ? "Cajero" : (cajero.getNombres() + " " + cajero.getPrimerApellido()).trim();
    }

    private String etiquetaCajero() {
        return cajero == null ? "Cajero · se asigna al cobrar" : nombreCajero() + "  ·  " + cajero.getRolEmpleado();
    }

    private String iniciales(UsuarioDTO u) {
        if (u == null) {
            return "CA";
        }
        String n = u.getNombres() == null ? "" : u.getNombres();
        String a = u.getPrimerApellido() == null ? "" : u.getPrimerApellido();
        return (n.isEmpty() ? "" : n.substring(0, 1)) + (a.isEmpty() ? "" : a.substring(0, 1));
    }

    private void mostrarError(Component parent, RuntimeException ex) {
        String mensaje = ex.getMessage() == null ? "Ocurrió un error inesperado." : ex.getMessage();
        JOptionPane.showMessageDialog(parent, mensaje, "No se pudo completar la operación", JOptionPane.ERROR_MESSAGE);
    }
}
