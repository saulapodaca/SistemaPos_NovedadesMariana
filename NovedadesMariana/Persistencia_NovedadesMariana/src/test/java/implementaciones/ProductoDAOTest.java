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

/**
 * Pruebas de integración para las operaciones CRUD de {@link IProductoDAO}.
 * Se crea una categoría auxiliar para validar también la relación del producto.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoDAOTest {

    /** DAO de productos bajo prueba. */
    private static IProductoDAO productoDAO;

    /** DAO utilizado para preparar y limpiar la categoría asociada. */
    private static ICategoriaDAO categoriaDAO;

    /** ID del producto creado durante la prueba. */
    private static Long idPrueba;

    /** ID de la categoría auxiliar. */
    private static Long idCategoria;

    /** SKU único asignado al producto de prueba. */
    private static String skuPrueba;

    /** Código de barras único asignado al producto de prueba. */
    private static String codigoBarrasPrueba;

    /** Categoría asociada al producto de prueba. */
    private static Categoria categoriaPrueba;

    /** Inicializa los DAO y persiste la categoría que requiere el producto. */
    @BeforeAll
    public static void setUp() throws Exception {
        productoDAO = new ProductoDAO();
        categoriaDAO = new CategoriaDAO();

        String codigo = UUID.randomUUID()
                .toString().substring(0, 8);

        skuPrueba = "SKU-TEST-" + codigo;
        codigoBarrasPrueba = "CB-" + codigo;

        // Crear categoria de prueba
        categoriaPrueba = new Categoria(
            "Pantalones-" + codigo,
            "Categoria para pruebas de productos"
        );

        categoriaDAO.guardar(categoriaPrueba);

        idCategoria = categoriaPrueba.getId();
    }

    /** Guarda un producto con categoría y tallas, y valida sus relaciones. */
    @Test
    @Order(1)
    public void testGuardar() throws Exception {
        // Se incluyen los 6 parámetros requeridos por el constructor de Producto
        Producto producto = new Producto(
            skuPrueba,
            codigoBarrasPrueba,
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

        // 🔴 BREAKPOINT 1: Inspeccionar el producto y sus relaciones tras guardar.
        assertNotNull(
            producto.getId(),
            "El producto debe generar un ID."
        );

        idPrueba = producto.getId();

        // CORREGIDO: Se usa buscarProductoPorId según la interfaz
        Producto guardado = productoDAO.buscarProductoPorId(idPrueba);

        assertNotNull(guardado);
        System.out.println("========================================");
        System.out.println("💾 [TEST GUARDAR] Producto persistido:");
        System.out.println("   - ID: " + guardado.getId());
        System.out.println("   - SKU: " + guardado.getSku());
        System.out.println("   - Código de barras: " + guardado.getCodigoBarras());
        System.out.println("   - Tallas: " + guardado.getTallas().size());
        System.out.println("   - Categorías: " + guardado.getCategorias().size());
        System.out.println("========================================");

        assertEquals(skuPrueba, guardado.getSku());
        assertEquals(codigoBarrasPrueba, guardado.getCodigoBarras());

        assertEquals(
            TipoPrenda.PANTALON,
            guardado.getTipoPrenda()
        );

        assertEquals(2, guardado.getTallas().size());

        assertEquals(1, guardado.getCategorias().size());
    }

    /** Busca el producto por ID y valida datos, tallas y categoría asociada. */
    @Test
    @Order(2)
    public void testBuscarPorId() {
        assertNotNull(idPrueba);

        // CORREGIDO: Se usa buscarProductoPorId según la interfaz
        Producto producto = productoDAO.buscarProductoPorId(idPrueba);

        assertNotNull(producto, "El producto debe existir.");
        System.out.println("========================================");
        System.out.println("🔍 [TEST BUSCAR POR ID] Producto encontrado:");
        System.out.println("   - ID: " + producto.getId());
        System.out.println("   - Nombre: " + producto.getNombre());
        System.out.println("   - SKU: " + producto.getSku());
        System.out.println("   - Precio: $" + producto.getPrecio());
        System.out.println("   - Tallas: " + producto.getTallas().size());
        System.out.println("   - Categorías: " + producto.getCategorias().size());
        System.out.println("========================================");
        assertNotNull(
            producto,
            "El producto debe existir."
        );

        assertEquals(skuPrueba, producto.getSku());
        assertEquals(codigoBarrasPrueba, producto.getCodigoBarras());

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

    /** Actualiza precio y descripción, comprobando que las relaciones se conserven. */
    @Test
    @Order(3)
    public void testActualizar() throws Exception {
        // CORREGIDO: Se usa buscarProductoPorId
        Producto producto = productoDAO.buscarProductoPorId(idPrueba);

        assertNotNull(producto);

        producto.setPrecio(499.50f);
        producto.setDescripcion(
            "Pantalon de mezclilla actualizado"
        );

        productoDAO.actualizar(producto);

        // 🔴 BREAKPOINT 2: Revisar los cambios antes de comprobarlos en la base.
        // CORREGIDO: Se usa buscarProductoPorId
        Producto actualizado =
                productoDAO.buscarProductoPorId(idPrueba);

        assertNotNull(actualizado);
        System.out.println("✏️ [TEST ACTUALIZAR] Producto: " + actualizado.getNombre()
                + " | Precio: $" + actualizado.getPrecio()
                + " | Descripción: " + actualizado.getDescripcion());

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

    /** Obtiene todos los productos y verifica que incluya el producto de prueba. */
    @Test
    @Order(4)
    public void testObtenerTodos() {
        List<Producto> productos =
                productoDAO.obtenerTodos();
        assertNotNull(productos);

        System.out.println("========================================");
        System.out.println("📋 [TEST OBTENER TODOS] Total de registros: " + productos.size());
        for (Producto p : productos) {
            System.out.println("   -> [" + p.getId() + "] " + p.getSku()
                    + " - " + p.getNombre() + " ($" + p.getPrecio() + ")");
        }
        System.out.println("========================================");
        assertTrue(
            productos.stream().anyMatch(
                p -> idPrueba.equals(p.getId())
            ),
            "La lista debe contener el producto registrado."
        );
    }

    /** Elimina el producto de prueba y valida que la búsqueda posterior sea nula. */
    @Test
    @Order(5)
    public void testEliminar() throws Exception {
        assertNotNull(idPrueba);

        productoDAO.eliminar(idPrueba);

        // 🔴 BREAKPOINT 3: Confirmar que el producto ya no se recupera.
        // CORREGIDO: Se usa buscarProductoPorId
        Producto eliminado =
                productoDAO.buscarProductoPorId(idPrueba);

        System.out.println("🗑️ [TEST ELIMINAR] Resultado de búsqueda: " + eliminado);
        assertNull(
            eliminado,
            "El producto debe haberse eliminado."
        );
    }

    /** Limpia el producto y la categoría si una prueba falla antes de eliminarlos. */
    @AfterAll
    public static void limpiarDatos() throws Exception {
        try {
            // Eliminar producto si alguna prueba fallo
            if (idPrueba != null) {
                // CORREGIDO: Se usa buscarProductoPorId
                Producto producto =
                        productoDAO.buscarProductoPorId(idPrueba);

                if (producto != null) {
                    productoDAO.eliminar(idPrueba);
                }
            }
        } finally {
            // Eliminar categoria de prueba
            if (idCategoria != null) {
                // CORREGIDO: Se usa buscarCategoriaPorId
                Categoria categoria =
                        categoriaDAO.buscarCategoriaPorId(idCategoria);

                if (categoria != null) {
                    categoriaDAO.eliminar(idCategoria);
                }
            }
        }
    }
}