package bos;

import dtos.UsuarioDTO;
import interfaces.IUsuarioBO;
import fachada.FachadaPersistencia;
import interfaces.IFachadaPersistencia;
import mappers.UsuarioMapper;
import java.util.List;

/**
 * Business Object para la gestión de usuarios.
 * Integra reglas de negocio y delega el acceso mediante el puerto de usuarios.
 * 
 * @author saula
 */
public class UsuarioBO implements IUsuarioBO {

    private static UsuarioBO instance;
    private final IFachadaPersistencia persistencia;

    private UsuarioBO() {
        this(FachadaPersistencia.getInstance());
    }

    public UsuarioBO(IFachadaPersistencia persistencia) {
        this.persistencia = java.util.Objects.requireNonNull(persistencia);
    }

    public static synchronized UsuarioBO getInstance() {
        if (instance == null) {
            instance = new UsuarioBO();
        }
        return instance;
    }

    @Override
    public UsuarioDTO autenticar(String usuario, String contrasenia) throws Exception {
        validarTexto(usuario, "El nombre de usuario es obligatorio.");
        validarTexto(contrasenia, "La contraseña es obligatoria.");

        UsuarioDTO usuarioEncontrado = UsuarioMapper.toDTO(persistencia.buscarUsuarioParaAutenticacion(usuario));
        if (usuarioEncontrado == null
                || !contrasenia.equals(usuarioEncontrado.getContrasenia())) {
            return null;
        }
        return new UsuarioDTO(usuarioEncontrado.getId(), usuarioEncontrado.getUsuario(),
                null, usuarioEncontrado.getNombres(),
                usuarioEncontrado.getPrimerApellido(),
                usuarioEncontrado.getSegundoApellido(),
                usuarioEncontrado.getRolEmpleado());
    }

    @Override
    public void guardar(UsuarioDTO usuarioDTO) throws Exception {
        validarUsuarioDTO(usuarioDTO);

        if (usuarioDTO.getId() != null) {
            throw new IllegalArgumentException("Un usuario nuevo no debe incluir un ID asignado.");
        }

        if (persistencia.buscarUsuarioPorNombre(usuarioDTO.getUsuario()) != null) {
            throw new Exception("El nombre de usuario ya está registrado.");
        }
        persistencia.guardarUsuario(UsuarioMapper.toEntity(usuarioDTO));
    }

    @Override
    public void actualizar(UsuarioDTO usuarioDTO) throws Exception {
        validarUsuarioDTO(usuarioDTO);

        if (usuarioDTO.getId() == null || usuarioDTO.getId() <= 0) {
            throw new IllegalArgumentException("El ID del usuario es requerido para actualizar.");
        }

        UsuarioDTO existente = UsuarioMapper.toDTO(persistencia.buscarUsuarioPorId(usuarioDTO.getId()));
        if (existente == null) {
            throw new Exception("El usuario que intenta actualizar no existe.");
        }
        UsuarioDTO mismoNombre = UsuarioMapper.toDTO(persistencia.buscarUsuarioPorNombre(usuarioDTO.getUsuario()));
        if (mismoNombre != null && !usuarioDTO.getId().equals(mismoNombre.getId())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }
        persistencia.actualizarUsuario(UsuarioMapper.toEntity(usuarioDTO));
    }

    @Override
    public void eliminar(Long id) throws Exception {
        validarId(id);

        UsuarioDTO existente = UsuarioMapper.toDTO(persistencia.buscarUsuarioPorId(id));
        if (existente == null) {
            throw new Exception("El usuario que desea eliminar no existe.");
        }

        persistencia.eliminarUsuario(id);
    }

    @Override
    public UsuarioDTO buscarPorId(Long id) throws Exception {
        validarId(id);
        return UsuarioMapper.toDTO(persistencia.buscarUsuarioPorId(id));
    }

    @Override
    public List<UsuarioDTO> obtenerTodos() throws Exception {
        return UsuarioMapper.toDTOList(persistencia.obtenerUsuarios());
    }

    @Override
    public UsuarioDTO buscarPorUsuario(String nombreUsuario) throws Exception {
        validarTexto(nombreUsuario, "El nombre de usuario no puede estar vacío.");
        return UsuarioMapper.toDTO(persistencia.buscarUsuarioPorNombre(nombreUsuario));
    }

    // =========================================================================
    // MÉTODOS PRIVADOS DE VALIDACIÓN
    // =========================================================================

    private void validarUsuarioDTO(UsuarioDTO dto) throws IllegalArgumentException {
        if (dto == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        validarTexto(dto.getUsuario(), "El nombre de usuario es obligatorio.");
        validarTexto(dto.getContrasenia(), "La contraseña es obligatoria.");
    }

    private void validarTexto(String texto, String mensajeError) throws IllegalArgumentException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
    }

    private void validarId(Long id) throws IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID proporcionado no es válido.");
        }
    }
}
