package vistas;

import com.itson.negocios_gestorreportes.PeriodoReporte;
import controles.ControlGenerarReporte;
import controles.ControlNavegacion;
import dtos.VentaDTO;
import utilerias.Estilo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Pantalla del caso de uso Generar reporte de ventas: filtros, vista previa y exportación a PDF. */
public final class PanelReportes extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ControlGenerarReporte control;
    private final ControlNavegacion navegacion;

    private final JComboBox<PeriodoReporte> cmbPeriodo = new JComboBox<>(PeriodoReporte.values());
    private final JSpinner spnFecha = new JSpinner();
    private final JLabel lblRango = new JLabel(" ");
    private final JLabel lblVentas = valorTarjeta();
    private final JLabel lblSubtotal = valorTarjeta();
    private final JLabel lblIva = valorTarjeta();
    private final JLabel lblTotal = valorTarjeta();
    private final JLabel lblEstado = new JLabel(" ");
    private final JButton btnExportar = Estilo.boton("Exportar PDF", true);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"FOLIO", "FECHA Y HORA", "ARTÍCULOS", "SUBTOTAL", "IVA", "TOTAL"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    /** Periodo y fecha de la última consulta mostrada; la exportación usa exactamente los mismos. */
    private PeriodoReporte periodoConsultado;
    private LocalDate fechaConsultada;

    public PanelReportes(ControlGenerarReporte control, ControlNavegacion navegacion) {
        super(new BorderLayout(0, 16));
        this.control = control;
        this.navegacion = navegacion;
        setBackground(Estilo.FONDO_VENTA);
        setBorder(new EmptyBorder(16, 20, 14, 20));

        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.setOpaque(false);
        superior.add(crearEncabezado());
        superior.add(Box.createVerticalStrut(12));
        superior.add(crearFiltros());
        superior.add(Box.createVerticalStrut(16));
        superior.add(crearTarjetas());
        add(superior, BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);

        registrarTecla("ESCAPE", navegacion::abrirMenuVentas);
        registrarTecla("F5", this::consultar);
        btnExportar.setEnabled(false);
        SwingUtilities.invokeLater(this::consultar);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(LEFT_ALIGNMENT);
        JLabel titulo = Estilo.titulo("Reporte de ventas");
        titulo.setBorder(BorderFactory.createEmptyBorder());
        encabezado.add(titulo, BorderLayout.WEST);
        JButton volver = Estilo.boton("← Volver a ventas", false);
        volver.addActionListener(e -> navegacion.abrirMenuVentas());
        encabezado.add(volver, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearFiltros() {
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filtros.setBackground(Color.WHITE);
        filtros.setAlignmentX(LEFT_ALIGNMENT);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true), new EmptyBorder(6, 8, 6, 8)));

        cmbPeriodo.setSelectedItem(PeriodoReporte.DIA);
        cmbPeriodo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        Date hoy = new Date();
        spnFecha.setModel(new SpinnerDateModel(hoy, null, hoy, Calendar.DAY_OF_MONTH));
        spnFecha.setEditor(new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy"));
        spnFecha.setFont(new Font("SansSerif", Font.PLAIN, 14));
        spnFecha.setPreferredSize(new Dimension(140, 34));

        JButton btnHoy = Estilo.boton("Hoy", false);
        btnHoy.addActionListener(e -> {
            spnFecha.setValue(new Date());
            consultar();
        });
        JButton btnConsultar = Estilo.boton("Consultar", false);
        btnConsultar.addActionListener(e -> consultar());

        cmbPeriodo.addActionListener(e -> consultar());

        lblRango.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblRango.setForeground(Estilo.TEXTO_VENTA);

        filtros.add(etiqueta("Periodo"));
        filtros.add(cmbPeriodo);
        filtros.add(etiqueta("Fecha de referencia"));
        filtros.add(spnFecha);
        filtros.add(btnHoy);
        filtros.add(btnConsultar);
        filtros.add(Box.createHorizontalStrut(12));
        filtros.add(lblRango);
        filtros.setMaximumSize(new Dimension(Short.MAX_VALUE, filtros.getPreferredSize().height));
        return filtros;
    }

    private JPanel crearTarjetas() {
        JPanel tarjetas = new JPanel(new GridLayout(1, 4, 12, 0));
        tarjetas.setOpaque(false);
        tarjetas.setAlignmentX(LEFT_ALIGNMENT);
        tarjetas.add(tarjeta("VENTAS", lblVentas));
        tarjetas.add(tarjeta("SUBTOTAL", lblSubtotal));
        tarjetas.add(tarjeta("IVA", lblIva));
        tarjetas.add(tarjeta("TOTAL", lblTotal));
        lblTotal.setForeground(Estilo.VERDE_OSCURO);
        tarjetas.setMaximumSize(new Dimension(Short.MAX_VALUE, 90));
        return tarjetas;
    }

    private JComponent crearTabla() {
        tabla.setRowHeight(40);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(Estilo.BORDE_TARJETA);
        tabla.setFillsViewportHeight(true);
        // Misma cabecera oscura que la tabla de PanelVenta.
        tabla.getTableHeader().setBackground(Estilo.CABECERA_COBRO);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tabla.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer izquierda = celda(SwingConstants.LEFT);
        DefaultTableCellRenderer derecha = celda(SwingConstants.RIGHT);
        DefaultTableCellRenderer centro = celda(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(izquierda);
        tabla.getColumnModel().getColumn(1).setCellRenderer(izquierda);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centro);
        for (int i = 3; i <= 5; i++) tabla.getColumnModel().getColumn(i).setCellRenderer(derecha);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel crearBarraInferior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblEstado.setForeground(Estilo.TEXTO_DESCUENTO);
        barra.add(lblEstado, BorderLayout.WEST);
        btnExportar.addActionListener(e -> exportar());
        barra.add(btnExportar, BorderLayout.EAST);
        return barra;
    }

    private void consultar() {
        if (navegacion.estaOcupado()) return;
        PeriodoReporte periodo = (PeriodoReporte) cmbPeriodo.getSelectedItem();
        LocalDate fecha = fechaSeleccionada();
        lblRango.setText(control.describirPeriodo(periodo, fecha));
        lblEstado.setText("Consultando ventas…");
        btnExportar.setEnabled(false);
        navegacion.ejecutar(() -> control.consultarVentas(periodo, fecha), ventas -> {
            periodoConsultado = periodo;
            fechaConsultada = fecha;
            mostrar(ventas);
        });
    }

    private void mostrar(List<VentaDTO> ventas) {
        modelo.setRowCount(0);
        float subtotal = 0, iva = 0, total = 0;
        for (VentaDTO venta : ventas) {
            subtotal += venta.getSubtotal();
            iva += venta.getIva();
            total += venta.getTotal();
            modelo.addRow(new Object[]{
                venta.getFolio(),
                venta.getFechaHora() == null ? "" : venta.getFechaHora().format(FORMATO_FECHA_HORA),
                ControlGenerarReporte.contarArticulos(venta),
                Estilo.dinero(venta.getSubtotal()),
                Estilo.dinero(venta.getIva()),
                Estilo.dinero(venta.getTotal())
            });
        }
        lblVentas.setText(Integer.toString(ventas.size()));
        lblSubtotal.setText(Estilo.dinero(subtotal));
        lblIva.setText(Estilo.dinero(iva));
        lblTotal.setText(Estilo.dinero(total));
        lblEstado.setText(ventas.isEmpty()
                ? "No se encontraron ventas en este periodo. Aun así puedes exportar el reporte vacío."
                : ventas.size() + (ventas.size() == 1 ? " venta encontrada." : " ventas encontradas."));
        btnExportar.setEnabled(true);
    }

    private void exportar() {
        if (navegacion.estaOcupado() || periodoConsultado == null) return;
        PeriodoReporte periodo = periodoConsultado;
        LocalDate fecha = fechaConsultada;
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte de ventas");
        selector.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        selector.setSelectedFile(new File(control.nombreArchivoSugerido(periodo, fecha)));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File destino = selector.getSelectedFile();
        if (destino.exists() && JOptionPane.showConfirmDialog(this,
                "El archivo «" + destino.getName() + "» ya existe. ¿Deseas reemplazarlo?",
                "Reemplazar archivo", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        lblEstado.setText("Generando PDF…");
        navegacion.ejecutar(() -> control.exportarPdf(periodo, fecha, destino), archivo -> {
            lblEstado.setText("Reporte guardado en " + archivo.getAbsolutePath());
            ofrecerAbrir(archivo);
        });
    }

    private void ofrecerAbrir(File archivo) {
        boolean puedeAbrir = Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.OPEN);
        if (!puedeAbrir) {
            navegacion.informar("Reporte guardado en:\n" + archivo.getAbsolutePath());
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "Reporte guardado en:\n" + archivo.getAbsolutePath() + "\n\n¿Deseas abrirlo ahora?",
                "Reporte generado", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().open(archivo);
            } catch (Exception ex) {
                navegacion.error(new IllegalStateException("No se pudo abrir el PDF: " + ex.getMessage(), ex));
            }
        }
    }

    private LocalDate fechaSeleccionada() {
        Date fecha = (Date) spnFecha.getValue();
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static DefaultTableCellRenderer celda(int alineacion) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tabla, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {
                JLabel celda = (JLabel) super.getTableCellRendererComponent(
                        tabla, valor, seleccionada, false, fila, columna);
                celda.setBorder(new EmptyBorder(0, 14, 0, 14));
                if (!seleccionada) celda.setForeground(columna == 0 ? Estilo.TEXTO_OSCURO : Estilo.TEXTO_VENTA);
                celda.setFont(new Font("SansSerif", columna == 0 || columna == 5 ? Font.BOLD : Font.PLAIN, 13));
                return celda;
            }
        };
        renderer.setHorizontalAlignment(alineacion);
        return renderer;
    }

    private static JLabel etiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 13));
        etiqueta.setForeground(Estilo.TEXTO_VENTA);
        return etiqueta;
    }

    private static JLabel valorTarjeta() {
        JLabel valor = new JLabel("—");
        valor.setFont(new Font("SansSerif", Font.BOLD, 20));
        valor.setForeground(Estilo.TEXTO_OSCURO);
        return valor;
    }

    private static JPanel tarjeta(String titulo, JLabel valor) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 6));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA, 1, true), new EmptyBorder(12, 16, 12, 16)));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 11));
        etiqueta.setForeground(Estilo.TEXTO_SECUNDARIO);
        tarjeta.add(etiqueta, BorderLayout.NORTH);
        tarjeta.add(valor, BorderLayout.CENTER);
        return tarjeta;
    }

    private void registrarTecla(String tecla, Runnable accion) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla), tecla);
        getActionMap().put(tecla, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent evento) { accion.run(); }
        });
    }
}
