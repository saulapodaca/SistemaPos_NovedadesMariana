/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_gestorventas;

import interfaces.IDetalleVentaDTO;
import interfaces.IResumenVentaDTO;
import interfaces.IVentaDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IGestorVentas {
    IVentaDTO registrarVenta(IVentaDTO venta);

    IVentaDTO obtenerVentaPorId(int id);

    IVentaDTO obtenerVentaPorFolio(String folio);

    IResumenVentaDTO calcularTotales(
            List<? extends IDetalleVentaDTO> detalles, float descuento);

    boolean cancelarVenta(int idVenta);
}
