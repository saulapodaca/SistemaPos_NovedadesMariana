/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import dominio.DetalleVenta;
import java.util.List;

/**
 *
 * @author rauln
 */
public interface IDetalleVentaDAO {
    void guardar(DetalleVenta detalleVenta) throws Exception;
    void actualizar(DetalleVenta detalleVenta) throws Exception;
    void eliminar(Long id) throws Exception;
    DetalleVenta buscarPorId(Long id);
    List<DetalleVenta> obtenerTodos();
}
