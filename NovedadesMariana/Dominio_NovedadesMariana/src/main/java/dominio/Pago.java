package dominio;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodoPago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "monto", nullable = false)
    private Float monto;

    @Column(name = "montoRecibido")
    private Float montoRecibido;

    @Column(name = "cambio")
    private Float cambio;

    @Column(name = "fechaHora", nullable = false)
    private LocalDateTime fechaHora;

    public Pago() {
    }

    public Pago(MetodoPago metodoPago, Float monto,
                Float montoRecibido, Float cambio,
                LocalDateTime fechaHora) {
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pago)) return false;

        Pago other = (Pago) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Pago{id=" + id
                + ", metodoPago=" + metodoPago
                + ", monto=" + monto
                + ", montoRecibido=" + montoRecibido
                + ", cambio=" + cambio
                + ", fechaHora=" + fechaHora + '}';
    }
}