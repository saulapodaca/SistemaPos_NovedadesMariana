package com.itson.tickets;

import dtos.*;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeneradorTicketTest {
    @Test
    void conservaImportesDelCobroConDescuento() {
        var fecha = LocalDateTime.of(2026, 11, 25, 10, 30);
        var detalle = new DetalleVentaDTO(1, 1, 1, "Playera", "M", 1, 100, 100);
        var pago = new PagoDTO(1, 1, "EFECTIVO", 104.4f, 110, 5.6f, fecha);
        var venta = new VentaDTO(1, "V-1", fecha, 100, 14.4f, 104.4f, 1, List.of(detalle), List.of(pago));
        var lineas = new GeneradorTicket().lineas(venta);
        assertTrue(lineas.contains("Folio: V-1"));
        assertTrue(lineas.contains("Playera · M"));
        assertTrue(lineas.contains("Descuento: $10.00"));
        assertTrue(lineas.contains("TOTAL: $104.40"));
        assertTrue(lineas.contains("Cambio: $5.60"));
    }
}
