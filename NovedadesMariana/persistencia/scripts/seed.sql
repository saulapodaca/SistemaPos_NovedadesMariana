-- Seed de desarrollo para el esquema creado a partir de las entidades JPA.
-- Ejecutar el archivo completo en MySQL Workbench después de crear las tablas.
-- Carga única. Los IDs 900001 en adelante quedan reservados para estos datos.
-- Una colisión de ID, usuario, SKU o nombre revierte toda la carga.
-- Requiere permisos para crear/eliminar esta rutina temporal de carga.
-- GestorSesion.iniciarSesionMock utiliza Usuarios.id = 900001.
-- Las existencias finales ya consideran las tres ventas de ejemplo.

USE novedadesmariana;
SET NAMES utf8mb4;

DROP PROCEDURE IF EXISTS seed_demo_novedadesmariana;

DELIMITER $$

CREATE PROCEDURE seed_demo_novedadesmariana()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    INSERT INTO Usuarios
        (id, usuario, contrasenia, nombres, primerApellido, segundoApellido, rolEmpleado)
    VALUES
        (900001, 'demo.diana', 'Demo1234', 'Diana', 'González', 'López', 'EMPLEADO'),
        (900002, 'demo.mariana', 'Demo1234', 'Mariana', 'Ramírez', 'Soto', 'ADMINISTRADORA'),
        (900003, 'demo.carlos', 'Demo1234', 'Carlos', 'Pérez', 'Ruiz', 'EMPLEADO');

    INSERT INTO Categorias (id, nombre, descripcion)
    VALUES
        (900001, 'Ropa bebé', 'Prendas para bebés y primeros años'),
        (900002, 'Niña', 'Ropa infantil para niña'),
        (900003, 'Niño', 'Ropa infantil para niño'),
        (900004, 'Invierno', 'Prendas para temporada de frío'),
        (900005, 'Conjuntos', 'Conjuntos de prendas y pijamas'),
        (900006, 'Verano', 'Prendas frescas para verano');

    INSERT INTO Productos
        (id, sku, codigo_barras, nombre, descripcion, precio, tipoPrenda)
    VALUES
        (900001, 'DEMO-001', '7500000900001', 'Conjunto Pijama Unicornio', 'Pijama infantil de algodón', 185.00, 'CONJUNTO'),
        (900002, 'DEMO-002', '7500000900002', 'Vestido Floral Niña', 'Vestido con estampado floral', 220.00, 'VESTIDO'),
        (900003, 'DEMO-003', '7500000900003', 'Playera Dinosaurio', 'Playera infantil de manga corta', 120.00, 'PLAYERA'),
        (900004, 'DEMO-004', '7500000900004', 'Camisa Cuadros Azul', 'Camisa infantil de cuadros', 180.00, 'CAMISA'),
        (900005, 'DEMO-005', '7500000900005', 'Chamarra Sudadera Rosa', 'Sudadera con capucha y cierre', 320.00, 'SUDADERA'),
        (900006, 'DEMO-006', '7500000900006', 'Blusa Mariposas', 'Blusa de algodón estampada', 150.00, 'BLUSA'),
        (900007, 'DEMO-007', '7500000900007', 'Pantalón Mezclilla', 'Pantalón con cintura ajustable', 280.00, 'PANTALON'),
        (900008, 'DEMO-008', '7500000900008', 'Suéter Tejido Crema', 'Suéter infantil de tejido suave', 250.00, 'SUETER'),
        (900009, 'DEMO-009', '7500000900009', 'Short Marino', 'Short infantil con bolsillos', 135.00, 'SHORT'),
        (900010, 'DEMO-010', '7500000900010', 'Falda Estrellas', 'Falda con estampado de estrellas', 165.00, 'FALDA'),
        (900011, 'DEMO-011', '7500000900011', 'Conjunto Osito Bebé', 'Conjunto de dos piezas para bebé', 210.00, 'CONJUNTO'),
        (900012, 'DEMO-012', '7500000900012', 'Playera Arcoíris', 'Playera infantil de colores', 125.00, 'PLAYERA');

    INSERT INTO producto_categoria (idProducto, idCategoria)
    VALUES
        (900001, 900002), (900001, 900005),
        (900002, 900002), (900002, 900006),
        (900003, 900003), (900003, 900006),
        (900004, 900003),
        (900005, 900002), (900005, 900004),
        (900006, 900002), (900006, 900006),
        (900007, 900003),
        (900008, 900001), (900008, 900004),
        (900009, 900003), (900009, 900006),
        (900010, 900002),
        (900011, 900001), (900011, 900005),
        (900012, 900002), (900012, 900006);

    -- Tres tallas por producto. El conjunto para bebé usa tallas en meses.
    INSERT INTO ProductosTallas (id, talla, stockActual, idProducto)
    SELECT
        900001 + (p.id - 900001) * 3 + t.posicion,
        CASE
            WHEN p.id = 900011 THEN
                CASE t.posicion WHEN 0 THEN '6M' WHEN 1 THEN '12M' ELSE '18M' END
            ELSE t.talla
        END,
        10,
        p.id
    FROM Productos p
    CROSS JOIN (
        SELECT 0 AS posicion, '2T' AS talla
        UNION ALL SELECT 1, '4T'
        UNION ALL SELECT 2, '6T'
    ) t
    WHERE p.id BETWEEN 900001 AND 900012;

    INSERT INTO Pagos
        (id, metodoPago, monto, montoRecibido, cambio, fechaHora)
    VALUES
        (900001, 'EFECTIVO', 684.40, 700.00, 15.60, '2026-11-10 10:15:00'),
        (900002, 'TARJETA', 545.20, 545.20, 0.00, '2026-11-10 12:30:00'),
        (900003, 'TRANSFERENCIA', 522.00, 522.00, 0.00, '2026-11-10 16:45:00');

    -- La tercera venta incluye un descuento del 10% sobre el subtotal.
    -- El modelo actual conserva subtotal, IVA y total; el motivo no tiene columna.
    INSERT INTO Ventas
        (id, folio, fechaHora, subtotal, iva, total, idUsuario, idPago)
    VALUES
        (900001, 'DEMO-VEN-001', '2026-11-10 10:15:00', 590.00, 94.40, 684.40, 900001, 900001),
        (900002, 'DEMO-VEN-002', '2026-11-10 12:30:00', 470.00, 75.20, 545.20, 900003, 900002),
        (900003, 'DEMO-VEN-003', '2026-11-10 16:45:00', 500.00, 72.00, 522.00, 900001, 900003);

    INSERT INTO DetallesVentas
        (id, cantidad, precioUnitario, subtotal, idVenta, idProducto)
    VALUES
        (900001, 2, 185.00, 370.00, 900001, 900001),
        (900002, 1, 220.00, 220.00, 900001, 900002),
        (900003, 1, 320.00, 320.00, 900002, 900005),
        (900004, 1, 150.00, 150.00, 900002, 900006),
        (900005, 2, 250.00, 500.00, 900003, 900008);

    -- DetalleVenta referencia al producto. Estas son las tallas vendidas en el seed.
    UPDATE ProductosTallas SET stockActual = stockActual - 2
    WHERE (idProducto = 900001 AND talla = '2T')
       OR (idProducto = 900008 AND talla = '4T');

    UPDATE ProductosTallas SET stockActual = stockActual - 1
    WHERE (idProducto = 900002 AND talla = '4T')
       OR (idProducto = 900005 AND talla = '6T')
       OR (idProducto = 900006 AND talla = '2T');

    COMMIT;
END$$

DELIMITER ;

CALL seed_demo_novedadesmariana();
DROP PROCEDURE seed_demo_novedadesmariana;

-- Consultas para comprobar los datos cargados.
SELECT id, usuario, nombres, primerApellido, rolEmpleado
FROM Usuarios WHERE id BETWEEN 900001 AND 900003;

SELECT p.sku, p.codigo_barras, p.nombre, p.precio, t.talla, t.stockActual
FROM Productos p
JOIN ProductosTallas t ON t.idProducto = p.id
WHERE p.id BETWEEN 900001 AND 900012
ORDER BY p.id, t.id;

SELECT folio, subtotal, iva, total
FROM Ventas WHERE id BETWEEN 900001 AND 900003;