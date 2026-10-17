package dominio;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Usuario
 */
@Entity
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "sku", nullable = false, length = 50)
    private String sku;
    
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    
    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;
    
    @Column(name = "precio", nullable = false, length = 50)
    private Float precio;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipoPrenda", nullable = false)
    private TipoPrenda tipoPrenda;
    
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "producto")
    private List<ProductoTalla> tallas;
    
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "producto")
    private List<Categoria> categorias;

    public Producto() {
    }
    
    public Producto(String sku, String nombre, String descripcion, Float precio, TipoPrenda tipoPrenda) {
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipoPrenda = tipoPrenda;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public TipoPrenda getTipoPrenda() {
        return tipoPrenda;
    }

    public void setTipoPrenda(TipoPrenda tipoPrenda) {
        this.tipoPrenda = tipoPrenda;
    }

    public List<ProductoTalla> getTallas() {
        return tallas;
    }

    public void setTallas(List<ProductoTalla> tallas) {
        this.tallas = tallas;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
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
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Producto{" + "id=" + id + ", sku=" + sku + ", nombre=" + nombre + ", descripcion=" + descripcion + ", precio=" + precio + ", tipoPrenda=" + tipoPrenda + ", tallas=" + tallas + ", categorias=" + categorias + '}';
    }
    
}
