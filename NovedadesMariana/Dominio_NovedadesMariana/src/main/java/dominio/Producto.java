
package dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.persistence.*;

@Entity
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "sku", nullable = false,
            length = 50, unique = true)
    private String sku;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "precio", nullable = false)
    private Float precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoPrenda", nullable = false)
    private TipoPrenda tipoPrenda;

    // Relacion N:M con Categoria
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "producto_categoria",
        joinColumns = @JoinColumn(name = "idProducto"),
        inverseJoinColumns = @JoinColumn(name = "idCategoria")
    )
    private Set<Categoria> categorias = new HashSet<>();

    // Relacion 1:N con ProductoTalla
    @OneToMany(
        mappedBy = "producto",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ProductoTalla> tallas = new ArrayList<>();

    // Relacion 1:N con DetalleVenta
    @OneToMany(mappedBy = "producto")
    private List<DetalleVenta> detallesVenta = new ArrayList<>();

    public Producto() {
    }

    public Producto(String sku, String nombre,
                    String descripcion, Float precio,
                    TipoPrenda tipoPrenda) {
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipoPrenda = tipoPrenda;
    }

    // Agregar una categoria
    public void agregarCategoria(Categoria categoria) {
        if (categoria != null && categorias.add(categoria)) {
            categoria.getProductos().add(this);
        }
    }

    // Eliminar una categoria
    public void eliminarCategoria(Categoria categoria) {
        if (categoria != null && categorias.remove(categoria)) {
            categoria.getProductos().remove(this);
        }
    }

    // Agregar una talla
    public void agregarTalla(ProductoTalla talla) {
        if (talla != null && !tallas.contains(talla)) {
            tallas.add(talla);
            talla.setProducto(this);
        }
    }

    // Clave primaria
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // SKU
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    // Nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Descripcion
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Precio
    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    // Tipo de prenda
    public TipoPrenda getTipoPrenda() {
        return tipoPrenda;
    }

    public void setTipoPrenda(TipoPrenda tipoPrenda) {
        this.tipoPrenda = tipoPrenda;
    }

    // Categorias
    public Set<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<Categoria> categorias) {
        Set<Categoria> nuevas = categorias == null
                ? new HashSet<>()
                : new HashSet<>(categorias);

        for (Categoria categoria : new HashSet<>(this.categorias)) {
            eliminarCategoria(categoria);
        }

        for (Categoria categoria : nuevas) {
            agregarCategoria(categoria);
        }
    }

    // Tallas
    public List<ProductoTalla> getTallas() {
        return tallas;
    }

    public void setTallas(List<ProductoTalla> tallas) {
        List<ProductoTalla> nuevas = tallas == null
                ? new ArrayList<>()
                : new ArrayList<>(tallas);

        for (ProductoTalla talla : this.tallas) {
            if (!nuevas.contains(talla)) {
                talla.setProducto(null);
            }
        }

        this.tallas.clear();

        for (ProductoTalla talla : nuevas) {
            agregarTalla(talla);
        }
    }

    // Detalles de venta
    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Producto)) return false;

        Producto other = (Producto) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Producto{id=" + id
                + ", sku=" + sku
                + ", nombre=" + nombre
                + ", descripcion=" + descripcion
                + ", precio=" + precio
                + ", tipoPrenda=" + tipoPrenda + '}';
    }
}
