package com.itson.facturacion;

import conexion.ConexionJPA;
import dominio.Venta;
import dtos.DatosFiscalesDTO;
import jakarta.persistence.LockModeType;
import java.util.Locale;

/** Captura la solicitud para su procesamiento fiscal posterior. */
public final class ServicioFacturacion {
    private static String campo(String valor, String patron, String etiqueta) {
        String texto = valor == null ? "" : valor.trim();
        if (!texto.matches(patron)) throw new IllegalArgumentException("Revisa " + etiqueta + ".");
        return texto;
    }

    public boolean solicitar(int idVenta, DatosFiscalesDTO datos) {
        if (datos == null) throw new IllegalArgumentException("Captura los datos fiscales del cliente.");
        String rfc = campo(datos.getRfc(), "(?iu)[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}", "el RFC").toUpperCase(Locale.ROOT);
        String razon = campo(datos.getRazonSocial(), ".{1,254}", "el nombre o razón social");
        String cp = campo(datos.getCodigoPostal(), "[0-9]{5}", "el código postal fiscal");
        String regimen = campo(datos.getRegimenFiscal(), "[0-9]{3}", "la clave del régimen fiscal");
        String uso = campo(datos.getUsoCfdi(), "(?i)[A-Z]{1,2}[0-9]{1,2}", "la clave de uso del CFDI").toUpperCase(Locale.ROOT);
        if (uso.length() != 3) throw new IllegalArgumentException("El uso de CFDI debe tener tres caracteres.");
        String correo = datos.getCorreo() == null ? "" : datos.getCorreo().trim();
        if (!correo.isEmpty() && (correo.length() > 254 || !correo.matches("[^ @]+@[^ @]+\\.[^ @]+")))
            throw new IllegalArgumentException("Revisa el correo de entrega.");
        try (var em = ConexionJPA.getInstance().getEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            try {
                if (em.find(Venta.class, (long) idVenta, LockModeType.PESSIMISTIC_WRITE) == null)
                    throw new IllegalArgumentException("La venta no existe.");
                var previas = em.createNativeQuery("SELECT idVenta FROM SolicitudesFactura WHERE idVenta = ?")
                        .setParameter(1, idVenta).getResultList();
                if (!previas.isEmpty()) throw new IllegalStateException("Esta venta ya tiene una solicitud de factura.");
                var insertar = em.createNativeQuery("""
                        INSERT INTO SolicitudesFactura
                        (idVenta, rfc, razonSocial, codigoPostal, regimenFiscal, usoCfdi, correo,
                         rfcEmisor, nombreEmisor, regimenEmisor, lugarExpedicion)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """);
                Object[] valores = {idVenta, rfc, razon, cp, regimen, uso, correo,
                        System.getenv("POS_EMISOR_RFC"), System.getenv("POS_EMISOR_NOMBRE"),
                        System.getenv("POS_EMISOR_REGIMEN"), System.getenv("POS_EMISOR_CP")};
                for (int i = 0; i < valores.length; i++) insertar.setParameter(i + 1, valores[i]);
                insertar.executeUpdate();
                tx.commit();
                return true;
            } finally {
                if (tx.isActive()) tx.rollback();
            }
        } catch (jakarta.persistence.PersistenceException ex) {
            throw new IllegalStateException("No se pudo guardar la solicitud. Revisa la conexión y la tabla SolicitudesFactura.", ex);
        }
    }
}
