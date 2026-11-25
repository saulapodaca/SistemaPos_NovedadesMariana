/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_gestorinventario;

import bos.ProductoBO;
import bos.ProductoTallaBO;
import dtos.CategoriaDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import interfaces.IProductoBO;
import interfaces.IProductoTallaBO;
import java.util.List;

/**
 *
 * @author saula
 */
public class GestorInventario implements IGestorInventario {

    private final IProductoBO productoBO = ProductoBO.getInstance();
    private final IProductoTallaBO productoTallaBO = ProductoTallaBO.getInstance();

    public GestorInventario() {
    }

    @Override
    public ProductoDTO buscarProductoPorCodigo(String codigo) {
        return this.productoBO.obtenerPorCodigoBarras(codigo);
    }

    @Override
    public ProductoDTO obtenerProductoPorId(int id) {
        return this.productoBO.obtenerPorId((long) id);
    }

    @Override
    public List<ProductoDTO> listarProductosPorCategoria(int idCategoria) {
        return this.productoBO.listarProductosPorCategoria((long) idCategoria);
    }

    @Override
    public List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto) {
        return this.productoTallaBO.obtenerTallasPorProducto(idProducto);
    }

    @Override
    public boolean validarStockDisponible(int idProductoTalla, int cantidad) {
        return this.productoTallaBO.validarStockDisponible(idProductoTalla, cantidad);
    }

    @Override
    public boolean descontarStock(int idProductoTalla, int cantidad) {
        return this.productoTallaBO.descontarStock(idProductoTalla, cantidad);
    }

    @Override
    public boolean reabastecerStock(int idProductoTalla, int cantidad) {
        return this.productoTallaBO.reabastecerStock(idProductoTalla, cantidad);
    }

    @Override
    public List<ProductoDTO> buscarProductos(String criterio) {
        return productoBO.buscarProductos(criterio);
    }

    @Override
    public List<ProductoDTO> buscarProductosPorCategoria(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return List.of();
        }
        for (dtos.CategoriaDTO categoria : bos.CategoriaBO.getInstance().obtenerTodas()) {
            if (nombre.trim().equalsIgnoreCase(categoria.getNombre())) {
                return productoBO.listarProductosPorCategoria(categoria.getId());
            }
        }
        return List.of();
    }

    @Override
    public ProductoDTO agregarProducto(ProductoDTO producto) {
        try {
            return this.productoBO.guardar(producto);
        } catch (Exception ex) {
            System.getLogger(GestorInventario.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return null;
    }

    @Override
    public ProductoDTO buscarProductoPorSKU(String sku) {
        return this.productoBO.obtenerPorSku(sku);
    }

    @Override
    public ProductoDTO registrarProducto(ProductoDTO normalizado) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<CategoriaDTO> obtenerCategorias() {
        return productoBO.obtenerCategoriaDTOs();
    }

    @Override
    public boolean existeCodigoBarras(String trim) {
        return productoBO.obtenerPorCodigoBarras(trim) != null;
    }

    @Override
    public boolean existeSku(String toUpperCase) {
        return productoBO.obtenerPorSku(toUpperCase) != null;
    }
}
