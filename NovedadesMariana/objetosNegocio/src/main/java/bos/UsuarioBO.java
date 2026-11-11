package bos;

import java.util.ArrayList;

import dominio.Usuario;

import dtos.UsuarioDTO;
import interfaces.IUsuarioBO;
import daos.UsuarioDAO;
import interfaces.IUsuarioDAO;
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
    private final IUsuarioDAO usuarioDAO;

    private UsuarioBO() {
        this(new UsuarioDAO());
    }

    public UsuarioBO(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = java.util.Objects.requireNonNull(usuarioDAO);
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

        UsuarioDTO usuarioEncontrado = UsuarioMapper.toDTO(usuarioDAO.buscarPorUsuario(usuario));
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

        if (usuarioDAO.buscarPorUsuario(usuarioDTO.getUsuario()) != null) {
            throw new Exception("El nombre de usuario ya está registrado.");
        }
        usuarioDAO.guardar(UsuarioMapper.toEntity(usuarioDTO));
    }

    @Override
    public void actualizar(UsuarioDTO usuarioDTO) throws Exception {
        validarUsuarioDTO(usuarioDTO);

        if (usuarioDTO.getId() == null || usuarioDTO.getId() <= 0) {
            throw new IllegalArgumentException("El ID del usuario es requerido para actualizar.");
        }

        UsuarioDTO existente = UsuarioMapper.toDTO(usuarioDAO.buscarPorId(usuarioDTO.getId()));
        if (existente == null) {
            throw new Exception("El usuario que intenta actualizar no existe.");
        }
        UsuarioDTO mismoNombre = UsuarioMapper.toDTO(usuarioDAO.buscarPorUsuario(usuarioDTO.getUsuario()));
        if (mismoNombre != null && !usuarioDTO.getId().equals(mismoNombre.getId())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }
        usuarioDAO.actualizar(UsuarioMapper.toEntity(usuarioDTO));
    }

    @Override
    public void eliminar(Long id) throws Exception {
        validarId(id);

        UsuarioDTO existente = UsuarioMapper.toDTO(usuarioDAO.buscarPorId(id));
        if (existente == null) {
            throw new Exception("El usuario que desea eliminar no existe.");
        }

        usuarioDAO.eliminar(id);
    }

    @Override
    public UsuarioDTO buscarPorId(Long id) throws Exception {
        validarId(id);
        return UsuarioMapper.toDTO(usuarioDAO.buscarPorId(id));
    }

    @Override
    public List<UsuarioDTO> obtenerTodos() throws Exception {
        return convertirListaADTO(usuarioDAO.obtenerTodos());
    }

    @Override
    public UsuarioDTO buscarPorUsuario(String nombreUsuario) throws Exception {
        validarTexto(nombreUsuario, "El nombre de usuario no puede estar vacío.");
        return UsuarioMapper.toDTO(usuarioDAO.buscarPorUsuario(nombreUsuario));
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

    private List<UsuarioDTO> convertirListaADTO(List<Usuario> entidades) {
        List<UsuarioDTO> resultado = new ArrayList<>();
        if (entidades != null) {
            for (Usuario entidad : entidades) {
                resultado.add(UsuarioMapper.toDTO(entidad));
            }
        }
        return resultado;
    }
}
