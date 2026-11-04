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

/**
 * Clase de pruebas unitarias y de integración para validar el comportamiento de
 * la implementación de {@link IDetalleVentaDAO} utilizando JUnit 5. 
 * Las pruebas sejecutan en un orden secuencial estricto controlado por anotaciones.
 *
 * @author rauln
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DetalleVentaDAOTest {

    /**
     * Instancia del DAO de detalles de venta utilizada para realizar las operaciones
     * CRUD en las pruebas.
     */
    private static IDetalleVentaDAO detalleVentaDAO;

    /**
     * Identificador único del detalle de venta creado durante las pruebas para su
     * posterior consulta, actualización y eliminación.
     */
    private static Long idPrueba;

    /**
     * Identificador del usuario asociado a la venta de prueba.
     */
    private static Long idUsuario;

    /**
     * Identificador del producto asociado al detalle de venta.
     */
    private static Long idProducto;

    /**
     * Identificador de la venta asociada al detalle.
     */
    private static Long idVenta;

    /**
     * Identificador del pago asociado a la venta de prueba.
     */
    private static Long idPago;

    /**
     * Instancia del producto de prueba requerido para la relación del detalle.
     */
    private static Producto productoPrueba;

    /**
     * Instancia de la venta de prueba requerida para la relación del detalle.
     */
    private static Venta ventaPrueba;

    /**
     * Configuración inicial que se ejecuta una sola vez antes de todas las
     * pruebas. Inicializa los datos preliminares en la base de datos (Usuario, Producto, Pago y Venta).
     */
    @BeforeAll
    public static void setUp() {
        detalleVentaDAO = new DetalleVentaDAO();
        EntityManager em = ConexionJPA.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            String codigo = UUID.randomUUID().toString().replace("-", "");
            Usuario usuario = new Usuario(
                    "test_" + codigo,
                    "HASH_SOLO_PARA_PRUEBAS",
                    "Usuario",
                    "Prueba",
                    "",
                    RolEmpleado.EMPLEADO
            );
            em.persist(usuario);
            productoPrueba = new Producto(
                    "SKU-" + codigo,                 
                    "BARCODE-" + codigo,           
                    "Pantalon Mezclilla",          
                    "Pantalon azul clasico",       
                    450.00f,                       
                    TipoPrenda.PANTALON            
            );
            em.persist(productoPrueba);
            Pago pago = new Pago(
                    MetodoPago.EFECTIVO,
                    500.0f,
                    500.0f,
                    0.0f,
                    LocalDateTime.now()
            );
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

    /**
     * Prueba la funcionalidad de guardado (Create) de un nuevo detalle de venta.
     *
     * @throws Exception Si ocurre un error al persistir el detalle en la base
     * de datos de pruebas.
     */
    @Test
    @Order(1)
    public void testGuardar() throws Exception {
        // Crear el objeto DetalleVenta con los datos requeridos
        DetalleVenta detalle = new DetalleVenta(
                2,
                250.0f,
                500.0f,
                productoPrueba
        );
        detalle.setVenta(ventaPrueba);
        detalleVentaDAO.guardar(detalle);   
        // 🔴 BREAKPOINT 1: Coloca el punto de interrupción aquí
        assertNotNull(detalle.getId(), "El detalle debe generar un ID.");
        System.out.println("========================================");
        System.out.println("💾 [TEST GUARDAR] Detalle de venta creado:");
        System.out.println("   - ID: " + detalle.getId());
        System.out.println("   - Venta: " + detalle.getVenta().getFolio());
        System.out.println("   - Producto: " + detalle.getProducto().getNombre());
        System.out.println("   - Cantidad: " + detalle.getCantidad());
        System.out.println("   - Precio unitario: $" + detalle.getPrecioUnitario());
        System.out.println("   - Subtotal: $" + detalle.getSubtotal());
        System.out.println("========================================");
        idPrueba = detalle.getId();
    }

    /**
     * Prueba la funcionalidad de búsqueda por ID (Read) de un detalle existente.
     * Valida que el registro recuperado no sea nulo y que sus valores coincidan.
     */
    @Test
    @Order(2)
    public void testBuscarPorId() {
        assertNotNull(idPrueba);
        DetalleVenta detalle = detalleVentaDAO.buscarPorId(idPrueba);
        assertNotNull(detalle, "El detalle debe existir.");
        System.out.println("========================================");
        System.out.println("🔍 [TEST BUSCAR POR ID] Detalle de Venta encontrado:");
        System.out.println("   - ID: " + detalle.getId());
        System.out.println("   - Cantidad: " + detalle.getCantidad());
        System.out.println("   - Precio Unitario: " + detalle.getPrecioUnitario());
        System.out.println("   - Subtotal: " + detalle.getSubtotal());
        System.out.println("========================================");
        assertEquals(2, detalle.getCantidad());
        assertEquals(250.0f, detalle.getPrecioUnitario(), 0.001f);
        assertEquals(500.0f, detalle.getSubtotal(), 0.001f);
    }

    /**
     * Prueba la funcionalidad de actualización (Update) de un detalle de venta.
     * Modifica la cantidad y subtotal, y corrobora que se guarden correctamente.
     *
     * @throws Exception Si ocurre un error durante la actualización en la base de datos.
     */
    @Test
    @Order(3)
    public void testActualizar() throws Exception {
        DetalleVenta detalle = detalleVentaDAO.buscarPorId(idPrueba);
        assertNotNull(detalle);
        detalle.setCantidad(3);
        detalle.setSubtotal(750.0f);
        detalleVentaDAO.actualizar(detalle);
        // 🔴 BREAKPOINT 2: Coloca el punto de interrupción aquí.
        DetalleVenta actualizado = detalleVentaDAO.buscarPorId(idPrueba);
        assertNotNull(actualizado);
        System.out.println("✏️ [TEST ACTUALIZAR] Detalle consultado:");
        System.out.println("   - ID: " + actualizado.getId());
        System.out.println("   - Cantidad: " + actualizado.getCantidad());
        System.out.println("   - Precio unitario: $" + actualizado.getPrecioUnitario());
        System.out.println("   - Subtotal: $" + actualizado.getSubtotal());
        assertEquals(3, actualizado.getCantidad());
        assertEquals(750.0f, actualizado.getSubtotal(), 0.001f);
    }

    /**
     * Prueba la obtención de todos los registros registrados (Read All).
     * Verifica que la lista devuelta contenga el detalle de prueba creado previamente.
     */
    @Test
    @Order(4)
    public void testObtenerTodos() {
        List<DetalleVenta> detalles = detalleVentaDAO.obtenerTodos();
        assertNotNull(detalles);
        System.out.println("========================================");
        System.out.println("📋 [TEST OBTENER TODOS] Total de registros: " + detalles.size());
        for (DetalleVenta d : detalles) {
            System.out.println("   -> [" + d.getId() + "] Cantidad: " + d.getCantidad() + " - Subtotal: $" + d.getSubtotal());
        }
        System.out.println("========================================");
        assertTrue(
                detalles.stream().anyMatch(d -> idPrueba.equals(d.getId())),
                "La lista debe contener el detalle registrado."
        );
    }

    /**
     * Prueba la funcionalidad de eliminación (Delete) de un detalle de venta.
     * Borra el registro utilizando su ID y valida mediante búsqueda posterior que ya no exista.
     *
     * @throws Exception Si ocurre un error durante el proceso de eliminación.
     */
    @Test
    @Order(5)
    public void testEliminar() throws Exception {
        assertNotNull(idPrueba);
        detalleVentaDAO.eliminar(idPrueba);
        // 🔴 BREAKPOINT 3: Coloca el punto de interrupción aquí.
        DetalleVenta eliminado = detalleVentaDAO.buscarPorId(idPrueba);
        System.out.println("🗑️ [TEST ELIMINAR] Resultado de búsqueda: " + eliminado);
        assertNull(eliminado, "El detalle debe haberse eliminado.");
    }

    /**
     * Limpieza general de los datos auxiliares creados en el {@link BeforeAll}
     * para mantener aislada la base de datos de pruebas.
     */
    @AfterAll
    public static void limpiarDatos() {
        EntityManager em = ConexionJPA.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            if (idVenta != null) {
                Venta venta = em.find(Venta.class, idVenta);
                if (venta != null) {
                    em.remove(venta);
                }
            }
            em.flush();
            if (idPago != null) {
                Pago pago = em.find(Pago.class, idPago);
                if (pago != null) {
                    em.remove(pago);
                }
            }
            if (idProducto != null) {
                Producto producto = em.find(Producto.class, idProducto);
                if (producto != null) {
                    em.remove(producto);
                }
            }
            if (idUsuario != null) {
                Usuario usuario = em.find(Usuario.class, idUsuario);
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
