package dominio;

import java.time.LocalDateTime;

/**
 *
 * @author ramon
 */
public class Pago {
    private Integer id;
    private MetodoPago metodoPago;
    private Float monto;
    private Float montoRecibido;
    private Float cambio;
    private LocalDateTime fechaHora;

    public Pago(Integer id, MetodoPago metodoPago, Float monto, Float montoRecibido, Float cambio, LocalDateTime fechaHora) {
        this.id = id;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.montoRecibido = montoRecibido;
        this.cambio = cambio;
        this.fechaHora = fechaHora;
    }

    public Pago(MetodoPago metodoPago, Float monto, Float montoRecibido, Float cambio, LocalDateTime fechaHora) {
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.montoRecibido = montoRecibido;
        this.cambio = cambio;
        this.fechaHora = fechaHora;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
    public String toString() {
        return "Pago{" + "id=" + id + ", metodoPago=" + metodoPago + ", monto=" + monto + ", montoRecibido=" + montoRecibido + ", cambio=" + cambio + ", fechaHora=" + fechaHora + '}';
    }
    
}
