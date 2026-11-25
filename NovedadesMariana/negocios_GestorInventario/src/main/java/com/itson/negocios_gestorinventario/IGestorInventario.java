package com.itson.negocios_gestorinventario;

import dtos.CategoriaDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import interfaces.IProductoDTO;
import interfaces.IProductoTallaDTO;
import java.util.List;

/**
 * Contrato de servicios de negocio para la gestión de inventario y catálogo.
 */
public interface IGestorInventario {

    IProductoDTO buscarProductoPorCodigo(String codigo);

    ProductoDTO obtenerProductoPorId(int id);

    List<ProductoDTO> listarProductosPorCategoria(int idCategoria);

    List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto);

    boolean validarStockDisponible(int idProductoTalla, int cantidad);

    boolean descontarStock(int idProductoTalla, int cantidad);

    boolean reabastecerStock(int idProductoTalla, int cantidad);

    boolean ajustarStock(int idProductoTalla, int nuevoStock);

    List<? extends IProductoDTO> buscarProductos(String criterio);

    List<? extends IProductoDTO> buscarProductosPorCategoria(String nombre);

    List<ProductoDTO> obtenerTodosLosProductos();

    void actualizarProducto(ProductoDTO producto) throws Exception;

    void eliminarProducto(Long id) throws Exception;

    void agregarTalla(ProductoTallaDTO talla) throws Exception;

    void eliminarTalla(int idProductoTalla) throws Exception;
    
    void guardarProducto(ProductoDTO producto) throws Exception;

    List<CategoriaDTO> obtenerCategorias();
}
