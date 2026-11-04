
package implementaciones;

import dominio.MetodoPago;
import dominio.Pago;
import interfaces.IPagoDAO;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para validar las operaciones CRUD de {@link IPagoDAO}.
 * Los métodos se ejecutan en orden para reutilizar el pago creado.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoDAOTest {

    /** DAO utilizado para persistir y consultar pagos. */
    private static IPagoDAO pagoDAO;

    /** ID del pago de prueba, compartido entre los métodos de prueba. */
    private static Long idPrueba;

    /** Inicializa la implementación del DAO antes de ejecutar las pruebas. */
    @BeforeAll
    public static void setUp() {
        pagoDAO = new PagoDAO();
    }

    /** Guarda un pago y valida que sus datos principales queden persistidos. */
    @Test
    @Order(1)
    public void testGuardar() throws Exception {

        Pago pago = new Pago(
            MetodoPago.EFECTIVO,
            500.0f,
            600.0f,
            100.0f,
            LocalDateTime.now()
        );

        pagoDAO.guardar(pago);

        // 🔴 BREAKPOINT 1: Inspeccionar el ID generado y los importes persistidos.
        assertNotNull(
            pago.getId(),
            "El pago debe generar un ID."
        );

        idPrueba = pago.getId();

        Pago guardado = pagoDAO.buscarPorId(idPrueba);

        assertNotNull(guardado);
        System.out.println("========================================");
        System.out.println("💾 [TEST GUARDAR] Pago persistido:");
        System.out.println("   - ID: " + guardado.getId());
        System.out.println("   - Método: " + guardado.getMetodoPago());
        System.out.println("   - Monto: $" + guardado.getMonto());
        System.out.println("   - Recibido: $" + guardado.getMontoRecibido());
        System.out.println("   - Cambio: $" + guardado.getCambio());
        System.out.println("========================================");

        assertEquals(
            MetodoPago.EFECTIVO,
            guardado.getMetodoPago()
        );

        assertEquals(
            500.0f,
            guardado.getMonto(),
            0.001f
        );
    }

    /** Busca el pago de prueba por ID y valida todos sus importes. */
    @Test
    @Order(2)
    public void testBuscarPorId() {

        assertNotNull(idPrueba);

        Pago pago = pagoDAO.buscarPorId(idPrueba);

        assertNotNull(pago, "El pago debe existir.");
        System.out.println("========================================");
        System.out.println("🔍 [TEST BUSCAR POR ID] Pago encontrado:");
        System.out.println("   - ID: " + pago.getId());
        System.out.println("   - Método: " + pago.getMetodoPago());
        System.out.println("   - Monto: $" + pago.getMonto());
        System.out.println("   - Recibido: $" + pago.getMontoRecibido());
        System.out.println("   - Cambio: $" + pago.getCambio());
        System.out.println("========================================");
        assertNotNull(
            pago,
            "El pago debe existir."
        );

        assertEquals(
            MetodoPago.EFECTIVO,
            pago.getMetodoPago()
        );

        assertEquals(
            600.0f,
            pago.getMontoRecibido(),
            0.001f
        );

        assertEquals(
            100.0f,
            pago.getCambio(),
            0.001f
        );
    }

    /** Actualiza los importes del pago y comprueba los valores recuperados. */
    @Test
    @Order(3)
    public void testActualizar() throws Exception {

        Pago pago = pagoDAO.buscarPorId(idPrueba);

        assertNotNull(pago);

        // Actualizar monto y cambio
        pago.setMonto(550.0f);
        pago.setMontoRecibido(600.0f);
        pago.setCambio(50.0f);

        pagoDAO.actualizar(pago);

        // 🔴 BREAKPOINT 2: Revisar los importes actualizados antes de las aserciones.
        Pago actualizado = pagoDAO.buscarPorId(idPrueba);

        assertNotNull(actualizado);
        System.out.println("✏️ [TEST ACTUALIZAR] Pago: monto $" + actualizado.getMonto()
                + " | recibido $" + actualizado.getMontoRecibido()
                + " | cambio $" + actualizado.getCambio());

        assertEquals(
            550.0f,
            actualizado.getMonto(),
            0.001f
        );

        assertEquals(
            600.0f,
            actualizado.getMontoRecibido(),
            0.001f
        );

        assertEquals(
            50.0f,
            actualizado.getCambio(),
            0.001f
        );
    }

    /** Obtiene todos los pagos y verifica que incluya el pago de prueba. */
    @Test
    @Order(4)
    public void testObtenerTodos() {

        List<Pago> pagos = pagoDAO.obtenerTodos();
        assertNotNull(pagos);

        System.out.println("========================================");
        System.out.println("📋 [TEST OBTENER TODOS] Total de registros: " + pagos.size());
        for (Pago p : pagos) {
            System.out.println("   -> [" + p.getId() + "] " + p.getMetodoPago()
                    + " | Monto: $" + p.getMonto());
        }
        System.out.println("========================================");
        assertTrue(
            pagos.stream().anyMatch(
                p -> idPrueba.equals(p.getId())
            ),
            "La lista debe contener el pago registrado."
        );
    }

    /** Elimina el pago y valida que ya no pueda recuperarse por su ID. */
    @Test
    @Order(5)
    public void testEliminar() throws Exception {

        assertNotNull(idPrueba);

        pagoDAO.eliminar(idPrueba);

        // 🔴 BREAKPOINT 3: Confirmar el resultado de la búsqueda tras eliminar.
        Pago eliminado = pagoDAO.buscarPorId(idPrueba);

        System.out.println("🗑️ [TEST ELIMINAR] Resultado de búsqueda: " + eliminado);
        assertNull(
            eliminado,
            "El pago debe haberse eliminado."
        );
    }

    /** Limpia el pago si la ejecución no alcanzó testEliminar. */
    @AfterAll
    public static void limpiarDatos() throws Exception {

        if (idPrueba != null) {

            Pago pago = pagoDAO.buscarPorId(idPrueba);

            if (pago != null) {
                pagoDAO.eliminar(idPrueba);
            }
        }
    }
}
