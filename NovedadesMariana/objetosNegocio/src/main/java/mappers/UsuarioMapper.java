package mappers;

import dominio.Usuario;
import dtos.UsuarioDTO;

public final class UsuarioMapper {
    private UsuarioMapper() { }
    public static UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioDTO(usuario.getId(), usuario.getUsuario(), usuario.getContrasenia(), usuario.getNombres(),
                usuario.getPrimerApellido(), usuario.getSegundoApellido(), usuario.getRol() == null ? null : usuario.getRol().name());
    }
    public static Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId()); usuario.setUsuario(dto.getUsuario()); usuario.setContrasenia(dto.getContrasenia());
        usuario.setNombres(dto.getNombres()); usuario.setPrimerApellido(dto.getPrimerApellido()); usuario.setSegundoApellido(dto.getSegundoApellido());
        if (dto.getRolEmpleado() != null && !dto.getRolEmpleado().isBlank()) usuario.setRol(enums.RolEmpleado.valueOf(dto.getRolEmpleado()));
        return usuario;
    }
}
