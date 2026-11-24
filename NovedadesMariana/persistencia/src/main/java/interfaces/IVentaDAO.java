/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import dominio.ProductoTalla;
import dominio.Venta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author rauln
 */
public interface IVentaDAO {
    void guardar(Venta venta) throws Exception;
    void actualizar(Venta venta) throws Exception;
    void eliminar(Long id) throws Exception;
    Venta buscarVentaPorId(Long id);
    Venta buscarPorFolio(String folio);
    List<Venta> obtenerTodas();
    List<Venta> obtenerEntre(LocalDateTime inicio, LocalDateTime fin);
    boolean cancelar(Long id) throws Exception;
    /** Ejecuta la actualización sobre las tallas bloqueadas dentro de la transacción de venta. */
    void guardarConTallas(Venta venta, Set<Long> idsTalla,
            Consumer<Map<Long, ProductoTalla>> actualizarTallas) throws Exception;
}
