package interfaces;

public interface IUsuarioDTO {
    Long getId();
    void setId(Long id);
    String getUsuario();
    void setUsuario(String usuario);
    String getContrasenia();
    void setContrasenia(String contrasenia);
    String getNombres();
    void setNombres(String nombres);
    String getPrimerApellido();
    void setPrimerApellido(String primerApellido);
    String getSegundoApellido();
    void setSegundoApellido(String segundoApellido);
    String getRolEmpleado();
    void setRolEmpleado(String rolEmpleado);
}
