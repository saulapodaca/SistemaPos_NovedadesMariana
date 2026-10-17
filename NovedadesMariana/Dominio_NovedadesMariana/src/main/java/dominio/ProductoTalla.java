package dominio;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 *
 * @author Usuario
 */
@Entity
public class ProductoTalla implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "talla", length = 30, nullable = false, unique = true)
    private String talla;
    
    @Column(name = "usuario", nullable = false, unique = true)
    private Integer stockActual;
    
    @OneToMany
    @JoinColumn(name = "idProducto")
    private Producto producto;

    public ProductoTalla() {
    }

    public ProductoTalla(String talla, Integer stockActual, Producto producto) {
        this.talla = talla;
        this.stockActual = stockActual;
        this.producto = producto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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
        if (!(object instanceof ProductoTalla)) {
            return false;
        }
        ProductoTalla other = (ProductoTalla) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProductoTalla{" + "id=" + id + ", talla=" + talla + ", stockActual=" + stockActual + ", producto=" + producto + '}';
    }
    
}
