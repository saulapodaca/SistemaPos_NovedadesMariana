package bos;

import dominio.Usuario;
import dtos.UsuarioDTO;
import implementaciones.UsuarioDAO;
import interfaces.IUsuarioBO;
import interfaces.IUsuarioDAO;
import java.util.List;
import mappers.UsuarioMapper;

/**
 * Business Object para la gestión de usuarios.
 * Integra validaciones de negocio y conversión DTO <-> Entidad mediante UsuarioMapper.
 * 
 * @author saula
 */
public class UsuarioBO implements IUsuarioBO {

    private static UsuarioBO instance;
    private final IUsuarioDAO usuarioDAO;

    private UsuarioBO() {
        this.usuarioDAO = new UsuarioDAO();
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

        Usuario usuarioEncontrado = usuarioDAO.buscarPorUsuario(usuario);
        if (usuarioEncontrado == null) {
            throw new Exception("El usuario no existe.");
        }

        if (!contrasenia.equals(usuarioEncontrado.getContrasenia())) {
            throw new Exception("Contraseña incorrecta.");
        }

        return UsuarioMapper.toDTO(usuarioEncontrado);
    }

    @Override
    public void guardar(UsuarioDTO usuarioDTO) throws Exception {
        validarUsuarioDTO(usuarioDTO);

        if (usuarioDAO.buscarPorUsuario(usuarioDTO.getUsuario()) != null) {
            throw new Exception("El nombre de usuario ya está registrado.");
        }

        Usuario entidad = UsuarioMapper.toEntity(usuarioDTO);
        usuarioDAO.guardar(entidad);
    }

    @Override
    public void actualizar(UsuarioDTO usuarioDTO) throws Exception {
        validarUsuarioDTO(usuarioDTO);

        if (usuarioDTO.getId() == null || usuarioDTO.getId() <= 0) {
            throw new IllegalArgumentException("El ID del usuario es requerido para actualizar.");
        }

        Usuario existente = usuarioDAO.buscarPorId(usuarioDTO.getId());
        if (existente == null) {
            throw new Exception("El usuario que intenta actualizar no existe.");
        }

        Usuario entidad = UsuarioMapper.toEntity(usuarioDTO);
        usuarioDAO.actualizar(entidad);
    }

    @Override
    public void eliminar(Long id) throws Exception {
        validarId(id);

        Usuario existente = usuarioDAO.buscarPorId(id);
        if (existente == null) {
            throw new Exception("El usuario que desea eliminar no existe.");
        }

        usuarioDAO.eliminar(id);
    }

    @Override
    public UsuarioDTO buscarPorId(Long id) throws Exception {
        validarId(id);
        Usuario usuario = usuarioDAO.buscarPorId(id);
        return UsuarioMapper.toDTO(usuario);
    }

    @Override
    public List<UsuarioDTO> obtenerTodos() throws Exception {
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        return UsuarioMapper.toDTOList(usuarios);
    }

    @Override
    public Usuario buscarPorUsuario(String nombreUsuario) throws Exception {
        validarTexto(nombreUsuario, "El nombre de usuario no puede estar vacío.");
        return usuarioDAO.buscarPorUsuario(nombreUsuario);
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