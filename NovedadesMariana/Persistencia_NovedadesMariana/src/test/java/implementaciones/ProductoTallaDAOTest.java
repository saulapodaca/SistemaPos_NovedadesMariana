
package implementaciones;

import dominio.Producto;
import dominio.ProductoTalla;
import dominio.TipoPrenda;
import interfaces.IProductoDAO;
import interfaces.IProductoTallaDAO;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoTallaDAOTest {

    private static IProductoTallaDAO productoTallaDAO;
    private static IProductoDAO productoDAO;

    private static Long idPrueba;
    private static Long idProducto;
    private static Producto productoPrueba;

    @BeforeAll
    public static void setUp() throws Exception {

        productoTallaDAO = new ProductoTallaDAO();
        productoDAO = new ProductoDAO();

        String codigo = UUID.randomUUID().toString();

        // Crear producto para las pruebas
        productoPrueba = new Producto(
            "SKU-PT-" + codigo,
            "Camisa de prueba",
            "Producto para pruebas de tallas",
            250.0f,
            TipoPrenda.CAMISA
        );

        productoDAO.guardar(productoPrueba);

        idProducto = productoPrueba.getId();

        assertNotNull(idProducto);
    }

    @Test
    @Order(1)
    public void testGuardar() throws Exception {

        ProductoTalla pt = new ProductoTalla(
            "M",
            15,
            productoPrueba
        );

        productoTallaDAO.guardar(pt);

        assertNotNull(
            pt.getId(),
            "La talla debe generar un ID."
        );

        idPrueba = pt.getId();

        ProductoTalla guardada =
                productoTallaDAO.buscarPorId(idPrueba);

        assertNotNull(guardada);

        assertEquals("M", guardada.getTalla());

        assertEquals(
            15,
            guardada.getStockActual().intValue()
        );

        assertEquals(
            idProducto,
            guardada.getProducto().getId()
        );
    }

    @Test
    @Order(2)
    public void testBuscarPorId() {

        assertNotNull(idPrueba);

        ProductoTalla pt =
                productoTallaDAO.buscarPorId(idPrueba);

        assertNotNull(
            pt,
            "La talla debe existir."
        );

        assertEquals("M", pt.getTalla());

        assertEquals(
            15,
            pt.getStockActual().intValue()
        );

        assertEquals(
            idProducto,
            pt.getProducto().getId()
        );
    }

    @Test
    @Order(3)
    public void testActualizar() throws Exception {

        ProductoTalla pt =
                productoTallaDAO.buscarPorId(idPrueba);

        assertNotNull(pt);

        // Actualizar existencias
        pt.setStockActual(20);

        productoTallaDAO.actualizar(pt);

        ProductoTalla actualizado =
                productoTallaDAO.buscarPorId(idPrueba);

        assertNotNull(actualizado);

        assertEquals(
            20,
            actualizado.getStockActual().intValue()
        );

        // Verificar que conserva el producto
        assertEquals(
            idProducto,
            actualizado.getProducto().getId()
        );
    }

    @Test
    @Order(4)
    public void testObtenerTodos() {

        List<ProductoTalla> lista =
                productoTallaDAO.obtenerTodos();

        assertNotNull(lista);

        assertTrue(
            lista.stream().anyMatch(
                pt -> idPrueba.equals(pt.getId())
            ),
            "La lista debe contener la talla registrada."
        );
    }

    @Test
    @Order(5)
    public void testEliminar() throws Exception {

        assertNotNull(idPrueba);

        // No debe permitir eliminar con existencias
        Exception excepcion = assertThrows(
            Exception.class,
            () -> productoTallaDAO.eliminar(idPrueba)
        );

        assertTrue(
            excepcion.getMessage().contains("existencias"),
            "Debe indicar que hay existencias."
        );

        // Dejar el stock en cero
        ProductoTalla pt =
                productoTallaDAO.buscarPorId(idPrueba);

        assertNotNull(pt);

        pt.setStockActual(0);

        productoTallaDAO.actualizar(pt);

        // Ahora si se permite eliminar
        productoTallaDAO.eliminar(idPrueba);

        ProductoTalla eliminada =
                productoTallaDAO.buscarPorId(idPrueba);

        assertNull(
            eliminada,
            "La talla debe haberse eliminado."
        );
    }

    @AfterAll
    public static void limpiarDatos() throws Exception {

        try {
            // Eliminar talla si alguna prueba fallo
            if (idPrueba != null) {

                ProductoTalla pt =
                        productoTallaDAO.buscarPorId(idPrueba);

                if (pt != null) {
                    pt.setStockActual(0);
                    productoTallaDAO.actualizar(pt);
                    productoTallaDAO.eliminar(idPrueba);
                }
            }

        } finally {

            // Eliminar producto de prueba
            if (idProducto != null) {

                Producto producto =
                        productoDAO.buscarPorId(idProducto);

                if (producto != null) {
                    productoDAO.eliminar(idProducto);
                }
            }
        }
    }
}
