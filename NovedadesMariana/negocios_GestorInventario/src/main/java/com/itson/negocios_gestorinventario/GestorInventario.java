/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_gestorinventario;

import bos.ProductoBO;
import bos.ProductoTallaBO;
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
        return this.productoBO.obtenerPorSku(codigo);
    }

    @Override
    public ProductoDTO obtenerProductoPorId(int id) {
        return this.productoBO.obtenerPorId(id);
    }

    @Override
    public List<ProductoDTO> listarProductosPorCategoria(int idCategoria) {
        return this.productoBO.listarProductosPorCategoria(idCategoria);
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
}