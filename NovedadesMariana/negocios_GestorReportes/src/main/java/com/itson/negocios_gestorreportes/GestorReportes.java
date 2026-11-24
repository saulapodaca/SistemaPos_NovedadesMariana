package com.itson.negocios_gestorreportes;

import com.itson.negocios_gestorventas.GestorVentas;
import com.itson.negocios_gestorventas.IGestorVentas;
import dtos.DetalleVentaDTO;
import dtos.VentaDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

/** Consulta ventas persistidas y las presenta en un documento PDF. */
public class GestorReportes implements IGestorReportes {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "MX"));
    private static final float MARGEN = 48.0f;

    private final IGestorVentas gestorVentas;

    public GestorReportes() {
        this(new GestorVentas());
    }

    public GestorReportes(IGestorVentas gestorVentas) {
        this.gestorVentas = Objects.requireNonNull(gestorVentas, "El gestor de ventas es obligatorio.");
    }

    @Override
    public byte[] generarReportePdf(PeriodoReporte periodo, LocalDate fechaReferencia) {
        if (periodo == null) {
            throw new IllegalArgumentException("Debe seleccionar día, semana o mes para el reporte.");
        }
        if (fechaReferencia == null) {
            throw new IllegalArgumentException("Debe indicar la fecha de referencia del reporte.");
        }

        LocalDate inicio;
        LocalDate fin;
        switch (periodo) {
            case DIA -> {
                inicio = fechaReferencia;
                fin = inicio.plusDays(1);
            }
            case SEMANA -> {
                inicio = fechaReferencia.with(DayOfWeek.MONDAY);
                fin = inicio.plusWeeks(1);
            }
            case MES -> {
                inicio = fechaReferencia.withDayOfMonth(1);
                fin = inicio.plusMonths(1);
            }
            default -> throw new IllegalArgumentException("El periodo de reporte no es válido.");
        }

        List<VentaDTO> ventas = gestorVentas.obtenerVentasEntre(
                inicio.atStartOfDay(), fin.atStartOfDay());
        return crearPdf(periodo, inicio, fin, ventas);
    }

    private byte[] crearPdf(PeriodoReporte periodo, LocalDate inicio,
            LocalDate fin, List<VentaDTO> ventas) {
        String descripcionPeriodo = describirPeriodo(periodo, inicio, fin);
        try (PDDocument documento = new PDDocument();
                ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            try (RenderizadorReporte reporte = new RenderizadorReporte(documento, descripcionPeriodo)) {
                reporte.renderizar(ventas);
            }
            documento.save(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el PDF del reporte de ventas.", e);
        }
    }

    private String describirPeriodo(PeriodoReporte periodo, LocalDate inicio, LocalDate fin) {
        return switch (periodo) {
            case DIA -> inicio.format(FORMATO_FECHA);
            case SEMANA -> inicio.format(FORMATO_FECHA) + " - " + fin.minusDays(1).format(FORMATO_FECHA);
            case MES -> inicio.format(FORMATO_MES);
        };
    }

    private static final class RenderizadorReporte implements AutoCloseable {

        private static final float ANCHO_CONTENIDO = PDRectangle.A4.getWidth() - 2 * MARGEN;
        private static final float Y_PIE = 48.0f;
        private static final float Y_MINIMO = Y_PIE + 18.0f;

        private final PDDocument documento;
        private final String periodo;
        private final PDFont normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        private final PDFont negrita = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        private PDPageContentStream contenido;
        private float y;
        private int numeroPagina;

        private RenderizadorReporte(PDDocument documento, String periodo) throws IOException {
            this.documento = documento;
            this.periodo = periodo;
            nuevaPagina(false);
        }

        private void renderizar(List<VentaDTO> ventas) throws IOException {
            dibujarResumen(ventas);
            if (ventas == null || ventas.stream().noneMatch(Objects::nonNull)) {
                asegurarEspacio(30.0f);
                texto("No se encontraron ventas en este periodo.", MARGEN, y, normal, 11.0f);
                y -= 24.0f;
            } else {
                for (VentaDTO venta : ventas) {
                    if (venta != null) {
                        dibujarVenta(venta);
                    }
                }
            }
            dibujarPie();
        }

        private void dibujarEncabezado(boolean continuacion) throws IOException {
            float altoPagina = PDRectangle.A4.getHeight();
            contenido.setNonStrokingColor(rgb(33, 57, 88));
            contenido.addRect(0, altoPagina - 104.0f, PDRectangle.A4.getWidth(), 104.0f);
            contenido.fill();
            texto("Novedades Mariana", MARGEN, altoPagina - 52.0f, negrita, 19.0f, true);
            texto(continuacion ? "Reporte de ventas - continuacion" : "Reporte de ventas",
                    MARGEN, altoPagina - 74.0f, normal, 12.0f, true);
            texto("Periodo: " + periodo, MARGEN, altoPagina - 92.0f, normal, 9.0f, true);
            y = altoPagina - 130.0f;
        }

        private void dibujarResumen(List<VentaDTO> ventas) throws IOException {
            float subtotal = 0.0f;
            float iva = 0.0f;
            float total = 0.0f;
            long cantidad = 0;
            if (ventas != null) {
                for (VentaDTO venta : ventas) {
                    if (venta != null) {
                        cantidad++;
                        subtotal += venta.getSubtotal();
                        iva += venta.getIva();
                        total += venta.getTotal();
                    }
                }
            }

            texto("Generado: " + LocalDateTime.now().format(FORMATO_FECHA_HORA), MARGEN, y, normal, 9.0f);
            y -= 28.0f;
            float cardGap = 8.0f;
            float cardWidth = (ANCHO_CONTENIDO - 3 * cardGap) / 4.0f;
            dibujarTarjeta(MARGEN, cardWidth, "VENTAS", Long.toString(cantidad));
            dibujarTarjeta(MARGEN + cardWidth + cardGap, cardWidth, "SUBTOTAL", dinero(subtotal));
            dibujarTarjeta(MARGEN + 2 * (cardWidth + cardGap), cardWidth, "IVA", dinero(iva));
            dibujarTarjeta(MARGEN + 3 * (cardWidth + cardGap), cardWidth, "TOTAL", dinero(total));
            y -= 76.0f;
            texto("DETALLE DE VENTAS", MARGEN, y, negrita, 11.0f);
            y -= 18.0f;
            linea(MARGEN, y, PDRectangle.A4.getWidth() - MARGEN, y, 0.75f);
            y -= 14.0f;
        }

        private void dibujarTarjeta(float x, float ancho, String etiqueta, String valor) throws IOException {
            float alto = 54.0f;
            float base = y - alto + 8.0f;
            contenido.setNonStrokingColor(rgb(241, 245, 249));
            contenido.addRect(x, base, ancho, alto);
            contenido.fill();
            texto(etiqueta, x + 10.0f, y - 13.0f, negrita, 8.0f);
            texto(valor, x + 8.0f, y - 35.0f, negrita, 11.0f);
        }

        private void dibujarVenta(VentaDTO venta) throws IOException {
            asegurarEspacio(76.0f);
            texto("Folio: " + valor(venta.getFolio(), "SIN FOLIO"), MARGEN, y, negrita, 10.0f);
            String fecha = venta.getFechaHora() == null
                    ? "Fecha no disponible" : venta.getFechaHora().format(FORMATO_FECHA_HORA);
            textoDerecha(fecha, PDRectangle.A4.getWidth() - MARGEN, y, normal, 9.0f);
            y -= 17.0f;

            List<DetalleVentaDTO> detalles = venta.getDetalles();
            if (detalles != null) {
                for (DetalleVentaDTO detalle : detalles) {
                    if (detalle == null) {
                        continue;
                    }
                    String concepto = detalle.getCantidad() + " x "
                            + valor(detalle.getNombreProducto(), "Producto");
                    List<String> lineas = ajustar(concepto, normal, 9.0f, ANCHO_CONTENIDO - 100.0f);
                    for (int i = 0; i < lineas.size(); i++) {
                        asegurarEspacio(16.0f);
                        texto(lineas.get(i), MARGEN + (i == 0 ? 8.0f : 20.0f), y, normal, 9.0f);
                        if (i == 0) {
                            textoDerecha(dinero(detalle.getSubtotal()), PDRectangle.A4.getWidth() - MARGEN,
                                    y, normal, 9.0f);
                        }
                        y -= 14.0f;
                    }
                }
            }

            asegurarEspacio(52.0f);
            texto("Subtotal: " + dinero(venta.getSubtotal()), MARGEN + 8.0f, y, normal, 9.0f);
            y -= 13.0f;
            texto("IVA: " + dinero(venta.getIva()), MARGEN + 8.0f, y, normal, 9.0f);
            y -= 13.0f;
            texto("Total: " + dinero(venta.getTotal()), MARGEN + 8.0f, y, negrita, 10.0f);
            y -= 18.0f;
            linea(MARGEN, y, PDRectangle.A4.getWidth() - MARGEN, y, 0.4f);
            y -= 13.0f;
        }

        private void asegurarEspacio(float altoNecesario) throws IOException {
            if (y - altoNecesario < Y_MINIMO) {
                dibujarPie();
                nuevaPagina(true);
            }
        }

        private void nuevaPagina(boolean continuacion) throws IOException {
            if (contenido != null) {
                contenido.close();
            }
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            contenido = new PDPageContentStream(documento, pagina);
            numeroPagina++;
            dibujarEncabezado(continuacion);
        }

        private void dibujarPie() throws IOException {
            linea(MARGEN, Y_PIE + 10.0f, PDRectangle.A4.getWidth() - MARGEN, Y_PIE + 10.0f, 0.5f);
            String textoPie = "Novedades Mariana | Reporte de ventas | Pagina " + numeroPagina;
            texto(textoPie, MARGEN, Y_PIE - 2.0f, normal, 8.0f);
        }

        private void texto(String valor, float x, float base, PDFont fuente, float tamano) throws IOException {
            texto(valor, x, base, fuente, tamano, false);
        }

        private void texto(String valor, float x, float base, PDFont fuente,
                float tamano, boolean blanco) throws IOException {
            contenido.beginText();
            contenido.setFont(fuente, tamano);
            contenido.setNonStrokingColor(blanco ? rgb(255, 255, 255) : rgb(35, 45, 60));
            contenido.newLineAtOffset(x, base);
            contenido.showText(textoCompatible(valor, fuente));
            contenido.endText();
        }

        private void textoDerecha(String valor, float bordeDerecho, float base,
                PDFont fuente, float tamano) throws IOException {
            String compatible = textoCompatible(valor, fuente);
            float ancho = fuente.getStringWidth(compatible) / 1000.0f * tamano;
            texto(compatible, bordeDerecho - ancho, base, fuente, tamano);
        }

        private void linea(float x1, float y1, float x2, float y2, float grosor) throws IOException {
            contenido.setStrokingColor(rgb(203, 213, 225));
            contenido.setLineWidth(grosor);
            contenido.moveTo(x1, y1);
            contenido.lineTo(x2, y2);
            contenido.stroke();
        }

        private List<String> ajustar(String valor, PDFont fuente, float tamano,
                float anchoMaximo) throws IOException {
            String compatible = textoCompatible(valor, fuente);
            List<String> resultado = new ArrayList<>();
            StringBuilder linea = new StringBuilder();
            for (String palabra : compatible.split("\\s+")) {
                String candidata = linea.isEmpty() ? palabra : linea + " " + palabra;
                if (!linea.isEmpty() && fuente.getStringWidth(candidata) / 1000.0f * tamano > anchoMaximo) {
                    resultado.add(linea.toString());
                    linea.setLength(0);
                    linea.append(palabra);
                } else {
                    if (!linea.isEmpty()) {
                        linea.append(' ');
                    }
                    linea.append(palabra);
                }
            }
            if (!linea.isEmpty()) {
                resultado.add(linea.toString());
            }
            return resultado;
        }

        private String textoCompatible(String valor, PDFont fuente) throws IOException {
            String plano = Normalizer.normalize(valor == null ? "" : valor, Normalizer.Form.NFC)
                    .replaceAll("[\\r\\n\\t]", " ");
            StringBuilder seguro = new StringBuilder();
            for (int i = 0; i < plano.length();) {
                int punto = plano.codePointAt(i);
                String caracter = new String(Character.toChars(punto));
                try {
                    fuente.encode(caracter);
                    seguro.append(caracter);
                } catch (IllegalArgumentException e) {
                    seguro.append('?');
                }
                i += Character.charCount(punto);
            }
            return seguro.toString();
        }

        private String dinero(float importe) {
            return String.format(Locale.US, "$%,.2f", importe);
        }

        private String valor(String texto, String alternativo) {
            return texto == null || texto.isBlank() ? alternativo : texto;
        }

        private PDColor rgb(int rojo, int verde, int azul) {
            return new PDColor(new float[]{rojo / 255.0f, verde / 255.0f, azul / 255.0f}, PDDeviceRGB.INSTANCE);
        }

        @Override
        public void close() throws IOException {
            if (contenido != null) {
                contenido.close();
                contenido = null;
            }
        }
    }
}
