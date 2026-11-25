package vistas;

import utilerias.Estilo;

import controles.*;
import dtos.*;
import java.awt.*;
import javax.swing.*;

public final class PanelPantallaTicket extends JPanel {
    public PanelPantallaTicket(ControlRealizarVenta control, ControlNavegacion navegacion) {
        super(new BorderLayout(24, 20));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(28, 70, 28, 70));
        JLabel exito = Estilo.titulo("Venta registrada con éxito");
        exito.setForeground(Estilo.VERDE);
        add(exito, BorderLayout.NORTH);
        VentaDTO venta = control.getVenta();
        JTextArea ticket = new JTextArea();
        ticket.setEditable(false);
        ticket.setFont(new Font("Monospaced", Font.PLAIN, 14));
        ticket.setMargin(new Insets(24, 32, 24, 32));
        StringBuilder texto = new StringBuilder("         NOVEDADES MARIANA\n      Ropa infantil y accesorios\n\n");
        texto.append("Folio: ").append(venta.getFolio()).append('\n');
        texto.append("Fecha: ").append(venta.getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append('\n');
        texto.append("Empleado: ").append(control.getUsuario().getNombres()).append("\n\n");
        for (DetalleVentaDTO detalle : venta.getDetalles()) {
            texto.append(detalle.getNombreProducto()).append(" · ").append(detalle.getTalla()).append('\n');
            texto.append(detalle.getCantidad()).append(" x ").append(Estilo.dinero(detalle.getPrecioUnitario()))
                    .append("    ").append(Estilo.dinero(detalle.getSubtotal())).append("\n\n");
        }
        texto.append("Subtotal:  ").append(Estilo.dinero(venta.getSubtotal())).append('\n');
        texto.append("Descuento: ").append(Estilo.dinero(control.getDescuento())).append('\n');
        if (!control.getMotivoDescuento().isBlank()) {
            texto.append("Motivo: ").append(control.getMotivoDescuento()).append('\n');
        }
        texto.append("IVA:       ").append(Estilo.dinero(venta.getIva())).append('\n');
        texto.append("TOTAL:     ").append(Estilo.dinero(venta.getTotal())).append('\n');
        PagoDTO pago = venta.getPagos().get(0);
        texto.append("Pago:      ").append(pago.getMetodoPago()).append('\n');
        texto.append("Recibido:  ").append(Estilo.dinero(pago.getMontoRecibido())).append('\n');
        texto.append("Cambio:    ").append(Estilo.dinero(pago.getCambio())).append("\n\nGracias por su compra");
        ticket.setText(texto.toString());
        ticket.setCaretPosition(0);
        add(new JScrollPane(ticket), BorderLayout.CENTER);
        JPanel lateral = Estilo.vertical();
        lateral.add(Estilo.titulo("Venta completada"));
        lateral.add(new JLabel("Inventario actualizado"));
        lateral.add(Box.createVerticalStrut(20));
        lateral.add(new JLabel("Cambio: " + Estilo.dinero(pago.getCambio())));
        lateral.add(Box.createVerticalGlue());
        JButton imprimir = Estilo.boton("Imprimir ticket", false);
        imprimir.addActionListener(e -> navegacion.ejecutar(control::imprimirTicket, impreso ->
                navegacion.informar(impreso ? "Ticket enviado a impresión."
                        : "No se encontró la venta para imprimir.")));
        JButton nueva = Estilo.boton("Nueva venta", true);
        nueva.addActionListener(e -> navegacion.nuevaVenta());
        JButton factura = Estilo.boton("Solicitar factura", false);
        factura.addActionListener(e -> {
            JTextField rfc = new JTextField();
            JTextField nombre = new JTextField();
            JTextField cp = new JTextField();
            JTextField regimen = new JTextField();
            JTextField uso = new JTextField();
            JTextField correo = new JTextField();
            JPanel campos = new JPanel(new GridLayout(0, 1, 2, 3));
            Object[][] entradas = {{"RFC", rfc}, {"Nombre o razón social", nombre},
                    {"Código postal fiscal", cp}, {"Clave de régimen fiscal", regimen},
                    {"Clave de uso del CFDI", uso}, {"Correo (opcional)", correo}};
            for (Object[] entrada : entradas) {
                JLabel etiqueta = new JLabel((String) entrada[0]);
                JTextField campo = (JTextField) entrada[1];
                etiqueta.setLabelFor(campo);
                campos.add(etiqueta);
                campos.add(campo);
            }
            JButton enviar = Estilo.boton("Registrar solicitud", true);
            enviar.addActionListener(evento -> {
                DatosFiscalesDTO datos = new DatosFiscalesDTO(rfc.getText(), nombre.getText(),
                        cp.getText(), regimen.getText(), uso.getText(), correo.getText());
                navegacion.ejecutar(() -> control.solicitarFactura(venta.getId(), datos), registrada -> {
                    if (!registrada) throw new IllegalStateException("No se pudo registrar la solicitud.");
                    factura.setEnabled(false);
                    factura.setText("Solicitud pendiente");
                    navegacion.cerrarModal();
                    navegacion.informar("Solicitud guardada para la venta " + venta.getFolio() + ". Pendiente de facturación.");
                });
            });
            JPanel panel = Estilo.vertical();
            panel.add(campos);
            panel.add(enviar);
            navegacion.mostrarModal("Solicitar factura · " + venta.getFolio(), panel, 470, 480);
        });
        lateral.add(imprimir);
        lateral.add(Box.createVerticalStrut(12));
        lateral.add(factura);
        lateral.add(Box.createVerticalStrut(12));
        lateral.add(nueva);
        add(lateral, BorderLayout.EAST);
    }
}