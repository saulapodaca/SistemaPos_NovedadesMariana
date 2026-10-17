package dominio;

/**
 *
 * @author ramon
 */
public class DetalleVenta {
    private int id;
    private int cantidad;
    private float precioUnitario;
    private float subtotal;
    private ProductoTalla productoTalla;

    public DetalleVenta(int id, int cantidad, float precioUnitario, float subtotal, ProductoTalla productoTalla) {
        this.id = id;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.productoTalla = productoTalla;
    }

    public DetalleVenta(int cantidad, float precioUnitario, float subtotal, ProductoTalla productoTalla) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.productoTalla = productoTalla;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public ProductoTalla getProductoTalla() {
        return productoTalla;
    }

    public void setProductoTalla(ProductoTalla productoTalla) {
        this.productoTalla = productoTalla;
    }

    @Override
    public String toString() {
        return "DetalleVenta{" + "id=" + id + ", cantidad=" + cantidad + ", precioUnitario=" + precioUnitario + ", subtotal=" + subtotal + ", productoTalla=" + productoTalla + '}';
    }
    
     
}
