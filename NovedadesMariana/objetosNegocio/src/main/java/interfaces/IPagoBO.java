/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import dtos.PagoDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IPagoBO {
    /** Valida y normaliza el DTO que se guardará junto con su venta. */
    PagoDTO prepararPagoParaVenta(PagoDTO pago);

    /** Recupera y confirma el pago guardado por cascada junto con su venta. */
    PagoDTO registrarPago(PagoDTO pago);

    float calcularCambio(float totalVenta, float montoRecibido);

    List<PagoDTO> obtenerPagosPorVenta(int idVenta);
}
