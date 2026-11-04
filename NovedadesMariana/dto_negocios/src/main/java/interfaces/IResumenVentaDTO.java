package interfaces;

public interface IResumenVentaDTO {
    float getSubtotal();
    void setSubtotal(float subtotal);
    float getIva();
    void setIva(float iva);
    float getDescuento();
    void setDescuento(float descuento);
    float getTotal();
    void setTotal(float total);
}
