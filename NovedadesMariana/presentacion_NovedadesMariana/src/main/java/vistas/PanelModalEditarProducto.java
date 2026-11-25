package vistas;

import controles.ControlGestionarInventario;
import controles.ControlNavegacion;
import dtos.CategoriaDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import dtos.TipoPrendaDTO;
import utilerias.Estilo;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Diálogo modal para registrar un nuevo producto o modificar los datos de uno existente.
 */
public final class PanelModalEditarProducto extends JPanel {

    private final ProductoDTO productoOriginal;
    private final ControlGestionarInventario control;
    private final ControlNavegacion navegacion;
    private final Runnable alTerminar;
    private final boolean esNuevo;

    private final JTextField txtSku = new JTextField();
    private final JTextField txtCodigoBarras = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JComboBox<TipoPrendaDTO> comboTipoPrenda = new JComboBox<>(TipoPrendaDTO.values());
    private final JPanel panelCategorias = new JPanel(new GridLayout(0, 2, 8, 4));
    private final Map<Long, JCheckBox> checksCategorias = new LinkedHashMap<>();

    // Tallas iniciales solo al registrar nuevo producto
    private final DefaultTableModel modeloTallas = new DefaultTableModel(new String[]{"Talla", "Stock Inicial"}, 0);
    private final JTable tablaTallas = new JTable(modeloTallas);
    private final JButton btnGuardar = Estilo.boton("Guardar", true);
    private final JButton btnCancelar = Estilo.boton("Cancelar", false);

