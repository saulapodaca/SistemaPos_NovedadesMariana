package com.itson.tickets;

import dtos.VentaDTO;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.print.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/** Imprime mediante el controlador del sistema operativo, en papel de rollo. */
public final class GeneradorTicket {
    public List<String> lineas(VentaDTO venta) {
        var dinero = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"));
        List<String> lineas = new ArrayList<>();
        lineas.add("NOVEDADES MARIANA");
        lineas.add("Folio: " + venta.getFolio());
        lineas.add(venta.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lineas.add("Empleado: " + venta.getIdUsuario());
        lineas.add("");
        for (var d : venta.getDetalles()) {
            lineas.add(d.getNombreProducto() + (d.getTalla() == null ? "" : " · " + d.getTalla()));
            lineas.add(d.getCantidad() + " x " + dinero.format(d.getPrecioUnitario()) + "   " + dinero.format(d.getSubtotal()));
        }
        lineas.add("Subtotal: " + dinero.format(venta.getSubtotal()));
        lineas.add("Descuento: " + dinero.format(Math.max(0, venta.getSubtotal() + venta.getIva() - venta.getTotal())));
        lineas.add("IVA: " + dinero.format(venta.getIva()));
        lineas.add("TOTAL: " + dinero.format(venta.getTotal()));
        for (var pago : venta.getPagos()) {
            lineas.add("Pago: " + pago.getMetodoPago());
            lineas.add("Recibido: " + dinero.format(pago.getMontoRecibido()));
            lineas.add("Cambio: " + dinero.format(pago.getCambio()));
        }
        lineas.add("Gracias por su compra");
        return lineas;
    }

    public boolean imprimir(VentaDTO venta) {
        String nombre = System.getenv("POS_IMPRESORA_TICKET");
        PrintService servicio = PrintServiceLookup.lookupDefaultPrintService();
        if (nombre != null && !nombre.isBlank()) {
            servicio = java.util.Arrays.stream(PrinterJob.lookupPrintServices())
                    .filter(p -> p.getName().equalsIgnoreCase(nombre.trim())).findFirst().orElse(null);
        }
        if (servicio == null) throw new IllegalStateException("Revisa la impresora instalada y POS_IMPRESORA_TICKET.");
        String ancho = System.getenv().getOrDefault("POS_TICKET_MM", "80");
        if (!List.of("58", "80").contains(ancho)) throw new IllegalArgumentException("POS_TICKET_MM admite 58 u 80.");
        List<String> texto = lineas(venta);
        try {
            PrinterJob trabajo = PrinterJob.getPrinterJob();
            trabajo.setPrintService(servicio);
            trabajo.setJobName("Ticket " + venta.getFolio());
            double puntos = Integer.parseInt(ancho) * 72.0 / 25.4;
            Paper papel = new Paper();
            double alto = Math.max(160, texto.stream().mapToInt(l -> 1 + l.length() / 24).sum() * 12 + 20);
            papel.setSize(puntos, alto);
            papel.setImageableArea(8, 6, puntos - 16, alto - 12);
            PageFormat formato = trabajo.defaultPage();
            formato.setPaper(papel);
            trabajo.setPrintable((g, pagina, indice) -> {
                Graphics2D grafico = (Graphics2D) g.create();
                try {
                    grafico.translate(pagina.getImageableX(), pagina.getImageableY());
                    grafico.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 9));
                    int columnas = Math.max(1, (int) pagina.getImageableWidth() / grafico.getFontMetrics().charWidth('M'));
                    List<String> filas = new ArrayList<>();
                    for (String linea : texto) {
                        if (linea.isEmpty()) filas.add("");
                        for (int i = 0; i < linea.length(); i += columnas)
                            filas.add(linea.substring(i, Math.min(i + columnas, linea.length())));
                    }
                    int porPagina = Math.max(1, (int) pagina.getImageableHeight() / 12);
                    int inicio = indice * porPagina;
                    if (inicio >= filas.size()) return Printable.NO_SUCH_PAGE;
                    for (int i = inicio; i < Math.min(inicio + porPagina, filas.size()); i++)
                        grafico.drawString(filas.get(i), 0, (i - inicio + 1) * 12 - 2);
                    return Printable.PAGE_EXISTS;
                } finally {
                    grafico.dispose();
                }
            }, trabajo.validatePage(formato));
            trabajo.print();
            return true;
        } catch (PrinterException ex) {
            throw new IllegalStateException("Falló la impresión en " + servicio.getName() + ": " + ex.getMessage(), ex);
        }
    }
}
