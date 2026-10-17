package dominio;

/**
 *
 * @author ramon
 */
public class ProductoTalla {
    private Integer id;
    private String talla;
    private Integer stockActual;
    private Producto producto;

    public ProductoTalla(Integer id, String talla, Integer stockActual, Producto producto) {
        this.id = id;
        this.talla = talla;
        this.stockActual = stockActual;
        this.producto = producto;
    }

    public ProductoTalla(String talla, Integer stockActual, Producto producto) {
        this.talla = talla;
        this.stockActual = stockActual;
        this.producto = producto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        this.stockActual = stockActual;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "ProductoTalla{" + "id=" + id + ", talla=" + talla + ", stockActual=" + stockActual + ", producto=" + producto + '}';
    }
    
    
}
