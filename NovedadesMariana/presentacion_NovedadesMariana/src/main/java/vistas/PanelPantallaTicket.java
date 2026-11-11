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
                        : "La venta está guardada. El servicio de impresión aún no está disponible.")));
        JButton nueva = Estilo.boton("Nueva venta", true);
        nueva.addActionListener(e -> navegacion.nuevaVenta());
        lateral.add(imprimir);
        lateral.add(Box.createVerticalStrut(12));
        lateral.add(nueva);
        add(lateral, BorderLayout.EAST);
    }
}