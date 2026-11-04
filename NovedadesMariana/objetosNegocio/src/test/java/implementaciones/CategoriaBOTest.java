package implementaciones;

import bos.CategoriaBO;
import dtos.CategoriaDTO;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoriaBOTest {

    private BOTestFixture fixture;
    private CategoriaBO categoriaBO;

    @BeforeEach
    void setUp() throws Exception {
        fixture = new BOTestFixture();
        categoriaBO = CategoriaBO.getInstance();
    }

    @AfterEach
    void limpiar() throws Exception {
        fixture.close();
    }

    @Test
    void encuentraCategoriaExistenteYDevuelveNullParaIdValidoInexistente() {
        CategoriaDTO encontrada = categoriaBO.buscarPorId(fixture.categoria.getId());

        assertNotNull(encontrada);
        System.out.println("[BO CATEGORÍA] Consultada: id=" + encontrada.getId()
                + ", nombre=" + encontrada.getNombre()
                + ", descripción=" + encontrada.getDescripcion());
        assertEquals(fixture.categoria.getId(), encontrada.getId());
        assertEquals(fixture.categoria.getNombre(), encontrada.getNombre());
        CategoriaDTO inexistente = categoriaBO.buscarPorId(Long.MAX_VALUE);
        System.out.println("[BO CATEGORÍA] Consulta con ID válido inexistente: " + inexistente);
        assertNull(inexistente);
    }

    @Test
    void rechazaIdsInvalidos() {
        System.out.println("[BO CATEGORÍA] IDs inválidos rechazados: null, 0 y -1.");
        assertThrows(IllegalArgumentException.class,
                () -> categoriaBO.buscarPorId(null));
        assertThrows(IllegalArgumentException.class,
                () -> categoriaBO.buscarPorId(0L));
        assertThrows(IllegalArgumentException.class,
                () -> categoriaBO.obtenerPorId(-1L));
    }

    @Test
    void guardaActualizaYEliminaCategoriaUsandoLaPersistencia() throws Exception {
        String sufijo = UUID.randomUUID().toString();
        CategoriaDTO nueva = new CategoriaDTO("CAT-" + sufijo, "Descripción inicial");
        categoriaBO.guardar(nueva);

        CategoriaDTO guardada = categoriaBO.obtenerTodas().stream()
                .filter(c -> c.getNombre().equals(nueva.getNombre())).findFirst().orElseThrow();
        guardada.setDescripcion("Descripción actualizada");
        categoriaBO.actualizar(guardada);
        assertEquals("Descripción actualizada", categoriaBO.buscarPorId(guardada.getId()).getDescripcion());

        categoriaBO.eliminar(guardada.getId());
        assertNull(categoriaBO.buscarPorId(guardada.getId()));
    }
}
