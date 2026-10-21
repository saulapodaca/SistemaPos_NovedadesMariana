
package implementaciones;

import dominio.DetalleVenta;
import dominio.MetodoPago;
import dominio.Pago;
import dominio.Producto;
import dominio.RolEmpleado;
import dominio.TipoPrenda;
import dominio.Usuario;
import dominio.Venta;
import entityManager.ConexionJPA;
import interfaces.IDetalleVentaDAO;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DetalleVentaDAOTest {

    private static IDetalleVentaDAO detalleVentaDAO;

    private static Long idPrueba;
    private static Long idUsuario;
    private static Long idProducto;
    private static Long idVenta;
    private static Long idPago;

    private static Producto productoPrueba;
    private static Venta ventaPrueba;

    @BeforeAll
    public static void setUp() {

        detalleVentaDAO = new DetalleVentaDAO();

        EntityManager em = ConexionJPA
                .getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            String codigo = UUID.randomUUID()
                    .toString().replace("-", "");

            // Crear usuario de prueba
            Usuario usuario = new Usuario(
                "test_" + codigo,
                "HASH_SOLO_PARA_PRUEBAS",
                "Usuario",
                "Prueba",
                null,
                RolEmpleado.EMPLEADO
            );

            em.persist(usuario);

            // Crear producto de prueba
            productoPrueba = new Producto(
                "SKU-" + codigo,
                "Camisa de prueba",
                "Producto para pruebas DAO",
                250.0f,
                TipoPrenda.CAMISA
            );

            em.persist(productoPrueba);

            // Crear pago de prueba
            Pago pago = new Pago(
                MetodoPago.EFECTIVO,
                500.0f,
                500.0f,
                0.0f,
                LocalDateTime.now()
            );

            // Crear venta de prueba
            ventaPrueba = new Venta(
                "V-" + codigo,
                LocalDateTime.now(),
                500.0f,
                0.0f,
                500.0f,
                usuario,
                pago
            );

            em.persist(ventaPrueba);
            em.getTransaction().commit();

            // Guardar identificadores
            idUsuario = usuario.getId();
            idProducto = productoPrueba.getId();
            idVenta = ventaPrueba.getId();
            idPago = pago.getId();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    @Test
    @Order(1)
    public void testGuardar() throws Exception {

        DetalleVenta detalle = new DetalleVenta(
            2,
            250.0f,
            500.0f,
            productoPrueba
        );

        detalle.setVenta(ventaPrueba);

        detalleVentaDAO.guardar(detalle);

        assertNotNull(
            detalle.getId(),
            "El detalle debe generar un ID."
        );

        idPrueba = detalle.getId();
    }

    @Test
    @Order(2)
    public void testBuscarPorId() {

        assertNotNull(idPrueba);

        DetalleVenta detalle =
                detalleVentaDAO.buscarPorId(idPrueba);

        assertNotNull(
            detalle,
            "El detalle debe existir."
        );

        assertEquals(2, detalle.getCantidad());

        assertEquals(
            250.0f,
            detalle.getPrecioUnitario(),
            0.001f
        );

        assertEquals(
            500.0f,
            detalle.getSubtotal(),
            0.001f
        );
    }

    @Test
    @Order(3)
    public void testActualizar() throws Exception {

        DetalleVenta detalle =
                detalleVentaDAO.buscarPorId(idPrueba);

        assertNotNull(detalle);

        detalle.setCantidad(3);
        detalle.setSubtotal(750.0f);

        detalleVentaDAO.actualizar(detalle);

        DetalleVenta actualizado =
                detalleVentaDAO.buscarPorId(idPrueba);

        assertNotNull(actualizado);

        assertEquals(
            3,
            actualizado.getCantidad()
        );

        assertEquals(
            750.0f,
            actualizado.getSubtotal(),
            0.001f
        );
    }

    @Test
    @Order(4)
    public void testObtenerTodos() {

        List<DetalleVenta> detalles =
                detalleVentaDAO.obtenerTodos();

        assertNotNull(detalles);

        assertTrue(
            detalles.stream().anyMatch(
                d -> idPrueba.equals(d.getId())
            ),
            "La lista debe contener el detalle registrado."
        );
    }

    @Test
    @Order(5)
    public void testEliminar() throws Exception {

        assertNotNull(idPrueba);

        detalleVentaDAO.eliminar(idPrueba);

        DetalleVenta eliminado =
                detalleVentaDAO.buscarPorId(idPrueba);

        assertNull(
            eliminado,
            "El detalle debe haberse eliminado."
        );
    }

    @AfterAll
    public static void limpiarDatos() {

        EntityManager em = ConexionJPA
                .getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            // Eliminar venta y sus detalles
            if (idVenta != null) {
                Venta venta = em.find(
                    Venta.class, idVenta
                );

                if (venta != null) {
                    em.remove(venta);
                }
            }

            em.flush();

            // Eliminar pago
            if (idPago != null) {
                Pago pago = em.find(
                    Pago.class, idPago
                );

                if (pago != null) {
                    em.remove(pago);
                }
            }

            // Eliminar producto
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
