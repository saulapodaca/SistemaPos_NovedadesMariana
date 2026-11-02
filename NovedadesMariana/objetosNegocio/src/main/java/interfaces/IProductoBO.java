/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import dtos.ProductoDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IProductoBO {
    public void guardar(ProductoDTO producto) throws Exception;
    public void actualizar(ProductoDTO producto) throws Exception;
    public void eliminar(Long id) throws Exception;
    public List<ProductoDTO> obtenerTodos();
    ProductoDTO obtenerPorId(Long id);
    ProductoDTO obtenerPorSku(String sku);
    List<ProductoDTO> listarProductosPorCategoria(Long idCategoria);
    List<ProductoDTO> buscarProductos(String criterio);
}
