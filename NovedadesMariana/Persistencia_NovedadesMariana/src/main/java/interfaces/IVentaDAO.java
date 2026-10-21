/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import dominio.DetalleVenta;
import dominio.Venta;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rauln
 */
public interface IVentaDAO {
    void guardar(Venta venta) throws Exception;
    void actualizar(Venta venta) throws Exception;
    void eliminar(Long id) throws Exception;
    Venta buscarPorId(Long id);
    List<Venta> obtenerTodas();
    void guardarConTallas(
    Venta venta, Map<DetalleVenta, Long> tallasPorDetalle) throws Exception;
}