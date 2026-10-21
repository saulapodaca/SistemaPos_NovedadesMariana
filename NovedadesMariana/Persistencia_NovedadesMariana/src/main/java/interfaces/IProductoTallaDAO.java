/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import dominio.ProductoTalla;
import java.util.List;

/**
 *
 * @author rauln
 */
public interface IProductoTallaDAO {
    void guardar(ProductoTalla productoTalla) throws Exception;
    void actualizar(ProductoTalla productoTalla) throws Exception;
    void eliminar(Long id) throws Exception;
    ProductoTalla buscarPorId(Long id);
    List<ProductoTalla> obtenerTodos();
}
