
package implementaciones;

import dominio.Categoria;
import dominio.Producto;
import dominio.ProductoTalla;
import dominio.TipoPrenda;
import interfaces.ICategoriaDAO;
import interfaces.IProductoDAO;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoDAOTest {

    private static IProductoDAO productoDAO;
    private static ICategoriaDAO categoriaDAO;

    private static Long idPrueba;
    private static Long idCategoria;
    private static String skuPrueba;
    private static Categoria categoriaPrueba;

    @BeforeAll
    public static void setUp() throws Exception {

        productoDAO = new ProductoDAO();
        categoriaDAO = new CategoriaDAO();

        String codigo = UUID.randomUUID()
                .toString().substring(0, 8);

        skuPrueba = "SKU-TEST-" + codigo;

        // Crear categoria de prueba
        categoriaPrueba = new Categoria(
            "Pantalones-" + codigo,
            "Categoria para pruebas de productos"
        );

        categoriaDAO.guardar(categoriaPrueba);

        idCategoria = categoriaPrueba.getId();
    }

    @Test
    @Order(1)
    public void testGuardar() throws Exception {

        Producto producto = new Producto(
            skuPrueba,
            "Pantalon Mezclilla",
            "Pantalon azul clasico",
            450.00f,
            TipoPrenda.PANTALON
        );

        // Agregar categoria
        producto.agregarCategoria(categoriaPrueba);

        // Agregar tallas y existencias
        producto.agregarTalla(
            new ProductoTalla("30", 5, producto)
        );

        producto.agregarTalla(
            new ProductoTalla("32", 8, producto)
        );

        productoDAO.guardar(producto);

        assertNotNull(
            producto.getId(),
            "El producto debe generar un ID."
        );

        idPrueba = producto.getId();

        Producto guardado = productoDAO.buscarPorId(idPrueba);

        assertNotNull(guardado);

        assertEquals(skuPrueba, guardado.getSku());

        assertEquals(
            TipoPrenda.PANTALON,
            guardado.getTipoPrenda()
        );

        assertEquals(2, guardado.getTallas().size());

        assertEquals(1, guardado.getCategorias().size());
    }

    @Test
    @Order(2)
    public void testBuscarPorId() {

        assertNotNull(idPrueba);

        Producto producto = productoDAO.buscarPorId(idPrueba);

        assertNotNull(
            producto,
            "El producto debe existir."
        );

        assertEquals(skuPrueba, producto.getSku());

        assertEquals(
            "Pantalon Mezclilla",
            producto.getNombre()
        );

        assertEquals(
            450.00f,
            producto.getPrecio(),
            0.001f
        );

        // Verificar las tallas
        assertEquals(2, producto.getTallas().size());

        assertTrue(
            producto.getTallas().stream().anyMatch(
                t -> t.getTalla().equals("30")
                     && t.getStockActual() == 5
            )
        );

        assertTrue(
            producto.getTallas().stream().anyMatch(
                t -> t.getTalla().equals("32")
                     && t.getStockActual() == 8
            )
        );

        // Verificar la categoria
        assertTrue(
            producto.getCategorias().stream().anyMatch(
                c -> idCategoria.equals(c.getId())
            )
        );
    }

    @Test
    @Order(3)
    public void testActualizar() throws Exception {

        Producto producto = productoDAO.buscarPorId(idPrueba);

        assertNotNull(producto);

        producto.setPrecio(499.50f);
        producto.setDescripcion(
            "Pantalon de mezclilla actualizado"
        );

        productoDAO.actualizar(producto);

        Producto actualizado =
                productoDAO.buscarPorId(idPrueba);

        assertNotNull(actualizado);

        assertEquals(
            499.50f,
            actualizado.getPrecio(),
            0.001f
        );

        assertEquals(
            "Pantalon de mezclilla actualizado",
            actualizado.getDescripcion()
        );

        // Verificar que no se modificaron las tallas
        assertEquals(2, actualizado.getTallas().size());

        // Verificar que conserva su categoria
        assertEquals(1, actualizado.getCategorias().size());
    }

    @Test
    @Order(4)
    public void testObtenerTodos() {

        List<Producto> productos =
                productoDAO.obtenerTodos();

        assertNotNull(productos);

        assertTrue(
            productos.stream().anyMatch(
                p -> idPrueba.equals(p.getId())
            ),
            "La lista debe contener el producto registrado."
        );
    }

    @Test
    @Order(5)
    public void testEliminar() throws Exception {

        assertNotNull(idPrueba);

        productoDAO.eliminar(idPrueba);

        Producto eliminado =
                productoDAO.buscarPorId(idPrueba);

        assertNull(
            eliminado,
            "El producto debe haberse eliminado."
        );
    }

    @AfterAll
    public static void limpiarDatos() throws Exception {

        try {
            // Eliminar producto si alguna prueba fallo
            if (idPrueba != null) {

                Producto producto =
                        productoDAO.buscarPorId(idPrueba);

                if (producto != null) {
                    productoDAO.eliminar(idPrueba);
                }
            }

        } finally {

            // Eliminar categoria de prueba
            if (idCategoria != null) {

                Categoria categoria =
                        categoriaDAO.buscarPorId(idCategoria);

                if (categoria != null) {
                    categoriaDAO.eliminar(idCategoria);
                }
            }
        }
    }
}
