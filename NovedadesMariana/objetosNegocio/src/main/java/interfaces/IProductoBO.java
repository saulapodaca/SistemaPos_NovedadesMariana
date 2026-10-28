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
    ProductoDTO obtenerPorId(int id);

    ProductoDTO obtenerPorSku(String sku);

    List<ProductoDTO> listarProductosPorCategoria(int idCategoria);

    List<ProductoDTO> buscarProductos(String criterio);
}
