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
    public void guardar(Producto producto) throws Exception;
    public void actualizar(Producto producto) throws Exception;
    public void eliminar(Long id) throws Exception;
    public Producto buscarPorId(Long id);
    public List<Producto> obtenerTodos();
    public Producto obtenerPorSku(String sku);
    public List<Producto> listarProductosPorCategoria(Long idCategoria);
    public List<Producto> buscarProductos(String criterio);
}