package implementaciones;

import bos.VentaBO;
import dtos.DetalleVentaDTO;
import dtos.ResumenVentaDTO;
import dtos.VentaDTO;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VentaBOTest {

    private VentaBO ventaBO;
    private BOTestFixture fixture;

    @BeforeEach
    void setUp() throws Exception {
        ventaBO = VentaBO.getInstance();
        fixture = new BOTestFixture();
    }

    @AfterEach
    void limpiar() throws Exception {
        fixture.close();
    }

    @Test
    void calcularTotalesVentaValidaConDescuento() {
        ResumenVentaDTO resumen = ventaBO.calcularTotales(
                List.of(detalle(2, 100.0f)), 10.0f);

        System.out.println("[BO VENTA] Totales calculados: subtotal=" + resumen.getSubtotal()
                + ", descuento=" + resumen.getDescuento()
                + ", IVA=" + resumen.getIva()
                + ", total=" + resumen.getTotal());
        assertEquals(200.0f, resumen.getSubtotal(), 0.001f);
        assertEquals(20.0f, resumen.getDescuento(), 0.001f);
        assertEquals(28.8f, resumen.getIva(), 0.001f);
        assertEquals(208.8f, resumen.getTotal(), 0.001f);
    }

    @Test
    void calcularTotalesRechazaListaNulaOVacia() {
        System.out.println("[BO VENTA] Se rechazan lista de detalles nula y lista vacía.");
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.calcularTotales(null, 0.0f));
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.calcularTotales(List.of(), 0.0f));
    }

    @Test
    void calcularTotalesRechazaDescuentoFueraDeRango() {
        System.out.println("[BO VENTA] Se rechazan descuentos -0.01, 100.01 y NaN.");
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.calcularTotales(List.of(detalle(1, 10.0f)), -0.01f));
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.calcularTotales(List.of(detalle(1, 10.0f)), 100.01f));
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.calcularTotales(List.of(detalle(1, 10.0f)), Float.NaN));
    }

    @Test
    void calcularTotalesRechazaDetalleNuloCantidadInvalidaOPrecioNegativo() {
        System.out.println("[BO VENTA] Se rechazan detalle nulo, cantidad 0/-1 y precio -1.");
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.calcularTotales(Collections.singletonList(null), 0.0f));
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.calcularTotales(List.of(detalle(0, 10.0f)), 0.0f));
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.calcularTotales(List.of(detalle(-1, 10.0f)), 0.0f));
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.calcularTotales(List.of(detalle(1, -1.0f)), 0.0f));
    }

    @Test
    void registrarVentaPersisteYAsignaIdFolioYFecha() {
        VentaDTO registrada = fixture.registrarVenta(2);

        imprimirVenta("Venta registrada", registrada);
        assertTrue(registrada.getId() > 0);
        assertTrue(registrada.getFolio().startsWith("VEN-"));
        assertNotNull(registrada.getFechaHora());
        assertEquals(fixture.usuario.getId().intValue(), registrada.getIdUsuario());
        assertEquals(2, registrada.getDetalles().get(0).getCantidad());
        assertEquals(fixture.talla.getId().intValue(),
                registrada.getDetalles().get(0).getIdProductoTalla());
        assertEquals(fixture.talla.getTalla(),
                registrada.getDetalles().get(0).getTalla());
        assertEquals(1, registrada.getPagos().size());
        assertTrue(registrada.getPagos().get(0).getId() > 0);
        assertEquals(registrada.getTotal(),
                registrada.getPagos().get(0).getMonto(), 0.001f);
        assertNotNull(registrada.getPagos().get(0).getFechaHora());
        assertNotNull(ventaBO.obtenerPorId(registrada.getId()));
        assertEquals(registrada.getId(),
                ventaBO.obtenerPorFolio(registrada.getFolio()).getId());
    }

    @Test
    void registrarVentaRechazaDatosInvalidos() {
        imprimirVenta("Venta nula rechazada", null);
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.registrarVenta(null));

        VentaDTO sinEmpleado = fixture.nuevaVenta(1);
        sinEmpleado.setIdUsuario(0);
        imprimirVenta("Venta sin empleado rechazada", sinEmpleado);
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.registrarVenta(sinEmpleado));

        VentaDTO sinDetalles = fixture.nuevaVenta(1);
        sinDetalles.setDetalles(List.of());
        imprimirVenta("Venta sin detalles rechazada", sinDetalles);
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.registrarVenta(sinDetalles));

        VentaDTO sinPago = fixture.nuevaVenta(1);
        sinPago.setPagos(List.of());
        imprimirVenta("Venta sin pago rechazada", sinPago);
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.registrarVenta(sinPago));

        VentaDTO pagoPorMontoIncorrecto = fixture.nuevaVenta(1);
        pagoPorMontoIncorrecto.getPagos().get(0).setMonto(1.0f);
        imprimirVenta("Venta con monto de pago incorrecto", pagoPorMontoIncorrecto);
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.registrarVenta(pagoPorMontoIncorrecto));

        VentaDTO importeInvalido = fixture.nuevaVenta(1);
        importeInvalido.setTotal(0.0f);
        imprimirVenta("Venta con total inválido", importeInvalido);
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.registrarVenta(importeInvalido));

        VentaDTO detalleInvalido = fixture.nuevaVenta(1);
        detalleInvalido.setDetalles(Collections.singletonList(null));
        imprimirVenta("Venta con detalle nulo", detalleInvalido);
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.registrarVenta(detalleInvalido));
    }

    @Test
    void registrarVentaRechazaTallaInexistente() {
        VentaDTO dto = fixture.nuevaVenta(1);
        dto.getDetalles().get(0).setIdProductoTalla(Integer.MAX_VALUE);
        imprimirVenta("Venta con talla inexistente", dto);
        assertThrows(IllegalArgumentException.class,
                () -> ventaBO.registrarVenta(dto));
    }

    @Test
    void cancelarVentaEliminaVentaYDevuelveFalseParaVentaInexistente() {
        VentaDTO registrada = fixture.registrarVenta(2);

        imprimirVenta("Venta que se cancela", registrada);
        boolean cancelada = ventaBO.cancelarVenta(registrada.getId());
        VentaDTO despuesDeCancelar = ventaBO.obtenerPorId(registrada.getId());
        boolean inexistenteCancelada = ventaBO.cancelarVenta(Integer.MAX_VALUE);
        System.out.println("[BO VENTA] Cancelación: resultado=" + cancelada
                + ", consulta posterior=" + despuesDeCancelar
                + ", ID inexistente=" + inexistenteCancelada);
        assertTrue(cancelada);
        assertNull(despuesDeCancelar);
        assertFalse(inexistenteCancelada);
    }

    private static void imprimirVenta(String etiqueta, VentaDTO venta) {
        if (venta == null) {
            System.out.println("[BO VENTA] " + etiqueta + ": null");
            return;
        }
        System.out.println("[BO VENTA] " + etiqueta + ": id=" + venta.getId()
                + ", folio=" + venta.getFolio()
                + ", usuario=" + venta.getIdUsuario()
                + ", subtotal=" + venta.getSubtotal()
                + ", IVA=" + venta.getIva()
                + ", total=" + venta.getTotal()
                + ", detalles=" + (venta.getDetalles() == null ? null : venta.getDetalles().size())
                + ", pagos=" + (venta.getPagos() == null ? null : venta.getPagos().size()));
    }

    private static DetalleVentaDTO detalle(int cantidad, float precio) {
        return new DetalleVentaDTO(0, 0, 1, "Producto de prueba", "M",
                cantidad, precio, cantidad * precio);
    }
}
