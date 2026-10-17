package dominio;
import jakarta.persistence.Column;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Usuario
 */
@Entity
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "usuario", length = 50, nullable = false, unique = true)
    private String usuario;
    
    @Column(name = "contraseña", length = 50, nullable = false, unique = true)
    private String contrasenia;
    
    @Column(name = "nombres", length = 50, nullable = false, unique = true)
    private String nombres;
    
    @Column(name = "primerApellido", length = 50, nullable = false, unique = true)
    private String primerApellido;
    
    @Column(name = "segundoApellido", length = 50, nullable = false, unique = true)
    private String segundoApellido;
    
    @Enumerated(EnumType.STRING)
    @javax.persistence.Column(name = "rolEmpleado", nullable = false)
    private RolEmpleado rol;

    public Usuario() {
    }
    
    public Usuario(String usuario, String contrasenia, String nombres, String primerApellido, String segundoApellido, RolEmpleado rol) {
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", usuario=" + usuario + ", contrasenia=" + contrasenia + ", nombres=" + nombres + ", primerApellido=" + primerApellido + ", segundoApellido=" + segundoApellido + ", rol=" + rol + '}';
    }
    
}
