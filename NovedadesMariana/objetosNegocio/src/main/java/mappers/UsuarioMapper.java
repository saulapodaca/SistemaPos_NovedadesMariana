package mappers;

import dominio.Usuario;
import dtos.UsuarioDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class UsuarioMapper {

    /**
     * Convierte una entidad Usuario a UsuarioDTO.
     */
    public static UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsuario(usuario.getUsuario());
        // Ajusta los getters/setters según los atributos reales de tu entidad y DTO
        // dto.setNombre(usuario.getNombre());
        // dto.setRol(usuario.getRol());

        return dto;
    }

    /**
     * Convierte un objeto UsuarioDTO a la entidad Usuario.
     */
    public static Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setUsuario(dto.getUsuario());
        // Ajusta los getters/setters según tus atributos
        // usuario.setNombre(dto.getNombre());
        // usuario.setRol(dto.getRol());

        return usuario;
    }

    /**
     * Convierte una lista de entidades Usuario a una lista de UsuarioDTO.
     */
    public static List<UsuarioDTO> toDTOList(List<Usuario> usuarios) {
        if (usuarios == null) {
            return new ArrayList<>();
        }

        List<UsuarioDTO> dtos = new ArrayList<>();
        for (Usuario u : usuarios) {
            dtos.add(toDTO(u));
        }
        return dtos;
    }
}