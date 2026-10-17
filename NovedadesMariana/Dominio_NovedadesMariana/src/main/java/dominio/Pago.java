package dominio;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
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
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodoPago", nullable = false)
    private MetodoPago metodoPago;
    
    @Column(name = "monto", nullable = false, length = 50)
    private Float monto;
    
    @Column(name = "montoRecibido", nullable = false, length = 50)
    private Float montoRecibido;
    
    @Column(name = "cambio", nullable = false, length = 50)
    private Float cambio;
    
    @Column(name = "fechaHora", nullable = false, length = 50)
    private LocalDateTime fechaHora;

    public Pago() {
    }
    
    public Pago(MetodoPago metodoPago, Float monto, Float montoRecibido, Float cambio, LocalDateTime fechaHora) {
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.montoRecibido = montoRecibido;
        this.cambio = cambio;
        this.fechaHora = fechaHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Float getMonto() {
        return monto;
    }

    public void setMonto(Float monto) {
        this.monto = monto;
    }

    public Float getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(Float montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public Float getCambio() {
        return cambio;
    }

    public void setCambio(Float cambio) {
        this.cambio = cambio;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
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
        if (!(object instanceof Pago)) {
            return false;
        }
        Pago other = (Pago) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Pago{" + "id=" + id + ", metodoPago=" + metodoPago + ", monto=" + monto + ", montoRecibido=" + montoRecibido + ", cambio=" + cambio + ", fechaHora=" + fechaHora + '}';
    }
    
}