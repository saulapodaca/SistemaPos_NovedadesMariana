/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import dominio.Pago;
import java.util.List;

/**
 *
 * @author rauln
 */
public interface IPagoDAO {
    void guardar(Pago pago) throws Exception;
    void actualizar(Pago pago) throws Exception;
    void eliminar(Long id) throws Exception;
    Pago buscarPorId(Long id);
    List<Pago> obtenerTodos();
}
