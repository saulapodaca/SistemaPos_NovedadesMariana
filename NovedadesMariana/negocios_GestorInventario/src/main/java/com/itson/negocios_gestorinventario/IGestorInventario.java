/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_gestorinventario;

import interfaces.IProductoDTO;
import interfaces.IProductoTallaDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IGestorInventario {
    IProductoDTO buscarProductoPorCodigo(String codigo);

    IProductoDTO obtenerProductoPorId(int id);

    List<? extends IProductoDTO> listarProductosPorCategoria(int idCategoria);

    List<? extends IProductoTallaDTO> obtenerTallasPorProducto(int idProducto);

    boolean validarStockDisponible(int idProductoTalla, int cantidad);

    boolean descontarStock(int idProductoTalla, int cantidad);

    boolean reabastecerStock(int idProductoTalla, int cantidad);
}
