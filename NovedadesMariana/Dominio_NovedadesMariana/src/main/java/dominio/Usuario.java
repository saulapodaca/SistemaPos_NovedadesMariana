package dominio;


/**
 *
 * @author ramon
 */


public class Usuario {

    private int id;
    private String usuario;
    private String contrasenia;
    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private RolEmpleado rol;

    public Usuario(String usuario, String contrasenia, String nombres, String primerApellido, String segundoApellido, RolEmpleado rol) {
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.rol = rol;
    }

    public Usuario(int id, String usuario, String contrasenia, String nombres, String primerApellido, String segundoApellido, RolEmpleado rol) {
        this.id = id;
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public String getNombres() {
        return nombres;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public RolEmpleado getRol() {
        return rol;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public void setRol(RolEmpleado rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", usuario=" + usuario + ", contrasenia=" + contrasenia + ", nombres=" + nombres + ", primerApellido=" + primerApellido + ", segundoApellido=" + segundoApellido + ", rol=" + rol + '}';
    }
    
}
