/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author saula
 */
public class DetalleVentaDTO {
    private int id;
    private int idVenta;
    private int idProductoTalla;
    private String nombreProducto;
    private String talla;
    private int cantidad;
    private float precioUnitario;
    private float subtotal;

    public DetalleVentaDTO() {
    }

    public DetalleVentaDTO(int id, int idVenta, int idProductoTalla, String nombreProducto, String talla, int cantidad, float precioUnitario, float subtotal) {
        this.id = id;
        this.idVenta = idVenta;
        this.idProductoTalla = idProductoTalla;
        this.nombreProducto = nombreProducto;
        this.talla = talla;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProductoTalla() {
        return idProductoTalla;
    }

    public void setIdProductoTalla(int idProductoTalla) {
        this.idProductoTalla = idProductoTalla;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
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
    
}
