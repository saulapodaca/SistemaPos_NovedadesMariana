/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_gestorventas;

import dtos.DetalleVentaDTO;
import dtos.ResumenVentaDTO;
import dtos.VentaDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IGestorVentas {
    VentaDTO registrarVenta(VentaDTO venta);

    VentaDTO obtenerVentaPorId(int id);

    VentaDTO obtenerVentaPorFolio(String folio);

    ResumenVentaDTO calcularTotales(List<DetalleVentaDTO> detalles, float descuento);

    boolean cancelarVenta(int idVenta);
}
