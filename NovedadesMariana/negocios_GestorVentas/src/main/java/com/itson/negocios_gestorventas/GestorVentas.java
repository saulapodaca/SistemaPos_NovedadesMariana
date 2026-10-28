/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_gestorventas;

import bos.VentaBO;
import dtos.DetalleVentaDTO;
import dtos.ResumenVentaDTO;
import dtos.VentaDTO;
import interfaces.IVentaBO;
import java.util.List;

/**
 *
 * @author saula
 */
public class GestorVentas implements IGestorVentas {

    private final IVentaBO ventaBO = VentaBO.getInstance();

    public GestorVentas() {
    }

    @Override
    public VentaDTO registrarVenta(VentaDTO venta) {
        return this.ventaBO.registrarVenta(venta);
    }

    @Override
    public VentaDTO obtenerVentaPorId(int id) {
        return this.ventaBO.obtenerPorId(id);
    }

    @Override
    public VentaDTO obtenerVentaPorFolio(String folio) {
        return this.ventaBO.obtenerPorFolio(folio);
    }

    @Override
    public ResumenVentaDTO calcularTotales(List<DetalleVentaDTO> detalles, float descuento) {
        return this.ventaBO.calcularTotales(detalles, descuento);
    }

    @Override
    public boolean cancelarVenta(int idVenta) {
        return this.ventaBO.cancelarVenta(idVenta);
    }
}
