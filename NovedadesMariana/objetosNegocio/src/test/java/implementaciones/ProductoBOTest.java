package implementaciones;

import bos.ProductoBO;
import dtos.ProductoDTO;
import dtos.TipoPrendaDTO;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductoBOTest {

    private BOTestFixture fixture;
    private ProductoBO productoBO;

    @BeforeEach
    void setUp() throws Exception {
        fixture = new BOTestFixture();
        productoBO = ProductoBO.getInstance();
    }

    @AfterEach
    void limpiar() throws Exception {
        fixture.close();
    }

    @Test
    void encuentraProductoPorSkuYCodigoDeBarras() {
        ProductoDTO porSku = productoBO.obtenerPorSku(fixture.producto.getSku());
        ProductoDTO porCodigo = productoBO.obtenerPorCodigoBarras(
                fixture.producto.getCodigoBarras());

        assertNotNull(porSku);
        assertNotNull(porCodigo);
        System.out.println("[BO PRODUCTO] SKU consultado: id=" + porSku.getId()
                + ", sku=" + porSku.getSku() + ", nombre=" + porSku.getNombre());
        System.out.println("[BO PRODUCTO] Código consultado: id=" + porCodigo.getId()
                + ", código=" + porCodigo.getCodigoBarras()
                + ", nombre=" + porCodigo.getNombre());
        assertEquals(fixture.producto.getId(), porSku.getId());
        assertEquals(fixture.producto.getId(), porCodigo.getId());
        assertEquals(fixture.producto.getCodigoBarras(), porCodigo.getCodigoBarras());
    }

    @Test
    void devuelveNullCuandoCodigoOIdValidosNoExisten() {
        ProductoDTO porSku = productoBO.obtenerPorSku("SKU-INEXISTENTE-BO");
        ProductoDTO porCodigo = productoBO.obtenerPorCodigoBarras("BAR-INEXISTENTE-BO");
        ProductoDTO porId = productoBO.obtenerPorId(Long.MAX_VALUE);
        System.out.println("[BO PRODUCTO] Consultas inexistentes: SKU=" + porSku
                + ", código=" + porCodigo + ", ID=" + porId);
        assertNull(porSku);
        assertNull(porCodigo);
        assertNull(porId);
    }

    @Test
    void rechazaIdOCodigoInvalidos() {
        System.out.println("[BO PRODUCTO] Se rechazan ID, SKU, código de barras y criterio vacíos o nulos.");
        assertThrows(IllegalArgumentException.class,
                () -> productoBO.obtenerPorId(null));
        assertThrows(IllegalArgumentException.class,
                () -> productoBO.obtenerPorId(0L));
        assertThrows(IllegalArgumentException.class,
                () -> productoBO.obtenerPorSku(" "));
        assertThrows(IllegalArgumentException.class,
                () -> productoBO.obtenerPorSku(null));
        assertThrows(IllegalArgumentException.class,
                () -> productoBO.obtenerPorCodigoBarras(" "));
        assertThrows(IllegalArgumentException.class,
                () -> productoBO.obtenerPorCodigoBarras(null));
        assertThrows(IllegalArgumentException.class,
                () -> productoBO.buscarProductos(" "));
    }

    @Test
    void guardaActualizaYEliminaProductoConCategoriasPorId() throws Exception {
        String codigo = UUID.randomUUID().toString().replace("-", "");
        ProductoDTO nuevo = new ProductoDTO("BAR-" + codigo, "SKU-" + codigo,
                "Prenda BO", "Producto para prueba", 125.0f,
                TipoPrendaDTO.CAMISA, Set.of(fixture.categoria.getId()));
        productoBO.guardar(nuevo);

        ProductoDTO guardado = productoBO.obtenerPorSku(nuevo.getSku());
        assertNotNull(guardado);
        assertEquals(Set.of(fixture.categoria.getId()), guardado.getCategoriasIds());
        guardado.setNombre("Prenda BO actualizada");
        productoBO.actualizar(guardado);
        assertEquals("Prenda BO actualizada", productoBO.obtenerPorId(guardado.getId()).getNombre());

        productoBO.eliminar(guardado.getId());
        assertNull(productoBO.obtenerPorId(guardado.getId()));
    }
}
