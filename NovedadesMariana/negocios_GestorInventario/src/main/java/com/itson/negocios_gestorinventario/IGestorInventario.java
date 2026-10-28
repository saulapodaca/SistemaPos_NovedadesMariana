/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_gestorinventario;

import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IGestorInventario {
    ProductoDTO buscarProductoPorCodigo(String codigo);

    ProductoDTO obtenerProductoPorId(int id);

    List<ProductoDTO> listarProductosPorCategoria(int idCategoria);

    List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto);

    boolean validarStockDisponible(int idProductoTalla, int cantidad);

    boolean descontarStock(int idProductoTalla, int cantidad);

    boolean reabastecerStock(int idProductoTalla, int cantidad);
}
