package dominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ramon
 */
public class Venta {
    private int id;
    private String folio;
    private LocalDateTime fechaHora;
    private float subtotal;
    private float iva;
    private float total;
    private Usuario usuario;
    private Pago pago;
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta(int id, String folio, LocalDateTime fechaHora, float subtotal, float iva, float total, Usuario usuario, Pago pago) {
        this.id = id;
        this.folio = folio;
        this.fechaHora = fechaHora;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.usuario = usuario;
        this.pago = pago;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    public String toString() {
        return "Venta{" + "id=" + id + ", folio=" + folio + ", fechaHora=" + fechaHora + ", subtotal=" + subtotal + ", iva=" + iva + ", total=" + total + ", usuario=" + usuario + ", pago=" + pago + ", detalles=" + detalles + '}';
    }
    
}
