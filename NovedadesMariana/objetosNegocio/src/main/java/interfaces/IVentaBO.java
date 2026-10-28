/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import dtos.DetalleVentaDTO;
import dtos.ResumenVentaDTO;
import dtos.VentaDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IVentaBO {
    VentaDTO registrarVenta(VentaDTO venta);

    VentaDTO obtenerPorId(int id);

    VentaDTO obtenerPorFolio(String folio);

    ResumenVentaDTO calcularTotales(List<DetalleVentaDTO> detalles, float descuento);

    boolean cancelarVenta(int idVenta);
}
