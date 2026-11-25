/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vistas;

import controles.ControlNavegacion;
import dtos.ProductoDTO;
import dtos.TipoPrendaDTO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import utilerias.Estilo;

/**
 *
 * @author Usuario
 */

public final class PanelRegistrarProducto extends JPanel {

    // Campos de texto solicitados para cada atributo de ProductoDTO
    private final JTextField txtCodigoBarras = new JTextField();
    private final JTextField txtSku = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtTipoPrenda = new JTextField();
    private final JTextField txtCategoriasIds = new JTextField();

    // Contenedores de tarjeta para cambiar el color del borde según validación
    private final Map<JTextField, JPanel> tarjetasCampos = new HashMap<>();
    private final Map<JTextField, JLabel> labelsError = new HashMap<>();

    // Botones de acción
    private final JButton btnConfirmar = new JButton("Confirmar Registro");
    private final JButton btnCancelar = botonSecundario("Cancelar");

    // Dependencias / callbacks
    private final ControlNavegacion navegacion;
    private final Consumer<ProductoDTO> onRegistrar;
    private boolean procesando = false;

    /**
     * @param navegacion Controlador de navegación para cerrar modal / ejecutar tareas asíncronas.
     * @param onRegistrar Callback que recibe el ProductoDTO validado al confirmar.
     */
    public PanelRegistrarProducto(ControlNavegacion navegacion, Consumer<ProductoDTO> onRegistrar) {
        super(new BorderLayout());
        this.navegacion = navegacion;
        this.onRegistrar = onRegistrar;

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(760, 620));

        add(crearCabecera(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
        add(crearPieBotones(), BorderLayout.SOUTH);

        configurarValidacionesEnTiempoReal();
        configurarAtajosTeclado();
        validarFormularioCompleto();
    }

    private JPanel crearCabecera() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Estilo.CABECERA_COBRO);
        header.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulos.setOpaque(false);

        JLabel titulo = new JLabel("Registrar Nuevo Producto");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Complete todos los campos obligatorios del catálogo");
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
        cerrar.setToolTipText("Cancelar registro");
        cerrar.addActionListener(e -> cancelarProceso());
        header.add(cerrar, BorderLayout.EAST);

