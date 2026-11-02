/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author saula
 */
public class ProductoDTO {
    private Long id;
    private String codigoBarras;
    private String sku;
    private String nombre;
    private String descripcion;
    private float precio;
    private TipoPrendaDTO tipoPrenda;
    private Set<Long> categoriasIds = new HashSet<>();

    public ProductoDTO() {
    }

    public ProductoDTO(Long id, String codigoBarras, String sku, String nombre, String descripcion, float precio, TipoPrendaDTO tipoPrenda, Set<Long> categoriasIds) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipoPrenda = tipoPrenda;
        this.categoriasIds = categoriasIds;
    }

    public ProductoDTO(String codigoBarras, String sku, String nombre, String descripcion, float precio, TipoPrendaDTO tipoPrenda, Set<Long> categoriasIds) {
        this.codigoBarras = codigoBarras;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipoPrenda = tipoPrenda;
        this.categoriasIds = categoriasIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
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

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public TipoPrendaDTO getTipoPrenda() {
        return tipoPrenda;
    }

    public void setTipoPrenda(TipoPrendaDTO tipoPrenda) {
        this.tipoPrenda = tipoPrenda;
    }

    public Set<Long> getCategoriasIds() {
        return categoriasIds;
    }

    public void setCategoriasIds(Set<Long> categoriasIds) {
        this.categoriasIds = categoriasIds;
    }
}