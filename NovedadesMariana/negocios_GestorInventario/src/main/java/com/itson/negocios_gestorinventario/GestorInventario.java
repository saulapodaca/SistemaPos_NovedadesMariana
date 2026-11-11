/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_gestorinventario;

import bos.ProductoBO;
import bos.ProductoTallaBO;
import interfaces.IProductoDTO;
import interfaces.IProductoBO;
import interfaces.IProductoTallaDTO;
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
    public IProductoDTO buscarProductoPorCodigo(String codigo) {
        return this.productoBO.obtenerPorCodigoBarras(codigo);
    }

    @Override
    public IProductoDTO obtenerProductoPorId(int id) {
        return this.productoBO.obtenerPorId((long) id);
    }

    @Override
    public List<? extends IProductoDTO> listarProductosPorCategoria(int idCategoria) {
        return this.productoBO.listarProductosPorCategoria((long) idCategoria);
    }

    @Override
    public List<? extends IProductoTallaDTO> obtenerTallasPorProducto(int idProducto) {
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
    public List<? extends IProductoDTO> buscarProductos(String criterio) {
        return productoBO.buscarProductos(criterio);
    }
    @Override
    public List<? extends IProductoDTO> buscarProductosPorCategoria(String nombre) {
        if (nombre == null || nombre.isBlank()) return List.of();
        for (dtos.CategoriaDTO categoria : bos.CategoriaBO.getInstance().obtenerTodas()) {
            if (nombre.trim().equalsIgnoreCase(categoria.getNombre())) {
                return productoBO.listarProductosPorCategoria(categoria.getId());
            }
        }
        return List.of();
    }
}
