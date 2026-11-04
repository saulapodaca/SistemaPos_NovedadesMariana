package implementaciones;

import bos.ProductoTallaBO;
import fachada.FachadaPersistencia;
import dominio.Producto;
import dominio.ProductoTalla;
import dominio.TipoPrenda;
import dtos.ProductoTallaDTO;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductoTallaBOTest {

    private final FachadaPersistencia persistencia = FachadaPersistencia.getInstance();
    private ProductoTallaBO productoTallaBO;
    private Long idProducto;
    private Long idTalla;

    @BeforeEach
    void setUp() throws Exception {
        productoTallaBO = ProductoTallaBO.getInstance();
        String codigo = UUID.randomUUID().toString().replace("-", "");
        Producto producto = new Producto("SKU-PT-" + codigo,
                "CB-PT-" + codigo, "Prenda de prueba", "Inventario de prueba",
                100.0f, TipoPrenda.CAMISA);
        ProductoTalla talla = new ProductoTalla("M", 8, producto);
        producto.agregarTalla(talla);
        persistencia.guardarProducto(producto);
        idProducto = producto.getId();
        idTalla = talla.getId();
    }

    @AfterEach
    void limpiar() throws Exception {
        if (idProducto != null && persistencia.buscarProductoPorId(idProducto) != null) {
            persistencia.eliminarProducto(idProducto);
        }
    }

    @Test
    void validaStockDisponibleYRechazaCantidadMayorAlDisponible() {
        System.out.println("[BO TALLA] Validando talla id=" + idTalla
                + " con stock inicial 8 y cantidades 8 y 9.");
        assertTrue(productoTallaBO.validarStockDisponible(idTalla.intValue(), 8));
        assertThrows(IllegalStateException.class,
                () -> productoTallaBO.validarStockDisponible(idTalla.intValue(), 9));
    }

    @Test
    void rechazaTallaInexistenteYCantidadInvalida() {
        System.out.println("[BO TALLA] Entradas rechazadas: talla inexistente y cantidades 0/ID 0.");
        assertThrows(IllegalArgumentException.class,
                () -> productoTallaBO.validarStockDisponible(Integer.MAX_VALUE, 1));
        assertThrows(IllegalArgumentException.class,
                () -> productoTallaBO.validarStockDisponible(idTalla.intValue(), 0));
        assertThrows(IllegalArgumentException.class,
                () -> productoTallaBO.validarStockDisponible(0, 1));
    }

    @Test
    void descontarActualizaElStockYRechazaFaltantes() {
        assertTrue(productoTallaBO.descontarStock(idTalla.intValue(), 3));
        ProductoTallaDTO actualizada = productoTallaBO
                .obtenerTallasPorProducto(idProducto.intValue()).get(0);
        System.out.println("[BO TALLA] Después de descontar 3: id=" + actualizada.getId()
                + ", talla=" + actualizada.getTalla()
                + ", stock=" + actualizada.getStockActual());
        assertEquals(5, actualizada.getStockActual());
        assertThrows(IllegalStateException.class,
                () -> productoTallaBO.descontarStock(idTalla.intValue(), 6));
        assertEquals(5, productoTallaBO.obtenerTallasPorProducto(idProducto.intValue())
                .get(0).getStockActual());
    }

    @Test
    void rechazaDescuentoConTallaInexistenteOCantidadInvalida() {
        System.out.println("[BO TALLA] Entradas de descuento rechazadas: talla inexistente y cantidad 0.");
        assertThrows(IllegalArgumentException.class,
                () -> productoTallaBO.descontarStock(Integer.MAX_VALUE, 1));
        assertThrows(IllegalArgumentException.class,
                () -> productoTallaBO.descontarStock(idTalla.intValue(), 0));
    }

    @Test
    void reabastecerActualizaStockYRechazaDesbordamiento() {
        assertTrue(productoTallaBO.reabastecerStock(idTalla.intValue(), 4));
        ProductoTallaDTO actualizada = productoTallaBO
                .obtenerTallasPorProducto(idProducto.intValue()).get(0);
        System.out.println("[BO TALLA] Después de reabastecer 4: id=" + actualizada.getId()
                + ", talla=" + actualizada.getTalla()
                + ", stock=" + actualizada.getStockActual());
        assertEquals(12, actualizada.getStockActual());
        assertThrows(IllegalArgumentException.class,
                () -> productoTallaBO.reabastecerStock(idTalla.intValue(), Integer.MAX_VALUE));
        assertEquals(12, productoTallaBO.obtenerTallasPorProducto(idProducto.intValue())
                .get(0).getStockActual());
    }

    @Test
    void rechazaReabastecimientoConTallaInexistenteOCantidadInvalida() {
        System.out.println("[BO TALLA] Entradas de reabastecimiento rechazadas: talla inexistente y cantidades 0/-1.");
        assertThrows(IllegalArgumentException.class,
                () -> productoTallaBO.reabastecerStock(Integer.MAX_VALUE, 1));
        assertThrows(IllegalArgumentException.class,
                () -> productoTallaBO.reabastecerStock(idTalla.intValue(), 0));
        assertThrows(IllegalArgumentException.class,
                () -> productoTallaBO.reabastecerStock(idTalla.intValue(), -1));
    }
}
