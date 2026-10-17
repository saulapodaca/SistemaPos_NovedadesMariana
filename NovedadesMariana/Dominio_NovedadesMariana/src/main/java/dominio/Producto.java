package dominio;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ramon
 */
public class Producto {
    private Integer id;
    private String sku;
    private String nombre;
    private String descripcion;
    private Float precio;
    private TipoPrenda tipoPrenda;
    private List<ProductoTalla> tallas = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();

    public Producto(Integer id, String sku, String nombre, String descripcion, Float precio, TipoPrenda tipoPrenda) {
        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipoPrenda = tipoPrenda;
    }

    public Producto(String sku, String nombre, String descripcion, Float precio, TipoPrenda tipoPrenda) {
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipoPrenda = tipoPrenda;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public TipoPrenda getTipoPrenda() {
        return tipoPrenda;
    }

    public void setTipoPrenda(TipoPrenda tipoPrenda) {
        this.tipoPrenda = tipoPrenda;
    }

    public List<ProductoTalla> getTallas() {
        return tallas;
    }

    public void setTallas(List<ProductoTalla> tallas) {
        this.tallas = tallas;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    @Override
    public String toString() {
        return "Producto{" + "id=" + id + ", sku=" + sku + ", nombre=" + nombre + ", descripcion=" + descripcion + ", precio=" + precio + ", tipoPrenda=" + tipoPrenda + ", tallas=" + tallas + ", categorias=" + categorias + '}';
    }
    
}
