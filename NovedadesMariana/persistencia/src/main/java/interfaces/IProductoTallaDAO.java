/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import dominio.ProductoTalla;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author rauln
 */
public interface IProductoTallaDAO {
    void guardar(ProductoTalla productoTalla) throws Exception;
    void actualizar(ProductoTalla productoTalla) throws Exception;
    void eliminar(Long id) throws Exception;
    void modificar(Long id, Consumer<ProductoTalla> cambios) throws Exception;
    void eliminar(Long id, Consumer<ProductoTalla> antesDeEliminar) throws Exception;
    ProductoTalla buscarProductoTallaPorId(Long id);
    List<ProductoTalla> obtenerTodos();
    List<ProductoTalla> obtenerPorProducto(Long idProducto);
}
