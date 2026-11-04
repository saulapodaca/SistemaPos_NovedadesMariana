package implementaciones;

import bos.PagoBO;
import bos.ProductoBO;
import bos.ProductoTallaBO;
import bos.UsuarioBO;
import dtos.PagoDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import dtos.UsuarioDTO;
import dtos.VentaDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Recorrido de venta usando DTOs, BO y DAO reales sobre la base de pruebas. */
class FlujoVentaBOTest {

    @Test
    void productoInventarioVentaPagoYEmpleadoSeIntegran() throws Exception {
        try (BOTestFixture fixture = new BOTestFixture()) {
            ProductoDTO producto = ProductoBO.getInstance()
                    .obtenerPorCodigoBarras(fixture.producto.getCodigoBarras());
            assertNotNull(producto);
            System.out.println("[FLUJO VENTA] Producto consultado: id=" + producto.getId()
                    + ", SKU=" + producto.getSku()
                    + ", código=" + producto.getCodigoBarras()
                    + ", nombre=" + producto.getNombre());

            ProductoTallaBO inventario = ProductoTallaBO.getInstance();
            int idTalla = Math.toIntExact(fixture.talla.getId());
            int idProducto = Math.toIntExact(fixture.producto.getId());
            int cantidad = 2;
            boolean stockDisponible = inventario.validarStockDisponible(idTalla, cantidad);
            System.out.println("[FLUJO VENTA] Talla id=" + idTalla
                    + ", cantidad solicitada=" + cantidad
                    + ", stock suficiente=" + stockDisponible);
            assertTrue(stockDisponible);

            VentaDTO registrada = fixture.registrarVenta(fixture.nuevaVenta(cantidad));
            System.out.println("[FLUJO VENTA] Venta creada: id=" + registrada.getId()
                    + ", folio=" + registrada.getFolio()
                    + ", subtotal=" + registrada.getSubtotal()
                    + ", IVA=" + registrada.getIva()
                    + ", total=" + registrada.getTotal());
            assertTrue(registrada.getId() > 0);
            assertNotNull(registrada.getFolio());
            assertNotNull(registrada.getFechaHora());
            assertEquals(500.0f, registrada.getSubtotal(), 0.001f);
            assertEquals(80.0f, registrada.getIva(), 0.001f);

            List<ProductoTallaDTO> tallas = inventario.obtenerTallasPorProducto(idProducto);
            for (ProductoTallaDTO talla : tallas) {
                System.out.println("[FLUJO VENTA] Inventario consultado: id=" + talla.getId()
                        + ", talla=" + talla.getTalla()
                        + ", stock=" + talla.getStockActual());
            }
            assertEquals(18, tallas.get(0).getStockActual());

            assertEquals(1, registrada.getPagos().size());
            PagoDTO pago = registrada.getPagos().get(0);
            PagoDTO registrado = PagoBO.getInstance().registrarPago(pago);
            System.out.println("[FLUJO VENTA] Pago consultado: id=" + registrado.getId()
                    + ", venta=" + registrado.getIdVenta()
                    + ", método=" + registrado.getMetodoPago()
                    + ", monto=" + registrado.getMonto()
                    + ", cambio=" + registrado.getCambio());
            assertTrue(registrado.getId() > 0);
            assertEquals(50.0f, registrado.getCambio(), 0.001f);
            assertNotNull(registrado.getFechaHora());

            UsuarioDTO autenticado = UsuarioBO.getInstance().autenticar(
                    fixture.usuario.getUsuario(), fixture.usuario.getContrasenia());
            assertNotNull(autenticado);
            System.out.println("[FLUJO VENTA] Empleado autenticado: id=" + autenticado.getId()
                    + ", usuario=" + autenticado.getUsuario()
                    + ", rol=" + autenticado.getRolEmpleado());
            assertEquals(fixture.usuario.getId(), autenticado.getId());
        }
    }
}
