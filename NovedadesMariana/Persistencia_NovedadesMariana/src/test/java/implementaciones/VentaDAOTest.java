
package implementaciones;

import dominio.DetalleVenta;
import dominio.MetodoPago;
import dominio.Pago;
import dominio.Producto;
import dominio.ProductoTalla;
import dominio.RolEmpleado;
import dominio.TipoPrenda;
import dominio.Usuario;
import dominio.Venta;
import entityManager.ConexionJPA;
import interfaces.IVentaDAO;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VentaDAOTest {

    private static IVentaDAO ventaDAO;
    private static VentaDAO implementacion;

    private static UsuarioDAO usuarioDAO;
    private static ProductoDAO productoDAO;
    private static ProductoTallaDAO productoTallaDAO;

    private static Usuario usuarioPrueba;
    private static Producto productoPrueba;

    private static Long idVenta;
    private static Long idPago;
    private static Long idUsuario;
    private static Long idProducto;
    private static Long idTallaM;
    private static Long idTallaS;

    private static String folioPrueba;

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

        idVenta = venta.getId();
        idPago = pago.getId();

        // Verificar identificadores generados
        assertNotNull(idVenta);
        assertNotNull(idPago);
        assertNotNull(detalleM.getId());
        assertNotNull(detalleS.getId());

        // Comprobar descuento de inventario
        ProductoTalla tallaM =
            productoTallaDAO.buscarPorId(idTallaM);

        ProductoTalla tallaS =
            productoTallaDAO.buscarPorId(idTallaS);

        assertEquals(
            4,
            tallaM.getStockActual().intValue()
        );

        assertEquals(
            7,
            tallaS.getStockActual().intValue()
        );
    }

    @Test
    @Order(2)
    public void testBuscarPorId() {

        assertNotNull(idVenta);

        Venta venta = ventaDAO.buscarPorId(idVenta);

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

    @Test
    @Order(3)
    public void testActualizar() {

        Venta venta = ventaDAO.buscarPorId(idVenta);

        assertNotNull(venta);

        // Las ventas registradas no se modifican
        assertThrows(
            Exception.class,
            () -> ventaDAO.actualizar(venta)
        );

        // Comprobar que conserva el total
        Venta actual = ventaDAO.buscarPorId(idVenta);

        assertEquals(
            1000.0f,
            actual.getTotal(),
            0.001f
        );
    }

    @Test
    @Order(4)
    public void testObtenerTodos() {

        List<Venta> ventas = ventaDAO.obtenerTodas();

        assertNotNull(ventas);

        assertTrue(
            ventas.stream().anyMatch(
                v -> idVenta.equals(v.getId())
            ),
            "La lista debe contener la venta registrada."
        );
    }

    @Test
    @Order(5)
    public void testStockInsuficiente() throws Exception {

        int stockAnterior = productoTallaDAO
            .buscarPorId(idTallaM)
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

        // El inventario debe permanecer intacto
        ProductoTalla tallaActual =
            productoTallaDAO.buscarPorId(idTallaM);

        assertEquals(
            stockAnterior,
            tallaActual.getStockActual().intValue()
        );
    }

    @Test
    @Order(6)
    public void testEliminar() throws Exception {

        assertNotNull(idVenta);

        // No permitir eliminar ventas registradas
        assertThrows(
            Exception.class,
            () -> ventaDAO.eliminar(idVenta)
        );

        // Verificar que la venta sigue existiendo
        Venta venta = ventaDAO.buscarPorId(idVenta);

        assertNotNull(
            venta,
            "La venta debe conservarse."
        );
    }

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
