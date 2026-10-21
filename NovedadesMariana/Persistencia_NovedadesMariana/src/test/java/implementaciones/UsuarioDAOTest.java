
package implementaciones;

import dominio.RolEmpleado;
import dominio.Usuario;
import interfaces.IUsuarioDAO;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioDAOTest {

    private static IUsuarioDAO usuarioDAO;
    private static UsuarioDAO implementacion;

    private static Long idPrueba;
    private static String nombreUsuarioPrueba;

    @BeforeAll
    public static void setUp() {

        implementacion = new UsuarioDAO();
        usuarioDAO = implementacion;

        String codigo = UUID.randomUUID()
                .toString().substring(0, 8);

        nombreUsuarioPrueba = "jlopez_" + codigo;
    }

    @Test
    @Order(1)
    public void testGuardar() throws Exception {

        Usuario usuario = new Usuario(
            nombreUsuarioPrueba,
            "HASH_SIMULADO_SOLO_PARA_PRUEBA",
            "Juan",
            "Lopez",
            "Perez",
            RolEmpleado.EMPLEADO
        );

        usuarioDAO.guardar(usuario);

        assertNotNull(
            usuario.getId(),
            "El usuario debe generar un ID."
        );

        idPrueba = usuario.getId();

        Usuario guardado =
                usuarioDAO.buscarPorId(idPrueba);

        assertNotNull(guardado);

        assertEquals(
            nombreUsuarioPrueba,
            guardado.getUsuario()
        );

        assertEquals("Juan", guardado.getNombres());
        assertEquals("Lopez", guardado.getPrimerApellido());
        assertEquals("Perez", guardado.getSegundoApellido());

        assertEquals(
            RolEmpleado.EMPLEADO,
            guardado.getRol()
        );
    }

    @Test
    @Order(2)
    public void testBuscarPorId() {

        assertNotNull(idPrueba);

        Usuario usuario =
                usuarioDAO.buscarPorId(idPrueba);

        assertNotNull(
            usuario,
            "El usuario debe existir."
        );

        assertEquals(
            nombreUsuarioPrueba,
            usuario.getUsuario()
        );

        assertEquals(
            RolEmpleado.EMPLEADO,
            usuario.getRol()
        );
    }

    @Test
    @Order(3)
    public void testBuscarPorUsuario() {

        Usuario usuario = implementacion
                .buscarPorUsuario(nombreUsuarioPrueba);

        assertNotNull(
            usuario,
            "Debe encontrar el usuario por su nombre."
        );

        assertEquals(
            idPrueba,
            usuario.getId()
        );

        // Verificar que un usuario inexistente
        // devuelve null
        Usuario inexistente = implementacion
                .buscarPorUsuario(
                    "inexistente_" + UUID.randomUUID()
                );

        assertNull(inexistente);
    }

    @Test
    @Order(4)
    public void testActualizar() throws Exception {

        Usuario usuario =
                usuarioDAO.buscarPorId(idPrueba);

        assertNotNull(usuario);

        usuario.setNombres("Juanito");
        usuario.setPrimerApellido("Lopez Garcia");

        usuarioDAO.actualizar(usuario);

        Usuario actualizado =
                usuarioDAO.buscarPorId(idPrueba);

        assertNotNull(actualizado);

        assertEquals(
            "Juanito",
            actualizado.getNombres()
        );

        assertEquals(
            "Lopez Garcia",
            actualizado.getPrimerApellido()
        );

        // Verificar que conserva su rol
        assertEquals(
            RolEmpleado.EMPLEADO,
            actualizado.getRol()
        );
    }

    @Test
    @Order(5)
    public void testObtenerTodos() {

        List<Usuario> usuarios =
                usuarioDAO.obtenerTodos();

        assertNotNull(usuarios);

        assertTrue(
            usuarios.stream().anyMatch(
                u -> idPrueba.equals(u.getId())
            ),
            "La lista debe contener el usuario registrado."
        );
    }

    @Test
    @Order(6)
    public void testEliminar() throws Exception {

        assertNotNull(idPrueba);

        usuarioDAO.eliminar(idPrueba);

        Usuario eliminado =
                usuarioDAO.buscarPorId(idPrueba);

        assertNull(
            eliminado,
            "El usuario debe haberse eliminado."
        );
    }

    @AfterAll
    public static void limpiarDatos() throws Exception {

        if (idPrueba != null) {

            Usuario usuario =
                    usuarioDAO.buscarPorId(idPrueba);

            if (usuario != null) {
                usuarioDAO.eliminar(idPrueba);
            }
        }
    }
}
