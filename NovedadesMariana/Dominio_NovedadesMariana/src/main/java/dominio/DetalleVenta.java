package dominio;

import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Usuario
 */
@Entity
public class DetalleVenta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "cantidad", length = 50, nullable = false, unique = true)
    private int cantidad;
    
    @Column(name = "precioUnitario", length = 50, nullable = false, unique = true)
    private float precioUnitario;
    
    @Column(name = "subtotal", length = 50, nullable = false, unique = true)
    private float subtotal;
    
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "detalle")
    private List<ProductoTalla> tallas;

    public DetalleVenta() {
    }
    
    public DetalleVenta(int cantidad, float precioUnitario, float subtotal, List<ProductoTalla> tallas) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.tallas = tallas;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public List<ProductoTalla> gettallas() {
        return tallas;
    }

    public void settallas(List<ProductoTalla> tallas) {
        this.tallas = tallas;
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
        if (!(object instanceof DetalleVenta)) {
            return false;
        }
        DetalleVenta other = (DetalleVenta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DetalleVenta{" + "id=" + id + ", cantidad=" + cantidad + ", precioUnitario=" + precioUnitario + ", subtotal=" + subtotal + ", tallas=" + tallas + '}';
    }
     
}
