package implementaciones;

import bos.UsuarioBO;
import dtos.UsuarioDTO;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioBOTest {

    private BOTestFixture fixture;
    private UsuarioBO usuarioBO;

    @BeforeEach
    void setUp() throws Exception {
        fixture = new BOTestFixture();
        usuarioBO = UsuarioBO.getInstance();
    }

    @AfterEach
    void limpiar() throws Exception {
        fixture.close();
    }

    @Test
    void autenticaCredencialesCorrectasYDevuelveDTO() throws Exception {
        UsuarioDTO autenticado = usuarioBO.autenticar(
                fixture.usuario.getUsuario(), fixture.usuario.getContrasenia());

        assertNotNull(autenticado);
        System.out.println("[BO USUARIO] Empleado autenticado: id=" + autenticado.getId()
                + ", usuario=" + autenticado.getUsuario()
                + ", nombre=" + autenticado.getNombres()
                + ", rol=" + autenticado.getRolEmpleado());
        assertEquals(fixture.usuario.getId(), autenticado.getId());
        assertEquals(fixture.usuario.getUsuario(), autenticado.getUsuario());
        assertNull(autenticado.getContrasenia(), "No debe exponer la contraseña.");
    }

    @Test
    void devuelveNullCuandoLasCredencialesSonIncorrectas() throws Exception {
        UsuarioDTO claveIncorrecta = usuarioBO.autenticar(
                fixture.usuario.getUsuario(), "incorrecta");
        UsuarioDTO usuarioInexistente = usuarioBO.autenticar("usuario_inexistente", "clave");
        System.out.println("[BO USUARIO] Resultados de credenciales incorrectas: clave="
                + claveIncorrecta + ", usuario inexistente=" + usuarioInexistente);
        assertNull(claveIncorrecta);
        assertNull(usuarioInexistente);
    }

    @Test
    void rechazaUsuarioOContraseniaVacios() {
        System.out.println("[BO USUARIO] Se rechazan usuario y contraseña nulos o vacíos.");
        assertThrows(IllegalArgumentException.class,
                () -> usuarioBO.autenticar("  ", "clave"));
        assertThrows(IllegalArgumentException.class,
                () -> usuarioBO.autenticar(fixture.usuario.getUsuario(), " "));
        assertThrows(IllegalArgumentException.class,
                () -> usuarioBO.autenticar(null, "clave"));
        assertThrows(IllegalArgumentException.class,
                () -> usuarioBO.autenticar(fixture.usuario.getUsuario(), null));
    }

    @Test
    void buscaUsuarioYEntregaDTOEnLugarDeLaEntidad() throws Exception {
        UsuarioDTO usuario = usuarioBO.buscarPorUsuario(fixture.usuario.getUsuario());

        assertNotNull(usuario);
        System.out.println("[BO USUARIO] Usuario consultado: id=" + usuario.getId()
                + ", usuario=" + usuario.getUsuario()
                + ", rol=" + usuario.getRolEmpleado());
        assertEquals(fixture.usuario.getId(), usuario.getId());
        assertEquals("EMPLEADO", usuario.getRolEmpleado());
    }

    @Test
    void guardaActualizaYEliminaUsuarioPorLaFachada() throws Exception {
        String nombre = "bo_" + UUID.randomUUID().toString().replace("-", "");
        UsuarioDTO nuevo = new UsuarioDTO(null, nombre, "clave-segura", "Luis",
                "Prueba", "BO", "EMPLEADO");
        usuarioBO.guardar(nuevo);

        UsuarioDTO guardado = usuarioBO.buscarPorUsuario(nombre);
        assertNotNull(guardado);
        guardado.setNombres("Luis Actualizado");
        usuarioBO.actualizar(guardado);
        assertEquals("Luis Actualizado", usuarioBO.buscarPorId(guardado.getId()).getNombres());

        usuarioBO.eliminar(guardado.getId());
        assertNull(usuarioBO.buscarPorId(guardado.getId()));
    }
}
