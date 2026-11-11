package daos;

import dominio.Categoria;
import interfaces.ICategoriaDAO;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias y de integración para validar el comportamiento de
 * la implementación de {@link ICategoriaDAO} utilizando JUnit 5. Las pruebas se
 * ejecutan en un orden secuencial estricto controlado por anotaciones.
 *
 * @author rauln
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoriaDAOTest {

    /**
     * Instancia del DAO de categorías utilizada para realizar las operaciones
     * CRUD en las pruebas.
     */
    private static ICategoriaDAO categoriaDAO;

    /**
     * Identificador único de la categoría creada durante las pruebas para su
     * posterior consulta, actualización y eliminación.
     */
    private static Long idPrueba;

    /**
     * Nombre asignado a la categoría de prueba.
     */
    private static String nombrePrueba;

    /**
     * Configuración inicial que se ejecuta una sola vez antes de todas las
     * pruebas. Inicializa la instancia del DAO y establece el nombre estándar
     * de la categoría de prueba.
     */
    @BeforeAll
    public static void setUp() {
        // Inicializar la implementación del DAO
        categoriaDAO = new CategoriaDAO();
        // Definir un nombre estándar para las pruebas
        nombrePrueba = "Ropa de Verano";
    }

    /**
     * Prueba la funcionalidad de guardado (Create) de una nueva categoría.
     *
     * @throws Exception Si ocurre un error al persistir la categoría en la base
     * de datos de pruebas.
     */
    @Test
    @Order(1)
    public void testGuardar() throws Exception {
        Categoria categoria = new Categoria(
                nombrePrueba,
                "Coleccion de temporada de calor"
        );
        categoriaDAO.guardar(categoria);
        // 🔴 BREAKPOINT 1: Coloca el punto de interrupción aqui.  
        assertNotNull(
                categoria.getId(),
                "El ID no debe ser nulo despues de guardar."
        );
        System.out.println("========================================");
        System.out.println("💾 [TEST GUARDAR] Categoría creada:");
        System.out.println("   - ID: " + categoria.getId());
        System.out.println("   - Nombre: " + categoria.getNombre());
        System.out.println("   - Descripción: " + categoria.getDescripcion());
        System.out.println("========================================");
        idPrueba = categoria.getId();
    }

    /**
     * Prueba la funcionalidad de búsqueda por ID (Read) de una categoría
     * existente. Valida que el registro recuperado no sea nulo y que su nombre
     * coincida con el esperado.
     */
    @Test
    @Order(2)
    public void testBuscarPorId() {
        assertNotNull(idPrueba);
        // CORREGIDO: Se usa buscarCategoriaPorId según la interfaz
        Categoria categoria = categoriaDAO.buscarCategoriaPorId(idPrueba);
        assertNotNull(categoria, "La categoría debe existir.");
        System.out.println("========================================");
        System.out.println("🔍 [TEST BUSCAR POR ID] Categoría encontrada:");
        System.out.println("   - ID: " + categoria.getId());
        System.out.println("   - Nombre: " + categoria.getNombre());
        System.out.println("   - Descripción: " + categoria.getDescripcion());
        System.out.println("========================================");
        assertEquals(
                nombrePrueba,
                categoria.getNombre()
        );
    }

    /**
     * Prueba la funcionalidad de actualización (Update) de una categoría.
     * Modifica la descripción del registro y corrobora que los cambios se hayan
     * guardado exitosamente.
     *
     * @throws Exception Si ocurre un error durante la actualización en la base
     * de datos.
     */
    @Test
    @Order(3)
    public void testActualizar() throws Exception {
        // CORREGIDO: Se usa buscarCategoriaPorId
        Categoria categoria = categoriaDAO.buscarCategoriaPorId(idPrueba);
        assertNotNull(categoria);
        categoria.setDescripcion(
                "Coleccion actualizada"
        );
        categoriaDAO.actualizar(categoria);
        // 🔴 BREAKPOINT 2: Coloca el punto de interrupción aquí.
        // CORREGIDO: Se usa buscarCategoriaPorId
        Categoria actualizada = categoriaDAO.buscarCategoriaPorId(idPrueba);
        assertNotNull(actualizada);
        System.out.println("✏️ [TEST ACTUALIZAR] Categoría consultada:");
        System.out.println("   - ID: " + actualizada.getId());
        System.out.println("   - Nombre: " + actualizada.getNombre());
        System.out.println("   - Descripción: " + actualizada.getDescripcion());
        assertEquals(
                "Coleccion actualizada",
                actualizada.getDescripcion()
        );
    }

    /**
     * Prueba la obtención de todos los registros registrados (Read All).
     * Verifica que la lista devuelta contenga la categoría de prueba creada
     * previamente.
     */
    @Test
    @Order(4)
    public void testObtenerTodos() {
        List<Categoria> categorias = categoriaDAO.obtenerTodas();
        assertNotNull(categorias);
        System.out.println("========================================");
        System.out.println("📋 [TEST OBTENER TODOS] Total de registros: " + categorias.size());
        for (Categoria c : categorias) {
            System.out.println("   -> [" + c.getId() + "] " + c.getNombre() + " (" + c.getDescripcion() + ")");
        }
        System.out.println("========================================");
        assertTrue(
                categorias.stream().anyMatch(
                        c -> c.getId().equals(idPrueba)
                ),
                "La lista debe contener la categoria registrada."
        );
    }

    /**
     * Prueba la funcionalidad de eliminación (Delete) de una categoría. Borra
     * el registro utilizando su ID y valida mediante una búsqueda posterior que
     * ya no exista.
     *
     * @throws Exception Si ocurre un error durante el proceso de eliminación.
     */
    @Test
    @Order(5)
    public void testEliminar() throws Exception {
        assertNotNull(idPrueba);
        categoriaDAO.eliminar(idPrueba);
        // 🔴 BREAKPOINT 3: Coloca el punto de interrupción aquí en NetBeans.
        // CORREGIDO: Se usa buscarCategoriaPorId
        Categoria eliminada = categoriaDAO.buscarCategoriaPorId(idPrueba);
        System.out.println("🗑️ [TEST ELIMINAR] Resultado de búsqueda: " + eliminada);
        assertNull(
                eliminada,
                "La categoria ya no debe existir."
        );
    }
}