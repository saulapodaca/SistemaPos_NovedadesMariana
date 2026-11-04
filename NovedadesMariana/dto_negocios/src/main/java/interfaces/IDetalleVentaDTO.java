package interfaces;

public interface IDetalleVentaDTO {
    int getId();
    void setId(int id);
    int getIdVenta();
    void setIdVenta(int idVenta);
    int getIdProductoTalla();
    void setIdProductoTalla(int idProductoTalla);
    String getNombreProducto();
    void setNombreProducto(String nombreProducto);
    String getTalla();
    void setTalla(String talla);
    int getCantidad();
    void setCantidad(int cantidad);
    float getPrecioUnitario();
    void setPrecioUnitario(float precioUnitario);
    float getSubtotal();
    void setSubtotal(float subtotal);
}
