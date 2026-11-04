package implementaciones;

import bos.PagoBO;
import dtos.PagoDTO;
import dtos.VentaDTO;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PagoBOTest {

    private PagoBO pagoBO;
    private BOTestFixture fixture;

    @BeforeEach
    void setUp() throws Exception {
        pagoBO = PagoBO.getInstance();
        fixture = new BOTestFixture();
    }

    @AfterEach
    void limpiar() throws Exception {
        fixture.close();
    }

    @Test
    void registraPagoEfectivoAsignaIdFechaYCalculaCambio() {
        VentaDTO venta = fixture.registrarVenta(1, " efectivo ", 25.0f);
        PagoDTO registrado = pagoBO.registrarPago(venta.getPagos().get(0));

        System.out.println("[BO PAGO] Pago consultado tras registrar venta: id=" + registrado.getId()
                + ", venta=" + registrado.getIdVenta()
                + ", método=" + registrado.getMetodoPago()
                + ", monto=" + registrado.getMonto()
                + ", recibido=" + registrado.getMontoRecibido()
                + ", cambio=" + registrado.getCambio()
                + ", fecha=" + registrado.getFechaHora());
        assertTrue(registrado.getId() > 0);
        assertEquals("EFECTIVO", registrado.getMetodoPago());
        assertEquals(25.0f, registrado.getCambio(), 0.001f);
        assertNotNull(registrado.getFechaHora());
        assertEquals(1, pagoBO.obtenerPagosPorVenta(venta.getId()).size());
    }

    @Test
    void registraPagoNoEfectivoSinCambio() {
        VentaDTO venta = fixture.registrarVenta(1, "tarjeta", 0.0f);
        PagoDTO registrado = pagoBO.registrarPago(venta.getPagos().get(0));

        System.out.println("[BO PAGO] Pago con tarjeta: id=" + registrado.getId()
                + ", monto=" + registrado.getMonto()
                + ", recibido=" + registrado.getMontoRecibido()
                + ", cambio=" + registrado.getCambio());
        assertEquals("TARJETA", registrado.getMetodoPago());
        assertEquals(venta.getTotal(), registrado.getMontoRecibido(), 0.001f);
        assertEquals(0.0f, registrado.getCambio(), 0.001f);
    }

    @Test
    void calcularCambioEntregaCambioCorrecto() {
        float cambio = pagoBO.calcularCambio(85.0f, 100.0f);
        System.out.println("[BO PAGO] Cambio calculado: total=85, recibido=100, cambio=" + cambio);
        assertEquals(15.0f, cambio, 0.001f);
    }

    @Test
    void rechazaPagoNuloVentaInvalidaMetodoDesconocidoYMontosInvalidos() {
        System.out.println("[BO PAGO] Se rechazan pago nulo, venta inválida, método desconocido y montos no positivos.");
        assertThrows(IllegalArgumentException.class,
                () -> pagoBO.prepararPagoParaVenta(null));
        assertThrows(IllegalArgumentException.class,
                () -> pagoBO.registrarPago(pago(0, "EFECTIVO", 10.0f, 10.0f)));
        assertThrows(IllegalArgumentException.class,
                () -> pagoBO.prepararPagoParaVenta(pago(0, "CRIPTO", 10.0f, 10.0f)));
        assertThrows(IllegalArgumentException.class,
                () -> pagoBO.prepararPagoParaVenta(pago(0, "TARJETA", 0.0f, 0.0f)));
        assertThrows(IllegalArgumentException.class,
                () -> pagoBO.prepararPagoParaVenta(pago(0, "TARJETA", -1.0f, 0.0f)));
    }

    @Test
    void rechazaVentaInexistenteYEfectivoInsuficiente() {
        System.out.println("[BO PAGO] Se rechazan venta inexistente y efectivo menor que el total.");
        assertThrows(IllegalStateException.class,
                () -> pagoBO.registrarPago(pago(Integer.MAX_VALUE, "TARJETA", 10.0f, 10.0f)));
        assertThrows(IllegalArgumentException.class,
                () -> pagoBO.prepararPagoParaVenta(
                        pago(0, "EFECTIVO", 10.0f, 9.0f)));
        assertThrows(IllegalArgumentException.class,
                () -> pagoBO.calcularCambio(0.0f, 10.0f));
        assertThrows(IllegalArgumentException.class,
                () -> pagoBO.calcularCambio(10.0f, 9.0f));
    }

    private static PagoDTO pago(int idVenta, String metodo, float monto, float recibido) {
        return new PagoDTO(0, idVenta, metodo, monto, recibido, 0.0f, LocalDateTime.now());
    }
}
