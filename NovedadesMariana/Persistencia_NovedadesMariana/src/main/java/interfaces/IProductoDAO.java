/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import dominio.Producto;
import java.util.List;

/**
 *
 * @author rauln
 */
public interface IProductoDAO {
    void guardar(Producto producto) throws Exception;
    void actualizar(Producto producto) throws Exception;
    void eliminar(Long id) throws Exception;
    Producto buscarPorId(Long id);
    List<Producto> obtenerTodos();
}