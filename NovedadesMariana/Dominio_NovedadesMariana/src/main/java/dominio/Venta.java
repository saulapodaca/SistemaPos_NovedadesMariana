
package dominio;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "folio", length = 50,
            nullable = false, unique = true)
    private String folio;

    @Column(name = "fechaHora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "subtotal", nullable = false)
    private float subtotal;

    @Column(name = "iva", nullable = false)
    private float iva;

    @Column(name = "total", nullable = false)
    private float total;

    // Relacion N:1 con Usuario
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    // Relacion 1:1 con Pago
    @OneToOne(
        cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
        },
        optional = false
    )
    @JoinColumn(
        name = "idPago",
        nullable = false,
        unique = true
    )
    private Pago pago;

    // Relacion 1:N con DetalleVenta
    @OneToMany(
        mappedBy = "venta",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {
    }

    public Venta(String folio, LocalDateTime fechaHora,
                 float subtotal, float iva, float total,
                 Usuario usuario, Pago pago) {
        this.folio = folio;
        this.fechaHora = fechaHora;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.usuario = usuario;
        this.pago = pago;
    }

    // Agregar un detalle a la venta
    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle != null) {
            if (!detalles.contains(detalle)) {
                detalles.add(detalle);
            }
            detalle.setVenta(this);
        }
    }

    // Clave primaria
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Folio
    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    // Fecha y hora
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    // Subtotal
    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    // IVA
    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

    // Total
    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    // Usuario
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Pago
    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    // Detalles de venta
    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> nuevosDetalles) {

        List<DetalleVenta> copia = nuevosDetalles == null
                ? new ArrayList<>()
                : new ArrayList<>(nuevosDetalles);

        // Quitar detalles que ya no pertenecen a la venta
        for (DetalleVenta detalle : new ArrayList<>(this.detalles)) {
            if (!copia.contains(detalle)) {
                this.detalles.remove(detalle);
                detalle.setVenta(null);
            }
        }

        // Agregar detalles y mantener la relacion
        for (DetalleVenta detalle : copia) {
            agregarDetalle(detalle);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Venta)) return false;

        Venta other = (Venta) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Venta{id=" + id
                + ", folio=" + folio
                + ", fechaHora=" + fechaHora
                + ", subtotal=" + subtotal
                + ", iva=" + iva
                + ", total=" + total + '}';
    }
}
