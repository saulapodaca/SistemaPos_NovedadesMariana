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

/**
 * Pruebas de integración para validar las operaciones CRUD de
 * {@link IProductoTallaDAO}, incluyendo la regla de stock al eliminar.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductoTallaDAOTest {

   /** DAO de tallas bajo prueba. */
   private static IProductoTallaDAO productoTallaDAO;

   /** DAO que prepara y limpia el producto asociado. */
   private static IProductoDAO productoDAO;

   /** ID de la talla de prueba. */
   private static Long idPrueba;

   /** ID del producto asociado al talla. */
   private static Long idProducto;

   /** Producto auxiliar requerido para crear la talla. */
   private static Producto productoPrueba;

   /** Crea y persiste el producto asociado antes de ejecutar las pruebas. */
   @BeforeAll
   public static void setUp() throws Exception {

        productoTallaDAO = new ProductoTallaDAO();
        productoDAO = new ProductoDAO();

        String codigo = UUID.randomUUID().toString();

        // Crear producto para las pruebas
        productoPrueba = new Producto(
            "SKU-PT-" + codigo,
            "CB-PT-" + codigo,
            "Camisa de prueba",
            "Producto para pruebas de tallas",
            250.0f,
            TipoPrenda.CAMISA
        );

        productoDAO.guardar(productoPrueba);

        idProducto = productoPrueba.getId();

        assertNotNull(idProducto);
   }

   /** Guarda una talla y valida sus datos y su producto asociado. */
   @Test
   @Order(1)
   public void testGuardar() throws Exception {

        ProductoTalla pt = new ProductoTalla(
            "M",
            15,
            productoPrueba
        );

        productoTallaDAO.guardar(pt);

        // 🔴 BREAKPOINT 1: Revisar el ID y la relación con el producto guardado.
        assertNotNull(
            pt.getId(),
            "La talla debe generar un ID."
        );

        idPrueba = pt.getId();

        // CORREGIDO: Se usa buscarProductoTallaPorId según la interfaz
        ProductoTalla guardada =
                productoTallaDAO.buscarProductoTallaPorId(idPrueba);

        assertNotNull(guardada);
        System.out.println("========================================");
        System.out.println("💾 [TEST GUARDAR] Talla persistida:");
        System.out.println("   - ID: " + guardada.getId());
        System.out.println("   - Talla: " + guardada.getTalla());
        System.out.println("   - Stock: " + guardada.getStockActual());
        System.out.println("   - Producto: " + guardada.getProducto().getNombre());
        System.out.println("========================================");

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

   /** Busca la talla por ID y comprueba sus datos y relación. */
   @Test
   @Order(2)
   public void testBuscarPorId() {

        assertNotNull(idPrueba);

        // CORREGIDO: Se usa buscarProductoTallaPorId según la interfaz
        ProductoTalla pt =
                productoTallaDAO.buscarProductoTallaPorId(idPrueba);

        assertNotNull(pt, "La talla debe existir.");
        System.out.println("========================================");
        System.out.println("🔍 [TEST BUSCAR POR ID] Talla encontrada:");
        System.out.println("   - ID: " + pt.getId());
        System.out.println("   - Talla: " + pt.getTalla());
        System.out.println("   - Stock: " + pt.getStockActual());
        System.out.println("   - ID del producto: " + pt.getProducto().getId());
        System.out.println("========================================");
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

   /** Actualiza el stock y valida que el producto asociado no cambie. */
   @Test
   @Order(3)
   public void testActualizar() throws Exception {

        // CORREGIDO: Se usa buscarProductoTallaPorId
        ProductoTalla pt =
                productoTallaDAO.buscarProductoTallaPorId(idPrueba);

        assertNotNull(pt);

        // Actualizar existencias
        pt.setStockActual(20);

        productoTallaDAO.actualizar(pt);

        // 🔴 BREAKPOINT 2: Inspeccionar el stock actualizado antes de validarlo.
        // CORREGIDO: Se usa buscarProductoTallaPorId
        ProductoTalla actualizado =
                productoTallaDAO.buscarProductoTallaPorId(idPrueba);

        assertNotNull(actualizado);
        System.out.println("✏️ [TEST ACTUALIZAR] Talla " + actualizado.getTalla()
                + " | Stock: " + actualizado.getStockActual());

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

   /** Obtiene todas las tallas y verifica que incluya la talla de prueba. */
   @Test
   @Order(4)
   public void testObtenerTodos() {

        List<ProductoTalla> lista =
                productoTallaDAO.obtenerTodos();
        assertNotNull(lista);

        System.out.println("========================================");
        System.out.println("📋 [TEST OBTENER TODOS] Total de registros: " + lista.size());
        for (ProductoTalla talla : lista) {
            System.out.println("   -> [" + talla.getId() + "] Talla: "
                    + talla.getTalla() + " | Stock: " + talla.getStockActual()
                    + " | Producto: " + talla.getProducto().getNombre());
        }
        System.out.println("========================================");
        assertTrue(
            lista.stream().anyMatch(
                pt -> idPrueba.equals(pt.getId())
            ),
            "La lista debe contener la talla registrada."
        );
   }

   /** Confirma el bloqueo por stock positivo y elimina la talla al dejarlo en cero. */
   @Test
   @Order(5)
   public void testEliminar() throws Exception {

        assertNotNull(idPrueba);

        // No debe permitir eliminar con existencias
        Exception excepcion = assertThrows(
            Exception.class,
            () -> productoTallaDAO.eliminar(idPrueba)
        );

        System.out.println("⚠️ [TEST ELIMINAR] Rechazo esperado: " + excepcion.getMessage());
        assertTrue(
            excepcion.getMessage().contains("existencias"),
            "Debe indicar que hay existencias."
        );

        // Dejar el stock en cero
        // CORREGIDO: Se usa buscarProductoTallaPorId
        ProductoTalla pt =
                productoTallaDAO.buscarProductoTallaPorId(idPrueba);

        assertNotNull(pt);

        pt.setStockActual(0);

        productoTallaDAO.actualizar(pt);

        // 🔴 BREAKPOINT 3: Verificar stock cero antes de permitir la eliminación.
        // Ahora si se permite eliminar
        productoTallaDAO.eliminar(idPrueba);

        // CORREGIDO: Se usa buscarProductoTallaPorId
        ProductoTalla eliminada =
                productoTallaDAO.buscarProductoTallaPorId(idPrueba);

        System.out.println("🗑️ [TEST ELIMINAR] Resultado de búsqueda: " + eliminada);
        assertNull(
            eliminada,
            "La talla debe haberse eliminado."
        );
   }

   /** Elimina los datos auxiliares si una prueba falla antes de completar la limpieza. */
   @AfterAll
   public static void limpiarDatos() throws Exception {

        try {
            // Eliminar talla si alguna prueba fallo
            if (idPrueba != null) {

                // CORREGIDO: Se usa buscarProductoTallaPorId
                ProductoTalla pt =
                        productoTallaDAO.buscarProductoTallaPorId(idPrueba);

                if (pt != null) {
                    pt.setStockActual(0);
                    productoTallaDAO.actualizar(pt);
                    productoTallaDAO.eliminar(idPrueba);
                }
            }

        } finally {

            // Eliminar producto de prueba
            if (idProducto != null) {

                // CORREGIDO: Se usa buscarProductoPorId
                Producto producto =
                        productoDAO.buscarProductoPorId(idProducto);

                if (producto != null) {
                    productoDAO.eliminar(idProducto);
                }
            }
        }
   }
}
