package com.itson.negocios_gestorinventario;

import bos.CategoriaBO;
import bos.ProductoBO;
import bos.ProductoTallaBO;
import dtos.CategoriaDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import interfaces.ICategoriaBO;
import interfaces.IProductoBO;
import interfaces.IProductoTallaBO;
import java.util.List;

/**
 * Implementación de la gestión de inventario delegando a los BOs de negocio.
 */
public class GestorInventario implements IGestorInventario {

    private final IProductoBO productoBO = ProductoBO.getInstance();
    private final IProductoTallaBO productoTallaBO = ProductoTallaBO.getInstance();
    private final ICategoriaBO categoriaBO = CategoriaBO.getInstance();

    public GestorInventario() {
    }
     
    @Override
    public void guardarProducto(ProductoDTO producto) throws Exception {
        productoBO.guardar(producto);
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
    public boolean ajustarStock(int idProductoTalla, int nuevoStock) {
        return this.productoTallaBO.ajustarStock(idProductoTalla, nuevoStock);
    }

    @Override
    public List<ProductoDTO> buscarProductos(String criterio) {
        return productoBO.buscarProductos(criterio);
    }

    @Override
    public List<ProductoDTO> buscarProductosPorCategoria(String nombre) {
        if (nombre == null || nombre.isBlank()) return List.of();
        for (CategoriaDTO categoria : categoriaBO.obtenerTodas()) {
            if (nombre.trim().equalsIgnoreCase(categoria.getNombre())) {
                return productoBO.listarProductosPorCategoria(categoria.getId());
            }
        }
        return List.of();
    }

    @Override
    public List<ProductoDTO> obtenerTodosLosProductos() {
        return productoBO.obtenerTodos();
    }

    @Override
    public void actualizarProducto(ProductoDTO producto) throws Exception {
        productoBO.actualizar(producto);
    }

    @Override
    public void eliminarProducto(Long id) throws Exception {
        productoBO.eliminar(id);
    }

    @Override
    public void agregarTalla(ProductoTallaDTO talla) throws Exception {
        productoTallaBO.guardarTalla(talla);
    }

    @Override
    public void eliminarTalla(int idProductoTalla) throws Exception {
        productoTallaBO.eliminarTalla(idProductoTalla);
    }

    @Override
    public List<CategoriaDTO> obtenerCategorias() {
        return categoriaBO.obtenerTodas();
    }
}
