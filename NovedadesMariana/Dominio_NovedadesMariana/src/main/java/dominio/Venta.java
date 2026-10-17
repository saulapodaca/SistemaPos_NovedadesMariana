package dominio;
import jakarta.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
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
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "folio", length = 50, nullable = false, unique = true)
    private String folio;
    
    @Column(name = "fechaHora", length = 50, nullable = false, unique = true)
    private LocalDateTime fechaHora;
    
    @Column(name = "subtotal", length = 50, nullable = false, unique = true)
    private float subtotal;
    
    @Column(name = "iva", length = 50, nullable = false, unique = true)
    private float iva;
    
    @Column(name = "total", length = 50, nullable = false, unique = true)
    private float total;
    
    @OneToMany
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;
    
    @OneToMany
    @JoinColumn(name = "idPago")
    private Pago pago;
    
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "venta")
    private List<DetalleVenta> detalles;

    public Venta() {
    }
    
    public Venta(String folio, LocalDateTime fechaHora, float subtotal, float iva, float total, Usuario usuario, Pago pago) {
        this.folio = folio;
        this.fechaHora = fechaHora;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.usuario = usuario;
        this.pago = pago;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
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
        if (!(object instanceof Venta)) {
            return false;
        }
        Venta other = (Venta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Venta{" + "id=" + id + ", folio=" + folio + ", fechaHora=" + fechaHora + ", subtotal=" + subtotal + ", iva=" + iva + ", total=" + total + ", usuario=" + usuario + ", pago=" + pago + ", detalles=" + detalles + '}';
    }
    
}
