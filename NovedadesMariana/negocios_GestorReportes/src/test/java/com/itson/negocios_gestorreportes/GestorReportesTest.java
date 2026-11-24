package com.itson.negocios_gestorreportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dtos.DetalleVentaDTO;
import dtos.ResumenVentaDTO;
import dtos.VentaDTO;
import com.itson.negocios_gestorventas.IGestorVentas;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

class GestorReportesTest {

    @Test
    void generaPdfValidoConDetalleYTotales() throws IOException {
        VentaDTO venta = venta("VEN-00001", "Vestido floreado", 1000.0f, 160.0f, 1160.0f);
        GestorVentasFalso ventas = new GestorVentasFalso(List.of(venta));
        GestorReportes gestor = new GestorReportes(ventas);

        byte[] pdf = gestor.generarReportePdf(PeriodoReporte.DIA, LocalDate.of(2026, 11, 18));

        assertTrue(new String(pdf, 0, 5, StandardCharsets.US_ASCII).startsWith("%PDF-"));
        assertEquals(LocalDate.of(2026, 11, 18).atStartOfDay(), ventas.inicio);
        assertEquals(LocalDate.of(2026, 11, 19).atStartOfDay(), ventas.fin);
        try (PDDocument documento = Loader.loadPDF(pdf)) {
            assertEquals(1, documento.getNumberOfPages());
            String texto = new PDFTextStripper().getText(documento);
            assertTrue(texto.contains("Reporte de ventas"));
            assertTrue(texto.contains("18/11/2026"));
            assertTrue(texto.contains("VEN-00001"));
            assertTrue(texto.contains("Vestido floreado"));
            assertTrue(texto.contains("$1,000.00"));
            assertTrue(texto.contains("$160.00"));
            assertTrue(texto.contains("$1,160.00"));
        }
    }

    @Test
    void calculaIntervalosDeSemanaYMesCalendario() {
        GestorVentasFalso ventas = new GestorVentasFalso(List.of());
        GestorReportes gestor = new GestorReportes(ventas);
        LocalDate fechaReferencia = LocalDate.of(2026, 11, 18);

        gestor.generarReportePdf(PeriodoReporte.SEMANA, fechaReferencia);
        assertEquals(LocalDate.of(2026, 11, 16).atStartOfDay(), ventas.inicio);
        assertEquals(LocalDate.of(2026, 11, 23).atStartOfDay(), ventas.fin);

        gestor.generarReportePdf(PeriodoReporte.MES, fechaReferencia);
        assertEquals(LocalDate.of(2026, 11, 1).atStartOfDay(), ventas.inicio);
        assertEquals(LocalDate.of(2026, 12, 1).atStartOfDay(), ventas.fin);
    }

    @Test
    void generaPdfLegibleSinVentas() throws IOException {
        GestorReportes gestor = new GestorReportes(new GestorVentasFalso(List.of()));

        byte[] pdf = gestor.generarReportePdf(PeriodoReporte.MES, LocalDate.of(2026, 11, 18));

        try (PDDocument documento = Loader.loadPDF(pdf)) {
            String texto = new PDFTextStripper().getText(documento);
            assertTrue(texto.contains("No se encontraron ventas en este periodo."));
            assertTrue(texto.contains("$0.00"));
        }
    }

    @Test
    void divideUnReporteLargoEnVariasPaginas() throws IOException {
        List<VentaDTO> muchasVentas = new ArrayList<>();
        for (int i = 1; i <= 35; i++) {
            muchasVentas.add(venta(String.format("VEN-%05d", i), "Blusa", 100.0f, 16.0f, 116.0f));
        }
        GestorReportes gestor = new GestorReportes(new GestorVentasFalso(muchasVentas));

        byte[] pdf = gestor.generarReportePdf(PeriodoReporte.DIA, LocalDate.of(2026, 11, 18));

        try (PDDocument documento = Loader.loadPDF(pdf)) {
            assertTrue(documento.getNumberOfPages() > 1);
            String texto = new PDFTextStripper().getText(documento);
            assertTrue(texto.contains("VEN-00035"));
            assertTrue(texto.contains("Pagina 2"));
        }
    }

    @Test
    void rechazaPeriodoOFechaNulos() {
        GestorReportes gestor = new GestorReportes(new GestorVentasFalso(List.of()));

        assertThrows(IllegalArgumentException.class,
                () -> gestor.generarReportePdf(null, LocalDate.now()));
        assertThrows(IllegalArgumentException.class,
                () -> gestor.generarReportePdf(PeriodoReporte.DIA, null));
    }

    private static VentaDTO venta(String folio, String producto, float subtotal, float iva, float total) {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 11, 18, 10, 30);
        DetalleVentaDTO detalle = new DetalleVentaDTO(
                1, 1, 0, producto, null, 1, subtotal, subtotal);
        return new VentaDTO(1, folio, fechaHora, subtotal, iva, total, 1,
                List.of(detalle), List.of());
    }

    private static final class GestorVentasFalso implements IGestorVentas {

        private final List<VentaDTO> resultado;
        private LocalDateTime inicio;
        private LocalDateTime fin;

        private GestorVentasFalso(List<VentaDTO> resultado) {
            this.resultado = new ArrayList<>(resultado);
        }

        @Override
        public VentaDTO registrarVenta(VentaDTO venta) {
            return venta;
        }

        @Override
        public VentaDTO obtenerVentaPorId(int id) {
            return null;
        }

        @Override
        public VentaDTO obtenerVentaPorFolio(String folio) {
            return null;
        }

        @Override
        public List<VentaDTO> obtenerVentasEntre(LocalDateTime inicio, LocalDateTime fin) {
            this.inicio = inicio;
            this.fin = fin;
            return resultado;
        }

        @Override
        public ResumenVentaDTO calcularTotales(
                List<DetalleVentaDTO> detalles, float descuento) {
            return new ResumenVentaDTO();
        }

        @Override
        public boolean cancelarVenta(int idVenta) {
            return false;
        }
    }
}
