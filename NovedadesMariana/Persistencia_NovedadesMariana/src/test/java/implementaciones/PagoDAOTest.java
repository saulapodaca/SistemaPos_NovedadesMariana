
package implementaciones;

import dominio.MetodoPago;
import dominio.Pago;
import interfaces.IPagoDAO;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoDAOTest {

    private static IPagoDAO pagoDAO;
    private static Long idPrueba;

    @BeforeAll
    public static void setUp() {
        pagoDAO = new PagoDAO();
    }

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

        assertNotNull(
            pago.getId(),
            "El pago debe generar un ID."
        );

        idPrueba = pago.getId();

        Pago guardado = pagoDAO.buscarPorId(idPrueba);

        assertNotNull(guardado);

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

    @Test
    @Order(2)
    public void testBuscarPorId() {

        assertNotNull(idPrueba);

        Pago pago = pagoDAO.buscarPorId(idPrueba);

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

        Pago actualizado = pagoDAO.buscarPorId(idPrueba);

        assertNotNull(actualizado);

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

    @Test
    @Order(4)
    public void testObtenerTodos() {

        List<Pago> pagos = pagoDAO.obtenerTodos();

        assertNotNull(pagos);

        assertTrue(
            pagos.stream().anyMatch(
                p -> idPrueba.equals(p.getId())
            ),
            "La lista debe contener el pago registrado."
        );
    }

    @Test
    @Order(5)
    public void testEliminar() throws Exception {

        assertNotNull(idPrueba);

        pagoDAO.eliminar(idPrueba);

        Pago eliminado = pagoDAO.buscarPorId(idPrueba);

        assertNull(
            eliminado,
            "El pago debe haberse eliminado."
        );
    }

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
