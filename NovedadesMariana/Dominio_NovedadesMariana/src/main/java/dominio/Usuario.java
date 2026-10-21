
package dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "usuario", length = 50,
            nullable = false, unique = true)
    private String usuario;

    @Column(name = "contrasenia", length = 255,
            nullable = false)
    private String contrasenia;

    @Column(name = "nombres", length = 50,
            nullable = false)
    private String nombres;

    @Column(name = "primerApellido", length = 50,
            nullable = false)
    private String primerApellido;

    @Column(name = "segundoApellido", length = 50)
    private String segundoApellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "rolEmpleado", nullable = false)
    private RolEmpleado rol;

    // Relacion 1:N con Venta
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Venta> ventas = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String usuario, String contrasenia,
                   String nombres, String primerApellido,
                   String segundoApellido, RolEmpleado rol) {
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

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public RolEmpleado getRol() {
        return rol;
    }

    public void setRol(RolEmpleado rol) {
        this.rol = rol;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Usuario)) return false;

        Usuario other = (Usuario) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id
                + ", usuario=" + usuario
                + ", nombres=" + nombres
                + ", primerApellido=" + primerApellido
                + ", segundoApellido=" + segundoApellido
                + ", rol=" + rol + '}';
    }
}
