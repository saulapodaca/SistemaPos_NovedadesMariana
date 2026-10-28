/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author saula
 */
public class ProductoTallaDTO {
    private int id;
    private int idProducto;
    private String talla;
    private int stockActual;

    public ProductoTallaDTO(int id, int idProducto, String talla, int stockActual) {
        this.id = id;
        this.idProducto = idProducto;
        this.talla = talla;
        this.stockActual = stockActual;
    }

    public ProductoTallaDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }
    
}
