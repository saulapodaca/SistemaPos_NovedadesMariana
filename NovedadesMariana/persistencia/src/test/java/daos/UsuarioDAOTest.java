
package daos;

import enums.RolEmpleado;
import dominio.Usuario;
import interfaces.IUsuarioDAO;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para validar las operaciones CRUD de {@link IUsuarioDAO}.
 * El orden secuencial permite reutilizar el usuario creado durante las pruebas.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioDAOTest {

    /** DAO utilizado para ejecutar las operaciones de persistencia. */
    private static IUsuarioDAO usuarioDAO;

    /** Implementación concreta, necesaria para probar búsquedas específicas. */
    private static UsuarioDAO implementacion;

    /** ID del usuario de prueba, reutilizado en las pruebas posteriores. */
    private static Long idPrueba;

    /** Nombre único para evitar colisiones con datos existentes. */
    private static String nombreUsuarioPrueba;

    /** Inicializa el DAO y genera un nombre de usuario único para la prueba. */
    @BeforeAll
    public static void setUp() {

        implementacion = new UsuarioDAO();
        usuarioDAO = implementacion;

        String codigo = UUID.randomUUID()
                .toString().substring(0, 8);

        nombreUsuarioPrueba = "jlopez_" + codigo;
    }

    /** Guarda un usuario y valida que pueda recuperarse con sus datos iniciales. */
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

        // 🔴 BREAKPOINT 1: Inspeccionar el ID generado después de guardar.
        assertNotNull(
            usuario.getId(),
            "El usuario debe generar un ID."
        );

        idPrueba = usuario.getId();

        Usuario guardado =
                usuarioDAO.buscarPorId(idPrueba);

        assertNotNull(guardado);
        System.out.println("========================================");
        System.out.println("💾 [TEST GUARDAR] Usuario persistido:");
        System.out.println("   - ID: " + guardado.getId());
        System.out.println("   - Usuario: " + guardado.getUsuario());
        System.out.println("   - Nombre: " + guardado.getNombres());
        System.out.println("   - Rol: " + guardado.getRol());
        System.out.println("========================================");

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

    /** Busca el usuario de prueba por su identificador y valida sus datos. */
    @Test
    @Order(2)
    public void testBuscarPorId() {

        assertNotNull(idPrueba);

        Usuario usuario =
                usuarioDAO.buscarPorId(idPrueba);

        assertNotNull(usuario, "El usuario debe existir.");
        System.out.println("========================================");
        System.out.println("🔍 [TEST BUSCAR POR ID] Usuario encontrado:");
        System.out.println("   - ID: " + usuario.getId());
        System.out.println("   - Usuario: " + usuario.getUsuario());
        System.out.println("   - Rol: " + usuario.getRol());
        System.out.println("========================================");

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

    /** Valida la búsqueda por nombre y el resultado para un nombre inexistente. */
    @Test
    @Order(3)
    public void testBuscarPorUsuario() {

        Usuario usuario = implementacion
                .buscarPorUsuario(nombreUsuarioPrueba);

        assertNotNull(usuario, "Debe encontrar el usuario por su nombre.");
        System.out.println("🔍 [TEST BUSCAR POR USUARIO] ID encontrado: " + usuario.getId());
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

        System.out.println("🔍 [TEST BUSCAR POR USUARIO] Resultado inexistente: " + inexistente);
        assertNull(inexistente);
    }

    /** Actualiza datos personales y comprueba que el cambio se conserve. */
    @Test
    @Order(4)
    public void testActualizar() throws Exception {

        Usuario usuario =
                usuarioDAO.buscarPorId(idPrueba);

        assertNotNull(usuario);

        usuario.setNombres("Juanito");
        usuario.setPrimerApellido("Lopez Garcia");

        usuarioDAO.actualizar(usuario);

        // 🔴 BREAKPOINT 2: Revisar los valores enviados antes de volver a consultar.
        Usuario actualizado =
                usuarioDAO.buscarPorId(idPrueba);

        assertNotNull(actualizado);
        System.out.println("========================================");
        System.out.println("✏️ [TEST ACTUALIZAR] Usuario actualizado:");
        System.out.println("   - Nombre: " + actualizado.getNombres());
        System.out.println("   - Primer apellido: " + actualizado.getPrimerApellido());
        System.out.println("   - Rol: " + actualizado.getRol());
        System.out.println("========================================");

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

    /** Obtiene todos los usuarios y comprueba que incluya el registro de prueba. */
    @Test
    @Order(5)
    public void testObtenerTodos() {

        List<Usuario> usuarios =
                usuarioDAO.obtenerTodos();
        assertNotNull(usuarios);

        System.out.println("========================================");
        System.out.println("📋 [TEST OBTENER TODOS] Total de registros: " + usuarios.size());
        for (Usuario u : usuarios) {
            System.out.println("   -> [" + u.getId() + "] " + u.getUsuario()
                    + " - " + u.getNombres() + " " + u.getPrimerApellido());
        }
        System.out.println("========================================");
        assertTrue(
            usuarios.stream().anyMatch(
                u -> idPrueba.equals(u.getId())
            ),
            "La lista debe contener el usuario registrado."
        );
    }

    /** Verifica que la baja conserva el registro inactivo. */
    @Test
    @Order(6)
    public void desactivarConservaElRegistro() throws Exception {
        assertTrue(usuarioDAO.buscarPorId(idPrueba).isActivo());
        usuarioDAO.eliminar(idPrueba);
        usuarioDAO.eliminar(idPrueba);
        Usuario inactivo = usuarioDAO.buscarPorUsuario(nombreUsuarioPrueba);
        assertNotNull(inactivo);
        assertEquals(idPrueba, inactivo.getId());
        assertFalse(inactivo.isActivo());
    }

    @AfterAll
    public static void limpiarDatos() {
        if (idPrueba == null) return;
        try (var em = conexion.ConexionJPA.getInstance().getEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            try {
                Usuario usuario = em.find(Usuario.class, idPrueba);
                if (usuario != null && nombreUsuarioPrueba.equals(usuario.getUsuario())) {
                    em.remove(usuario);
                }
                tx.commit();
            } finally {
                if (tx.isActive()) tx.rollback();
            }
        }
    }
}
