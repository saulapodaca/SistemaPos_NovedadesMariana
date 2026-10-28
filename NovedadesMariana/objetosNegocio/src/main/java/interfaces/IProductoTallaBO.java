/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import dtos.ProductoTallaDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IProductoTallaBO {
    boolean validarStockDisponible(int idProductoTalla, int cantidadRequerida);

    boolean descontarStock(int idProductoTalla, int cantidad);

    boolean reabastecerStock(int idProductoTalla, int cantidad);

    List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto);
}
