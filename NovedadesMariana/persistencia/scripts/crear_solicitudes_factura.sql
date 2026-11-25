CREATE TABLE IF NOT EXISTS SolicitudesFactura (
    idVenta BIGINT NOT NULL PRIMARY KEY,
    fechaSolicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE',
    rfc VARCHAR(13) NOT NULL,
    razonSocial VARCHAR(254) NOT NULL,
    codigoPostal CHAR(5) NOT NULL,
    regimenFiscal CHAR(3) NOT NULL,
    usoCfdi CHAR(3) NOT NULL,
    correo VARCHAR(254),
    rfcEmisor VARCHAR(13),
    nombreEmisor VARCHAR(254),
    regimenEmisor CHAR(3),
    lugarExpedicion CHAR(5),
    CONSTRAINT fk_solicitud_factura_venta FOREIGN KEY (idVenta) REFERENCES Ventas(id)
);
