# Tickets y solicitudes de factura

En la pantalla de venta completada, **Imprimir ticket** envía un trabajo a la cola de impresión del sistema. Instala el controlador de la impresora térmica y selecciona papel de rollo en sus preferencias. Configura las variables de entorno en el equipo que ejecuta el POS y reinicia la aplicación.

| Variable | Valor |
| --- | --- |
| POS_IMPRESORA_TICKET | Nombre de la cola instalada en Windows. Si se omite se usa la impresora predeterminada. |
| POS_TICKET_MM | Ancho del rollo en milímetros. Admite 58 u 80. Predeterminado 80. |
| POS_EMISOR_RFC | RFC del negocio. |
| POS_EMISOR_NOMBRE | Nombre o razón social del emisor. |
| POS_EMISOR_REGIMEN | Clave de régimen fiscal del emisor. |
| POS_EMISOR_CP | Código postal del lugar de expedición. |

En MySQL selecciona el esquema del POS y ejecuta `persistencia/scripts/crear_solicitudes_factura.sql` antes de usar **Solicitar factura**. Para correr la suite, ejecuta también el script en `novedadesmariana_test`.

Caja captura los datos del cliente sobre la venta cobrada. La solicitud queda pendiente, vinculada a la venta y a sus conceptos. Los campos opcionales del emisor se copian con los valores configurados en el momento de la solicitud. Pueden completarse al procesar las solicitudes si aún estaban vacíos.

La persona que factura puede consultar los pendientes en MySQL:

```sql
SELECT v.folio, v.fechaHora, v.subtotal, v.iva, v.total, s.*
FROM SolicitudesFactura s
JOIN Ventas v ON v.id = s.idVenta
WHERE s.estado = 'PENDIENTE'
ORDER BY s.fechaSolicitud;
```

El timbrado automático requiere elegir el PAC, integrar su API de pruebas, configurar las credenciales del emisor y mapear los productos, unidades, impuestos y pagos a los catálogos fiscales. La solicitud pendiente es un registro interno. El XML timbrado y el UUID deben provenir del proceso de emisión.
