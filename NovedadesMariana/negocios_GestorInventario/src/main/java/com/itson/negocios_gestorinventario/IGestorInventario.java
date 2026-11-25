package com.itson.negocios_gestorinventario;

import dtos.CategoriaDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import java.util.List;

/**
 * Contrato de servicios de negocio para la gestión de inventario y catálogo.
 */
public interface IGestorInventario {
    ProductoDTO buscarProductoPorCodigo(String codigo);

    ProductoDTO obtenerProductoPorId(int id);

    List<ProductoDTO> listarProductosPorCategoria(int idCategoria);

    List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto);

    boolean validarStockDisponible(int idProductoTalla, int cantidad);

    boolean descontarStock(int idProductoTalla, int cantidad);

    boolean reabastecerStock(int idProductoTalla, int cantidad);
    List<ProductoDTO> buscarProductos(String criterio);
    List<ProductoDTO> buscarProductosPorCategoria(String nombre);

    boolean ajustarStock(int idProductoTalla, int nuevoStock);

    List<ProductoDTO> obtenerTodosLosProductos();

    void actualizarProducto(ProductoDTO producto) throws Exception;

    void eliminarProducto(Long id) throws Exception;

    void agregarTalla(ProductoTallaDTO talla) throws Exception;

    void eliminarTalla(int idProductoTalla) throws Exception;
    
    void guardarProducto(ProductoDTO producto) throws Exception;

    List<CategoriaDTO> obtenerCategorias();
}