    public PanelModalEditarProducto(ProductoDTO producto,
                                    ControlGestionarInventario control,
                                    ControlNavegacion navegacion,
                                    Runnable alTerminar) {
        super(new BorderLayout());
        this.productoOriginal = producto;
        this.esNuevo = (producto == null || producto.getId() == null);
        this.control = control;
        this.navegacion = navegacion;
        this.alTerminar = alTerminar;

        btnGuardar.setText(esNuevo ? "Guardar Producto" : "Guardar Cambios");

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, esNuevo ? 720 : 620));
        add(crearCabecera(), BorderLayout.NORTH);
        add(new JScrollPane(crearFormulario()), BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);

        configurarCampos();
        cargarDatos();
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout(10, 4));
        cabecera.setBackground(Estilo.AZUL);
        cabecera.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel titulo = new JLabel(esNuevo ? "Registrar Nuevo Producto" : "Modificar Datos del Producto");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(esNuevo ?
                "Ingresa la información básica y las existencias iniciales." :
                "Actualiza el precio, descripción y clasificación del producto.");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(219, 234, 254));

        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(subtitulo, BorderLayout.SOUTH);
        return cabecera;
    }

    private void configurarCampos() {
        estilizarCampoTexto(txtSku);
        estilizarCampoTexto(txtCodigoBarras);
        estilizarCampoTexto(txtNombre);
        estilizarCampoTexto(txtDescripcion);
        estilizarCampoTexto(txtPrecio);

        comboTipoPrenda.setBackground(Color.WHITE);
        comboTipoPrenda.setForeground(Estilo.TEXTO_OSCURO);
        comboTipoPrenda.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private void estilizarCampoTexto(JTextField txt) {
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setBackground(Color.WHITE);
        txt.setForeground(Estilo.TEXTO_OSCURO);
        txt.setCaretColor(Estilo.TEXTO_OSCURO);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(18, 24, 18, 24));

        panel.add(etiqueta("SKU (Identificador único):"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtSku);
        panel.add(Box.createVerticalStrut(10));

        panel.add(etiqueta("Código de Barras:"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtCodigoBarras);
        panel.add(Box.createVerticalStrut(10));

        panel.add(etiqueta("Nombre del Producto:"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtNombre);
        panel.add(Box.createVerticalStrut(10));

        panel.add(etiqueta("Descripción:"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtDescripcion);
        panel.add(Box.createVerticalStrut(10));

        // Precio y Tipo de prenda en dos columnas
        JPanel filaDos = new JPanel(new GridLayout(1, 2, 12, 0));
        filaDos.setOpaque(false);

        JPanel colPrecio = new JPanel(new BorderLayout(0, 4));
        colPrecio.setOpaque(false);
        colPrecio.add(etiqueta("Precio Unitario ($ MXN):"), BorderLayout.NORTH);
        txtPrecio.setFont(new Font("SansSerif", Font.BOLD, 14));
        colPrecio.add(txtPrecio, BorderLayout.CENTER);

        JPanel colTipo = new JPanel(new BorderLayout(0, 4));
        colTipo.setOpaque(false);
        colTipo.add(etiqueta("Tipo de Prenda:"), BorderLayout.NORTH);
        colTipo.add(comboTipoPrenda, BorderLayout.CENTER);

        filaDos.add(colPrecio);
        filaDos.add(colTipo);
        panel.add(filaDos);
        panel.add(Box.createVerticalStrut(12));

        // Categorías asociadas
        panel.add(etiqueta("Categorías Asociadas:"));
        panel.add(Box.createVerticalStrut(6));
        panelCategorias.setOpaque(false);
        cargarChecksCategorias();
        panel.add(panelCategorias);
        panel.add(Box.createVerticalStrut(14));

        // Tallas iniciales solo si es nuevo producto
        if (esNuevo) {
            panel.add(etiqueta("Tallas y Existencias Iniciales:"));
            panel.add(Box.createVerticalStrut(6));

            modeloTallas.setRowCount(0);
            modeloTallas.addRow(new Object[]{"2T", 10});
            modeloTallas.addRow(new Object[]{"4T", 10});
            modeloTallas.addRow(new Object[]{"6T", 10});

            tablaTallas.setRowHeight(26);
            tablaTallas.setBackground(Color.WHITE);
            tablaTallas.setForeground(Estilo.TEXTO_OSCURO);
            tablaTallas.setFont(new Font("SansSerif", Font.PLAIN, 12));
            JScrollPane scrollTallas = new JScrollPane(tablaTallas);
            scrollTallas.setPreferredSize(new Dimension(0, 110));
            scrollTallas.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1));
            panel.add(scrollTallas);

            JPanel barraTallas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            barraTallas.setOpaque(false);
            JButton btnMasTalla = Estilo.botonPersonalizado(
                    "+ Agregar Fila", new Color(241, 245, 249), new Color(203, 213, 225), Estilo.TEXTO_OSCURO);
            btnMasTalla.setFont(new Font("SansSerif", Font.BOLD, 11));
            btnMasTalla.setBorder(new EmptyBorder(5, 12, 5, 12));
            btnMasTalla.addActionListener(e -> modeloTallas.addRow(new Object[]{"", 0}));

            JButton btnMenosTalla = Estilo.botonPersonalizado(
                    "Quitar Fila", new Color(254, 226, 226), new Color(252, 165, 165), new Color(185, 28, 28));
            btnMenosTalla.setFont(new Font("SansSerif", Font.BOLD, 11));
            btnMenosTalla.setBorder(new EmptyBorder(5, 12, 5, 12));
            btnMenosTalla.addActionListener(e -> {
                int sel = tablaTallas.getSelectedRow();
                if (sel >= 0) modeloTallas.removeRow(sel);
                else if (modeloTallas.getRowCount() > 0) modeloTallas.removeRow(modeloTallas.getRowCount() - 1);
            });

            barraTallas.add(btnMasTalla);
            barraTallas.add(btnMenosTalla);
            panel.add(barraTallas);
        }

        for (Component c : panel.getComponents()) {
            if (c instanceof JComponent jc) jc.setAlignmentX(LEFT_ALIGNMENT);
        }

        return panel;
    }

    private void cargarChecksCategorias() {
        panelCategorias.removeAll();
        checksCategorias.clear();
        try {
            List<CategoriaDTO> categorias = control.obtenerCategorias();
            for (CategoriaDTO cat : categorias) {
                JCheckBox cb = new JCheckBox(cat.getNombre());
                cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
                cb.setForeground(Estilo.TEXTO_OSCURO);
                cb.setOpaque(false);
                checksCategorias.put(cat.getId(), cb);
                panelCategorias.add(cb);
            }
        } catch (Exception ex) {
            panelCategorias.add(new JLabel("No se pudieron cargar las categorías."));
        }
    }

    private void cargarDatos() {
        if (!esNuevo && productoOriginal != null) {
            txtSku.setText(productoOriginal.getSku());
            txtCodigoBarras.setText(productoOriginal.getCodigoBarras());
            txtNombre.setText(productoOriginal.getNombre());
            txtDescripcion.setText(productoOriginal.getDescripcion());
            txtPrecio.setText(String.format(Locale.US, "%.2f", productoOriginal.getPrecio()));
            if (productoOriginal.getTipoPrenda() != null) {
                comboTipoPrenda.setSelectedItem(productoOriginal.getTipoPrenda());
            }
            if (productoOriginal.getCategoriasIds() != null) {
                for (Long catId : productoOriginal.getCategoriasIds()) {
                    if (checksCategorias.containsKey(catId)) {
                        checksCategorias.get(catId).setSelected(true);
                    }
                }
            }
        }
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

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(Estilo.TEXTO_VENTA);
        return l;
    }

    private void guardar() {
        String sku = txtSku.getText().trim();
        String cb = txtCodigoBarras.getText().trim();
        String nom = txtNombre.getText().trim();
        String desc = txtDescripcion.getText().trim();
        String precioStr = txtPrecio.getText().trim();

        if (sku.isEmpty() || cb.isEmpty() || nom.isEmpty() || desc.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos de texto son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        float precio;
        try {
            precio = Float.parseFloat(precioStr);
            if (precio <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa un precio válido mayor a cero.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<Long> categoriasSeleccionadas = new HashSet<>();
        for (Map.Entry<Long, JCheckBox> entry : checksCategorias.entrySet()) {
            if (entry.getValue().isSelected()) {
                categoriasSeleccionadas.add(entry.getKey());
            }
        }
        if (categoriasSeleccionadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona al menos una categoría.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TipoPrendaDTO tipoPrenda = (TipoPrendaDTO) comboTipoPrenda.getSelectedItem();

        List<ProductoTallaDTO> tallasIniciales = new ArrayList<>();
        if (esNuevo) {
            if (tablaTallas.isEditing()) tablaTallas.getCellEditor().stopCellEditing();
            for (int i = 0; i < modeloTallas.getRowCount(); i++) {
                String tNom = String.valueOf(modeloTallas.getValueAt(i, 0)).trim();
                String tStockStr = String.valueOf(modeloTallas.getValueAt(i, 1)).trim();
                if (tNom.isEmpty()) continue;
                int stock;
                try {
                    stock = Integer.parseInt(tStockStr);
                    if (stock < 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "El stock para la talla " + tNom + " debe ser un número entero mayor o igual a cero.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                tallasIniciales.add(new ProductoTallaDTO(0, 0, tNom.toUpperCase(), stock));
            }
            if (tallasIniciales.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Agrega al menos una talla con existencias para el nuevo producto.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        navegacion.ejecutar(() -> {
            if (esNuevo) {
                ProductoDTO nuevo = new ProductoDTO(cb, sku, nom, desc, precio, tipoPrenda, categoriasSeleccionadas);
                control.guardarProducto(nuevo, tallasIniciales);
                return true;
            } else {
                ProductoDTO actualizar = new ProductoDTO(productoOriginal.getId(), cb, sku, nom, desc, precio, tipoPrenda, categoriasSeleccionadas);
                control.actualizarProducto(actualizar);
                return true;
            }
        }, exito -> {
            navegacion.cerrarModal();
            if (alTerminar != null) alTerminar.run();
            JOptionPane.showMessageDialog(this,
                    esNuevo ? "Producto registrado con éxito en el catálogo." : "Producto actualizado con éxito.",
                    "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
