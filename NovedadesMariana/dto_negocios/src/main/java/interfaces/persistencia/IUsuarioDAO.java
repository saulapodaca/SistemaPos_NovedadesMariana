package interfaces.persistencia;

import dtos.UsuarioDTO;
import java.util.List;

public interface IUsuarioDAO {
    void guardarDTO(UsuarioDTO usuario) throws Exception;
    void actualizarDTO(UsuarioDTO usuario) throws Exception;
    void eliminar(Long id) throws Exception;
    UsuarioDTO buscarPorIdDTO(Long id);
    List<UsuarioDTO> obtenerTodosDTO();
    UsuarioDTO buscarPorUsuarioDTO(String usuario);
    UsuarioDTO buscarParaAutenticacionDTO(String usuario);
}
