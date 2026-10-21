
package dominio;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(
        name = "uq_producto_talla",
        columnNames = {"idProducto", "talla"}
    )
)
public class ProductoTalla implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "talla", length = 30, nullable = false)
    private String talla;

    @Column(name = "stockActual", nullable = false)
    private Integer stockActual;

    // Relacion N:1 con Producto
    @ManyToOne(optional = false)
    @JoinColumn(name = "idProducto", nullable = false)
    private Producto producto;

    public ProductoTalla() {
    }

    public ProductoTalla(String talla, Integer stockActual,
                         Producto producto) {
        this.talla = talla;
        setStockActual(stockActual);
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
        if (stockActual == null || stockActual < 0) {
            throw new IllegalArgumentException(
                "El stock no puede ser nulo ni negativo."
            );
        }
        this.stockActual = stockActual;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ProductoTalla)) return false;

        ProductoTalla other = (ProductoTalla) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ProductoTalla{id=" + id
                + ", talla=" + talla
                + ", stockActual=" + stockActual + '}';
    }
}
