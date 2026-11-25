package com.itson.facturacion;

import conexion.ConexionJPA;
import dominio.*;
import dtos.DatosFiscalesDTO;
import enums.MetodoPago;
import enums.RolEmpleado;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServicioFacturacionTest {
    @Test
    void guardaUnaSolicitudPendientePorVenta() {
        var servicio = new ServicioFacturacion();
        String folio = UUID.randomUUID().toString();
        var ahora = LocalDateTime.now();
        var usuario = new Usuario(folio, "prueba", "Prueba", "Fiscal", "", RolEmpleado.EMPLEADO);
        var pago = new Pago(MetodoPago.EFECTIVO, 116f, 120f, 4f, ahora);
        var venta = new Venta(folio, ahora, 100f, 16f, 116f, usuario, pago);
        try (var em = ConexionJPA.getInstance().getEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            em.persist(usuario);
            em.persist(venta);
            tx.commit();
            try {
                var datos = new DatosFiscalesDTO("XAXX010101000", "CLIENTE PRUEBA", "83000", "616", "S01", "");
                assertTrue(servicio.solicitar(venta.getId().intValue(), datos));
                assertThrows(IllegalStateException.class, () -> servicio.solicitar(venta.getId().intValue(), datos));
                Object[] solicitud = (Object[]) em.createNativeQuery(
                        "SELECT estado, rfc FROM SolicitudesFactura WHERE idVenta = ?")
                        .setParameter(1, venta.getId()).getSingleResult();
                assertArrayEquals(new Object[]{"PENDIENTE", datos.getRfc()}, solicitud);
            } finally {
                tx.begin();
                em.createNativeQuery("DELETE FROM SolicitudesFactura WHERE idVenta = ?")
                        .setParameter(1, venta.getId()).executeUpdate();
                em.remove(venta);
                em.flush();
                em.remove(pago);
                em.remove(usuario);
                tx.commit();
            }
        }
    }
}
