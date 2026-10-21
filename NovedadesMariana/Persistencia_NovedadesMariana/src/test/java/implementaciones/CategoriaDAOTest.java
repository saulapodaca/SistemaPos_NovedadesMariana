
package implementaciones;

import dominio.Categoria;
import interfaces.ICategoriaDAO;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoriaDAOTest {

    private static ICategoriaDAO categoriaDAO;
    private static Long idPrueba;
    private static String nombrePrueba;

    @BeforeAll
    public static void setUp() {
        categoriaDAO = new CategoriaDAO();

        // Nombre unico para evitar duplicados
        nombrePrueba = "Ropa de Verano-"
                + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @Order(1)
    public void testGuardar() throws Exception {

        Categoria categoria = new Categoria(
            nombrePrueba,
            "Coleccion de temporada de calor"
        );

        categoriaDAO.guardar(categoria);

        assertNotNull(
            categoria.getId(),
            "El ID no debe ser nulo despues de guardar."
        );

        idPrueba = categoria.getId();
    }

    @Test
    @Order(2)
    public void testBuscarPorId() {

        assertNotNull(idPrueba);

        Categoria categoria = categoriaDAO.buscarPorId(idPrueba);

        assertNotNull(
            categoria,
            "La categoria debe existir."
        );

        assertEquals(
            nombrePrueba,
            categoria.getNombre()
        );
    }

    @Test
    @Order(3)
    public void testActualizar() throws Exception {

        Categoria categoria = categoriaDAO.buscarPorId(idPrueba);

        assertNotNull(categoria);

        categoria.setDescripcion(
            "Coleccion actualizada"
        );

        categoriaDAO.actualizar(categoria);

        Categoria actualizada = categoriaDAO.buscarPorId(idPrueba);

        assertNotNull(actualizada);

        assertEquals(
            "Coleccion actualizada",
            actualizada.getDescripcion()
        );
    }

    @Test
    @Order(4)
    public void testObtenerTodos() {

        List<Categoria> categorias = categoriaDAO.obtenerTodas();

        assertNotNull(categorias);

        assertTrue(
            categorias.stream().anyMatch(
                c -> c.getId().equals(idPrueba)
            ),
            "La lista debe contener la categoria registrada."
        );
    }

    @Test
    @Order(5)
    public void testEliminar() throws Exception {

        assertNotNull(idPrueba);

        categoriaDAO.eliminar(idPrueba);

        Categoria eliminada = categoriaDAO.buscarPorId(idPrueba);

        assertNull(
            eliminada,
            "La categoria ya no debe existir."
        );
    }
}
