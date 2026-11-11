package daos;

import dominio.DetalleVenta;
import enums.MetodoPago;
import dominio.Pago;
import dominio.Producto;
import dominio.ProductoTalla;
import enums.RolEmpleado;
import enums.TipoPrenda;
import dominio.Usuario;
import dominio.Venta;
import conexion.ConexionJPA;
import interfaces.IVentaDAO;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para las operaciones de {@link IVentaDAO}, sus
 * relaciones con pago y detalles, y el descuento transaccional del inventario.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VentaDAOTest {

   /** Interfaz de persistencia utilizada por las pruebas CRUD. */
   private static IVentaDAO ventaDAO;

   /** Implementación concreta, necesaria para registrar ventas con tallas. */
   private static VentaDAO implementacion;

   /** DAO auxiliar para crear el usuario asociado a la venta. */
   private static UsuarioDAO usuarioDAO;

   /** DAO auxiliar para crear y limpiar el producto. */
   private static ProductoDAO productoDAO;

   /** DAO usado para inspeccionar las existencias de cada talla. */
   private static ProductoTallaDAO productoTallaDAO;

   /** Usuario auxiliar asociado a la venta de prueba. */
   private static Usuario usuarioPrueba;

   /** Producto auxiliar vendido en la prueba. */
   private static Producto productoPrueba;

   /** ID de la venta registrada. */
   private static Long idVenta;

   /** ID del pago asociado a la venta. */
   private static Long idPago;

   /** ID del usuario auxiliar. */
   private static Long idUsuario;

   /** ID del producto auxiliar. */
   private static Long idProducto;

   /** ID de la talla M. */
   private static Long idTallaM;

   /** ID de la talla S. */
   private static Long idTallaS;

   /** Folio único de la venta de prueba. */
   private static String folioPrueba;

   /** Prepara un usuario, un producto y sus tallas para las pruebas de venta. */
   @BeforeAll
   public static void setUp() throws Exception {

        implementacion = new VentaDAO();
        ventaDAO = implementacion;

        usuarioDAO = new UsuarioDAO();
        productoDAO = new ProductoDAO();
        productoTallaDAO = new ProductoTallaDAO();

        String codigo = UUID.randomUUID()
                .toString().substring(0, 8);

        folioPrueba = "V-TEST-" + codigo;

        // Crear usuario de prueba
        usuarioPrueba = new Usuario(
            "empleado_" + codigo,
            "HASH_SIMULADO_SOLO_PRUEBAS",
            "Juan",
            "Lopez",
            "Perez",
            RolEmpleado.EMPLEADO
        );

        usuarioDAO.guardar(usuarioPrueba);
        idUsuario = usuarioPrueba.getId();

        // Crear producto de prueba
        productoPrueba = new Producto(
            "CAM-" + codigo,
            "BARCODE-" + codigo,
            "Camisa Nike",
            "Camisa de prueba",
            500.0f,
            TipoPrenda.CAMISA
        );

        // Agregar tallas
        productoPrueba.agregarTalla(
            new ProductoTalla(
                "M", 5, productoPrueba
            )
        );

        productoPrueba.agregarTalla(
            new ProductoTalla(
                "S", 8, productoPrueba
            )
        );

        productoDAO.guardar(productoPrueba);
        idProducto = productoPrueba.getId();

        // Obtener identificadores de tallas
        for (ProductoTalla talla
                : productoPrueba.getTallas()) {

            if (talla.getTalla().equals("M")) {
                idTallaM = talla.getId();
            }

            if (talla.getTalla().equals("S")) {
                idTallaS = talla.getId();
            }
        }

        assertNotNull(idUsuario);
        assertNotNull(idProducto);
        assertNotNull(idTallaM);
        assertNotNull(idTallaS);
   }

   /** Registra una venta, guarda sus detalles y comprueba el descuento de stock. */
   @Test
   @Order(1)
   public void testGuardar() throws Exception {

        // Crear pago
        Pago pago = new Pago(
            MetodoPago.EFECTIVO,
            1000.0f,
            1000.0f,
            0.0f,
            LocalDateTime.now()
        );

        // Crear venta
        Venta venta = new Venta(
            folioPrueba,
            LocalDateTime.now(),
            1000.0f,
            0.0f,
            1000.0f,
            usuarioPrueba,
            pago
        );

        // Camisa Nike talla M
        DetalleVenta detalleM = new DetalleVenta(
            1,
            500.0f,
            500.0f,
            productoPrueba
        );

        // Camisa Nike talla S
        DetalleVenta detalleS = new DetalleVenta(
            1,
            500.0f,
            500.0f,
            productoPrueba
        );

        venta.agregarDetalle(detalleM);
        venta.agregarDetalle(detalleS);

        // Asociar cada detalle con su talla
        Map<DetalleVenta, Long> tallas = new HashMap<>();

        tallas.put(detalleM, idTallaM);
        tallas.put(detalleS, idTallaS);

        // Registrar venta y descontar inventario
        implementacion.guardarConTallas(
            venta, tallas
        );

        // 🔴 BREAKPOINT 1: Revisar IDs generados y stock después del registro.
        idVenta = venta.getId();
        idPago = pago.getId();

        // Verificar identificadores generados
        assertNotNull(idVenta);
        assertNotNull(idPago);
        assertNotNull(detalleM.getId());
        assertNotNull(detalleS.getId());

        // Comprobar descuento de inventario (CORREGIDO: buscarProductoTallaPorId)
        ProductoTalla tallaM =
            productoTallaDAO.buscarProductoTallaPorId(idTallaM);

        ProductoTalla tallaS =
            productoTallaDAO.buscarProductoTallaPorId(idTallaS);

        assertNotNull(tallaM, "La talla M debe existir.");
        assertNotNull(tallaS, "La talla S debe existir.");
        System.out.println("========================================");
        System.out.println("💾 [TEST GUARDAR] Venta registrada:");
        System.out.println("   - ID: " + idVenta);
        System.out.println("   - Folio: " + venta.getFolio());
        System.out.println("   - Pago: " + idPago);
        System.out.println("   - Detalles: " + venta.getDetalles().size());
        System.out.println("   - Stock talla M: " + tallaM.getStockActual());
        System.out.println("   - Stock talla S: " + tallaS.getStockActual());
        System.out.println("========================================");
        assertEquals(
            4,
            tallaM.getStockActual().intValue()
        );

        assertEquals(
            7,
            tallaS.getStockActual().intValue()
        );
   }

   /** Busca una venta y valida sus datos y asociaciones principales. */
   @Test
   @Order(2)
   public void testBuscarPorId() {

        assertNotNull(idVenta);

        Venta venta = ventaDAO.buscarVentaPorId(idVenta);

        assertNotNull(venta, "La venta debe existir.");
        System.out.println("========================================");
        System.out.println("🔍 [TEST BUSCAR POR ID] Venta encontrada:");
        System.out.println("   - ID: " + venta.getId());
        System.out.println("   - Folio: " + venta.getFolio());
        System.out.println("   - Total: $" + venta.getTotal());
        System.out.println("   - Usuario: " + venta.getUsuario().getId());
        System.out.println("   - Método de pago: " + venta.getPago().getMetodoPago());
        System.out.println("   - Detalles: " + venta.getDetalles().size());
        System.out.println("========================================");
        assertNotNull(
            venta,
            "La venta debe existir."
        );

        assertEquals(
            folioPrueba,
            venta.getFolio()
        );

        assertEquals(
            1000.0f,
            venta.getTotal(),
            0.001f
        );

        // Verificar usuario
        assertEquals(
            idUsuario,
            venta.getUsuario().getId()
        );

        // Verificar pago
        assertNotNull(venta.getPago());

        assertEquals(
            MetodoPago.EFECTIVO,
            venta.getPago().getMetodoPago()
        );

        // Verificar detalles
        assertEquals(
            2,
            venta.getDetalles().size()
        );

        for (DetalleVenta detalle
                : venta.getDetalles()) {

            assertEquals(
                idProducto,
                detalle.getProducto().getId()
            );

            assertEquals(1, detalle.getCantidad());
        }
   }

   /** Comprueba que no se permita modificar una venta ya registrada. */
   @Test
   @Order(3)
   public void testActualizar() {

        Venta venta = ventaDAO.buscarVentaPorId(idVenta);

        assertNotNull(venta);

        // Las ventas registradas no se modifican
        // 🔴 BREAKPOINT 2: Inspeccionar la venta antes de comprobar el rechazo.
        assertThrows(
            Exception.class,
            () -> ventaDAO.actualizar(venta)
        );

        // Comprobar que conserva el total
        Venta actual = ventaDAO.buscarVentaPorId(idVenta);

        assertNotNull(actual);
        System.out.println("⚠️ [TEST ACTUALIZAR] Venta conserva el total: $" + actual.getTotal());
        assertEquals(
            1000.0f,
            actual.getTotal(),
            0.001f
        );
   }

   /** Obtiene todas las ventas y verifica que incluya la venta de prueba. */
   @Test
   @Order(4)
   public void testObtenerTodos() {

        List<Venta> ventas = ventaDAO.obtenerTodas();
        assertNotNull(ventas);

        System.out.println("========================================");
        System.out.println("📋 [TEST OBTENER TODOS] Total de registros: " + ventas.size());
        for (Venta v : ventas) {
            System.out.println("   -> [" + v.getId() + "] " + v.getFolio()
                    + " | Total: $" + v.getTotal());
        }
        System.out.println("========================================");
        assertTrue(
            ventas.stream().anyMatch(
                v -> idVenta.equals(v.getId())
            ),
            "La lista debe contener la venta registrada."
        );
   }

   /** Valida que una venta sin stock suficiente falle sin alterar el inventario. */
   @Test
   @Order(5)
   public void testStockInsuficiente() throws Exception {

        // CORREGIDO: buscarProductoTallaPorId
        int stockAnterior = productoTallaDAO
            .buscarProductoTallaPorId(idTallaM)
            .getStockActual();

        int cantidad = stockAnterior + 1;
        float importe = cantidad * 500.0f;

        Pago pago = new Pago(
            MetodoPago.EFECTIVO,
            importe,
            importe,
            0.0f,
            LocalDateTime.now()
        );

        Venta venta = new Venta(
            folioPrueba + "-SIN-STOCK",
            LocalDateTime.now(),
            importe,
            0.0f,
            importe,
            usuarioPrueba,
            pago
        );

        DetalleVenta detalle = new DetalleVenta(
            cantidad,
            500.0f,
            importe,
            productoPrueba
        );

        venta.agregarDetalle(detalle);

        Map<DetalleVenta, Long> tallas = new HashMap<>();
        tallas.put(detalle, idTallaM);

        // Debe rechazar la venta
        assertThrows(
            Exception.class,
            () -> implementacion.guardarConTallas(
                venta, tallas
            )
        );

        // 🔴 BREAKPOINT 3: Confirmar que el fallo de registro revirtió la transacción.
        // El inventario debe permanecer intacto (CORREGIDO: buscarProductoTallaPorId)
        ProductoTalla tallaActual =
            productoTallaDAO.buscarProductoTallaPorId(idTallaM);

        assertNotNull(tallaActual);
        System.out.println("⚠️ [TEST STOCK INSUFICIENTE] Stock conservado: "
                + tallaActual.getStockActual() + " (anterior: " + stockAnterior + ")");
        assertEquals(
            stockAnterior,
            tallaActual.getStockActual().intValue()
        );
   }

   /** Comprueba que una venta registrada no se pueda eliminar. */
   @Test
   @Order(6)
   public void testEliminar() throws Exception {

        assertNotNull(idVenta);

        // No permitir eliminar ventas registradas
        // 🔴 BREAKPOINT 4: Revisar el ID antes de validar el rechazo de eliminación.
        assertThrows(
            Exception.class,
            () -> ventaDAO.eliminar(idVenta)
        );

        // Verificar que la venta sigue existiendo
        Venta venta = ventaDAO.buscarVentaPorId(idVenta);

        assertNotNull(venta, "La venta debe conservarse.");
        System.out.println("🗑️ [TEST ELIMINAR] Venta conservada: " + venta.getFolio());
        assertNotNull(
            venta,
            "La venta debe conservarse."
        );
   }

   /** Elimina las entidades auxiliares creadas por setUp. */
   @AfterAll
   public static void limpiarDatos() {

        EntityManager em = ConexionJPA
            .getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            // Eliminar venta de prueba
            if (idVenta != null) {
                Venta venta = em.find(
                    Venta.class, idVenta
                );

                if (venta != null) {
                    em.remove(venta);
                }
            }

            em.flush();

            // Eliminar pago de prueba
            if (idPago != null) {
                Pago pago = em.find(
                    Pago.class, idPago
                );

                if (pago != null) {
                    em.remove(pago);
                }
            }

            // Eliminar producto y sus tallas
            if (idProducto != null) {
                Producto producto = em.find(
                    Producto.class, idProducto
                );

                if (producto != null) {
                    em.remove(producto);
                }
            }

            // Eliminar usuario
            if (idUsuario != null) {
                Usuario usuario = em.find(
                    Usuario.class, idUsuario
                );

                if (usuario != null) {
                    em.remove(usuario);
                }
            }

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
   }
}