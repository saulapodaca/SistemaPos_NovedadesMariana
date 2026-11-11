package utilerias;

import dtos.UsuarioDTO;
import java.util.Objects;

/** Sesión compartida por los controles y las pantallas de presentación. */
public final class GestorSesion {
    private static final GestorSesion INSTANCE = new GestorSesion();
    // Debe coincidir con el empleado de scripts/seed.sql.
    public static final long ID_EMPLEADO_MOCK = 900001L;
    private UsuarioDTO empleado;

    private GestorSesion() { }

    public static GestorSesion getInstance() { return INSTANCE; }

    public synchronized void iniciarSesionMock() {
        iniciarSesion(new UsuarioDTO(ID_EMPLEADO_MOCK, "demo.diana", null,
                "Diana", "González", "López", "EMPLEADO"));
    }

    public synchronized void iniciarSesion(UsuarioDTO usuario) {
        Objects.requireNonNull(usuario, "El empleado es obligatorio.");
        if (usuario.getId() == null || usuario.getId() <= 0) {
            throw new IllegalArgumentException("El empleado debe tener un ID válido.");
        }
        empleado = copiar(usuario);
    }

    public synchronized UsuarioDTO getEmpleadoActual() {
        return empleado == null ? null : copiar(empleado);
    }

    public synchronized boolean haySesionActiva() { return empleado != null; }

    public synchronized void cerrarSesion() { empleado = null; }

    private static UsuarioDTO copiar(UsuarioDTO usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getUsuario(), null,
                usuario.getNombres(), usuario.getPrimerApellido(), usuario.getSegundoApellido(),
                usuario.getRolEmpleado());
    }
}