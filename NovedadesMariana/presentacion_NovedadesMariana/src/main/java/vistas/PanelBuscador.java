package vistas;

import utilerias.Estilo;

import java.awt.BorderLayout;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Observa el documento del campo y notifica cada cambio al controlador. */
public final class PanelBuscador extends JPanel {
    private final JTextField texto = new JTextField();
    private Consumer<String> onBusquedaChange = valor -> { };

    public PanelBuscador() {
        super(new BorderLayout(8, 8));
        setBackground(Estilo.FONDO_VENTA);
        add(new JLabel("Buscar por código de barras, SKU o nombre"), BorderLayout.NORTH);
        texto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE_TARJETA),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        texto.setToolTipText("Escribe para consultar productos");
        add(texto, BorderLayout.CENTER);
        texto.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent evento) { notificarCambio(); }
            public void removeUpdate(DocumentEvent evento) { notificarCambio(); }
            public void changedUpdate(DocumentEvent evento) { notificarCambio(); }
        });
    }

    public void setOnBusquedaChange(Consumer<String> observador) {
        onBusquedaChange = java.util.Objects.requireNonNull(observador);
    }

    private void notificarCambio() { onBusquedaChange.accept(getTextoBusqueda()); }
    public String getTextoBusqueda() { return texto.getText(); }
    public void setTextoBusqueda(String valor) { texto.setText(valor == null ? "" : valor); }
    public void enfocar() { texto.requestFocusInWindow(); }
    public void setOnConfirmar(Runnable accion) {
        java.util.Objects.requireNonNull(accion);
        texto.addActionListener(evento -> accion.run());
    }
}