        return header;
    }

    private JPanel crearCuerpo() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 12));
        contenedor.setBackground(Color.WHITE);
        contenedor.setBorder(new EmptyBorder(16, 24, 10, 24));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.NORTH;

        // Fila 0: Código de Barras y SKU
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 10, 10);
        grid.add(crearBloqueCampo("CÓDIGO DE BARRAS *", "Ej. 7501234567890", txtCodigoBarras, null), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 10, 0);
        grid.add(crearBloqueCampo("SKU *", "Ej. ROPA-PLAY-001", txtSku, null), gbc);

        // Fila 1: Nombre y Precio
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 10, 10);
        grid.add(crearBloqueCampo("NOMBRE DEL PRODUCTO *", "Ej. Playera estampada infantil", txtNombre, null), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 10, 0);
        grid.add(crearBloqueCampo("PRECIO DE VENTA (MXN) *", "0.00", txtPrecio, "$"), gbc);

        // Fila 2: Tipo de Prenda y Categorías IDs
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 10, 10);
        String valoresEnum = "PLAYERA, CAMISA, BLUSA, PANTALON, SHORT, FALDA, VESTIDO, SUDADERA, SUETER, CONJUNTO";
        txtTipoPrenda.setToolTipText("Valores permitidos: " + valoresEnum);
        grid.add(crearBloqueCampo("TIPO DE PRENDA * (Ej. PLAYERA, CAMISA, PANTALON...)", "PLAYERA", txtTipoPrenda, null), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 10, 0);
        txtCategoriasIds.setToolTipText("Ingrese uno o más IDs numéricos separados por coma. Ej: 1, 3");
        grid.add(crearBloqueCampo("IDs DE CATEGORÍAS * (Separados por coma)", "Ej. 1, 2", txtCategoriasIds, "#"), gbc);

        // Fila 3: Descripción (ocupa ambas columnas)
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 6, 0);
        grid.add(crearBloqueCampo("DESCRIPCIÓN DEL PRODUCTO *", "Material, corte, detalles o temporada", txtDescripcion, null), gbc);

        // Tarjeta informativa inferior (mismo estilo que el desglose en PanelModalCobro)
        JPanel nota = new JPanel(new BorderLayout());
        nota.setBackground(Estilo.FONDO_TECLAS);
        nota.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA),
                new EmptyBorder(10, 14, 10, 14)));
        JLabel lblNota = new JLabel(
                "<html><b>Tipos válidos:</b> PLAYERA, CAMISA, BLUSA, PANTALON, SHORT, FALDA, VESTIDO, SUDADERA, SUETER, CONJUNTO.</html>");
        lblNota.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblNota.setForeground(Estilo.TEXTO_SECUNDARIO);
        nota.add(lblNota, BorderLayout.CENTER);

        contenedor.add(grid, BorderLayout.NORTH);
        contenedor.add(nota, BorderLayout.SOUTH);
        return contenedor;
    }

    private JPanel crearBloqueCampo(String tituloLabel, String placeholder, JTextField campo, String prefijo) {
        JPanel bloque = new JPanel();
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setOpaque(false);

        JLabel label = new JLabel(tituloLabel);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(Estilo.TEXTO_SECUNDARIO);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setLabelFor(campo);

        JPanel tarjetaInput = new JPanel(new BorderLayout(8, 0));
        tarjetaInput.setBackground(Color.WHITE);
        tarjetaInput.setAlignmentX(LEFT_ALIGNMENT);
        tarjetaInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA),
                new EmptyBorder(8, 12, 8, 12)));
        tarjetaInput.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        tarjetaInput.setPreferredSize(new Dimension(100, 40));

        if (prefijo != null) {
            JLabel lblPrefijo = new JLabel(prefijo);
            lblPrefijo.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblPrefijo.setForeground(Estilo.TEXTO_SECUNDARIO);
            tarjetaInput.add(lblPrefijo, BorderLayout.WEST);
        }

        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(Estilo.TEXTO_OSCURO);
        campo.setBorder(null);
        campo.putClientProperty("JTextField.placeholderText", placeholder);
        tarjetaInput.add(campo, BorderLayout.CENTER);

        JLabel lblError = new JLabel(" ");
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblError.setForeground(Estilo.ERROR);
        lblError.setAlignmentX(LEFT_ALIGNMENT);

        tarjetasCampos.put(campo, tarjetaInput);
        labelsError.put(campo, lblError);

        bloque.add(label);
        bloque.add(Box.createVerticalStrut(4));
        bloque.add(tarjetaInput);
        bloque.add(Box.createVerticalStrut(2));
        bloque.add(lblError);
        return bloque;
    }

    private JPanel crearPieBotones() {
        JPanel pie = new JPanel(new GridLayout(1, 2, 12, 0));
        pie.setBackground(Color.WHITE);
        pie.setBorder(new EmptyBorder(8, 24, 20, 24));

        btnCancelar.setPreferredSize(new Dimension(0, 44));
        btnCancelar.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btnCancelar.addActionListener(e -> cancelarProceso());

        btnConfirmar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnConfirmar.setBackground(Estilo.VERDE_COBRO);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirmar.setBorder(new EmptyBorder(10, 14, 10, 14));
        btnConfirmar.setPreferredSize(new Dimension(0, 44));
        btnConfirmar.addActionListener(e -> confirmarRegistro());

        pie.add(btnCancelar);
        pie.add(btnConfirmar);
        return pie;
    }

    private JButton botonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBackground(Estilo.FONDO_TECLAS);
        boton.setForeground(Estilo.TEXTO_SECUNDARIO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA),
                new EmptyBorder(8, 12, 8, 12)));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void configurarValidacionesEnTiempoReal() {
        DocumentListener listener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validarFormularioCompleto(); }
            @Override public void removeUpdate(DocumentEvent e) { validarFormularioCompleto(); }
            @Override public void changedUpdate(DocumentEvent e) { validarFormularioCompleto(); }
        };

        txtCodigoBarras.getDocument().addDocumentListener(listener);
        txtSku.getDocument().addDocumentListener(listener);
        txtNombre.getDocument().addDocumentListener(listener);
        txtDescripcion.getDocument().addDocumentListener(listener);
        txtPrecio.getDocument().addDocumentListener(listener);
        txtTipoPrenda.getDocument().addDocumentListener(listener);
        txtCategoriasIds.getDocument().addDocumentListener(listener);
    }

    private void configurarAtajosTeclado() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "confirmar");
        getActionMap().put("confirmar", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (btnConfirmar.isEnabled()) confirmarRegistro();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelar");
        getActionMap().put("cancelar", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                cancelarProceso();
            }
        });
    }

    // ==================== VALIDACIONES ====================

    private boolean validarFormularioCompleto() {
        boolean v1 = validarCodigoBarras();
        boolean v2 = validarSku();
        boolean v3 = validarNombre();
        boolean v4 = validarDescripcion();
        boolean v5 = validarPrecio();
        boolean v6 = validarTipoPrenda();
        boolean v7 = validarCategoriasIds();

        boolean todoValido = v1 && v2 && v3 && v4 && v5 && v6 && v7;
        btnConfirmar.setEnabled(!procesando && todoValido);
        btnConfirmar.setBackground(todoValido ? Estilo.VERDE_COBRO : Estilo.BORDE_TARJETA);
        return todoValido;
    }

    private boolean validarCodigoBarras() {
        String val = txtCodigoBarras.getText().trim();
        if (val.isEmpty()) {
            return marcarEstadoCampo(txtCodigoBarras, false, "El código de barras es obligatorio.");
        }
        if (!val.matches("^[A-Za-z0-9\\-]{4,50}$")) {
            return marcarEstadoCampo(txtCodigoBarras, false, "Debe tener entre 4 y 50 caracteres alfanuméricos.");
        }
        return marcarEstadoCampo(txtCodigoBarras, true, null);
    }

    private boolean validarSku() {
        String val = txtSku.getText().trim();
        if (val.isEmpty()) {
            return marcarEstadoCampo(txtSku, false, "El SKU es obligatorio.");
        }
        if (!val.matches("^[A-Za-z0-9\\-_]{3,30}$")) {
            return marcarEstadoCampo(txtSku, false, "Solo letras, números, '-' o '_' (3-30 caracteres).");
        }
        return marcarEstadoCampo(txtSku, true, null);
    }

    private boolean validarNombre() {
        String val = txtNombre.getText().trim();
        if (val.isEmpty()) {
            return marcarEstadoCampo(txtNombre, false, "El nombre es obligatorio.");
        }
        if (val.length() < 3 || val.length() > 100) {
            return marcarEstadoCampo(txtNombre, false, "Debe contener entre 3 y 100 caracteres.");
        }
        return marcarEstadoCampo(txtNombre, true, null);
    }

    private boolean validarDescripcion() {
        String val = txtDescripcion.getText().trim();
        if (val.isEmpty()) {
            return marcarEstadoCampo(txtDescripcion, false, "La descripción es obligatoria.");
        }
        if (val.length() < 5 || val.length() > 255) {
            return marcarEstadoCampo(txtDescripcion, false, "Debe contener entre 5 y 255 caracteres.");
        }
        return marcarEstadoCampo(txtDescripcion, true, null);
    }

    private boolean validarPrecio() {
        String val = txtPrecio.getText().trim();
        if (val.isEmpty()) {
            return marcarEstadoCampo(txtPrecio, false, "El precio es obligatorio.");
        }
        try {
            float precio = Float.parseFloat(val);
            if (!Float.isFinite(precio) || precio <= 0f) {
                return marcarEstadoCampo(txtPrecio, false, "El precio debe ser mayor a $0.00.");
            }
            return marcarEstadoCampo(txtPrecio, true, null);
        } catch (NumberFormatException ex) {
            return marcarEstadoCampo(txtPrecio, false, "Ingrese un monto numérico válido (ej. 249.90).");
        }
    }

    private boolean validarTipoPrenda() {
        String val = txtTipoPrenda.getText().trim().toUpperCase(Locale.ROOT);
        if (val.isEmpty()) {
            return marcarEstadoCampo(txtTipoPrenda, false, "El tipo de prenda es obligatorio.");
        }
        try {
            TipoPrendaDTO.valueOf(val);
            return marcarEstadoCampo(txtTipoPrenda, true, null);
        } catch (IllegalArgumentException ex) {
            return marcarEstadoCampo(txtTipoPrenda, false, "Tipo inválido. Use: PLAYERA, CAMISA, PANTALON, etc.");
        }
    }

    private boolean validarCategoriasIds() {
        String val = txtCategoriasIds.getText().trim();
        if (val.isEmpty()) {
            return marcarEstadoCampo(txtCategoriasIds, false, "Ingrese al menos un ID de categoría.");
        }
        try {
            Set<Long> ids = parsearCategoriasIds(val);
            if (ids.isEmpty()) {
                return marcarEstadoCampo(txtCategoriasIds, false, "Ingrese al menos un ID válido.");
            }
            return marcarEstadoCampo(txtCategoriasIds, true, null);
        } catch (NumberFormatException ex) {
            return marcarEstadoCampo(txtCategoriasIds, false, "Solo números enteros positivos separados por coma.");
        }
    }

    private Set<Long> parsearCategoriasIds(String texto) throws NumberFormatException {
        Set<Long> ids = new LinkedHashSet<>();
        String[] partes = texto.split(",");
        for (String parte : partes) {
            String limpio = parte.trim();
            if (limpio.isEmpty()) throw new NumberFormatException("Coma extra");
            long id = Long.parseLong(limpio);
            if (id <= 0) throw new NumberFormatException("ID debe ser positivo");
            ids.add(id);
        }
        return ids;
    }

    private boolean marcarEstadoCampo(JTextField campo, boolean valido, String mensajeError) {
        JPanel tarjeta = tarjetasCampos.get(campo);
        JLabel lblError = labelsError.get(campo);

        if (valido) {
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Estilo.BORDE_ACTIVO),
                    new EmptyBorder(8, 12, 8, 12)));
            lblError.setText(" ");
        } else {
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Estilo.ERROR),
                    new EmptyBorder(8, 12, 8, 12)));
            lblError.setText(mensajeError);
        }
        return valido;
    }

    // ==================== ACCIONES ====================

    private void confirmarRegistro() {
        if (procesando || (navegacion != null && navegacion.estaOcupado())) {
            return;
        }

        // Doble verificación antes de construir el DTO
        if (!validarFormularioCompleto()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor corrija los campos marcados en rojo antes de confirmar.",
                    "Datos incompletos o inválidos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigoBarras = txtCodigoBarras.getText().trim();
        String sku = txtSku.getText().trim().toUpperCase(Locale.ROOT);
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        float precio = Float.parseFloat(txtPrecio.getText().trim());
        TipoPrendaDTO tipoPrenda = TipoPrendaDTO.valueOf(txtTipoPrenda.getText().trim().toUpperCase(Locale.ROOT));
        Set<Long> categoriasIds = parsearCategoriasIds(txtCategoriasIds.getText().trim());

        ProductoDTO nuevoProducto = new ProductoDTO(
                codigoBarras,
                sku,
                nombre,
                descripcion,
                precio,
                tipoPrenda,
                categoriasIds
        );

        setProcesando(true);
        try {
            if (onRegistrar != null) {
                onRegistrar.accept(nuevoProducto);
            }
            if (navegacion != null) {
                navegacion.informar("Producto registrado correctamente.");
                navegacion.cerrarModal();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo registrar el producto: " + ex.getMessage(),
                    "Error al registrar",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            setProcesando(false);
        }
    }

    private void cancelarProceso() {
        if (procesando) return;
        boolean hayDatosEscritos = !txtCodigoBarras.getText().trim().isEmpty()
                || !txtSku.getText().trim().isEmpty()
                || !txtNombre.getText().trim().isEmpty()
                || !txtDescripcion.getText().trim().isEmpty()
                || !txtPrecio.getText().trim().isEmpty()
                || !txtTipoPrenda.getText().trim().isEmpty()
                || !txtCategoriasIds.getText().trim().isEmpty();

        if (hayDatosEscritos) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas cancelar el registro? Se perderán los datos ingresados.",
                    "Confirmar cancelación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (opcion != JOptionPane.YES_OPTION) return;
        }

        if (navegacion != null) {
            navegacion.cerrarModal();
        }
    }

    private void setProcesando(boolean valor) {
        this.procesando = valor;
        txtCodigoBarras.setEnabled(!valor);
        txtSku.setEnabled(!valor);
        txtNombre.setEnabled(!valor);
        txtDescripcion.setEnabled(!valor);
        txtPrecio.setEnabled(!valor);
        txtTipoPrenda.setEnabled(!valor);
        txtCategoriasIds.setEnabled(!valor);
        btnCancelar.setEnabled(!valor);
        validarFormularioCompleto();
    }
}
