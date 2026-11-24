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
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

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
        if (venta == null) {
            return this.ventaBO.registrarVenta(null);
        }
        VentaDTO ventaAdaptada = new VentaDTO(
                venta.getId(), venta.getFolio(), venta.getFechaHora(),
                venta.getSubtotal(), venta.getIva(), venta.getTotal(),
                venta.getIdUsuario(), venta.getDetalles(), venta.getPagos());
        return this.ventaBO.registrarVenta(ventaAdaptada);
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
    public List<VentaDTO> obtenerVentasEntre(LocalDateTime inicio, LocalDateTime fin) {
        return new ArrayList<>(this.ventaBO.obtenerVentasEntre(inicio, fin));
    }

    @Override
    public ResumenVentaDTO calcularTotales(
            List<DetalleVentaDTO> detalles, float descuento) {
        if (detalles == null) {
            return this.ventaBO.calcularTotales(null, descuento);
        }
        List<DetalleVentaDTO> detallesAdaptados = new ArrayList<>();
        for (DetalleVentaDTO detalle : detalles) {
            detallesAdaptados.add(detalle == null ? null : new DetalleVentaDTO(
                    detalle.getId(), detalle.getIdVenta(), detalle.getIdProductoTalla(),
                    detalle.getNombreProducto(), detalle.getTalla(), detalle.getCantidad(),
                    detalle.getPrecioUnitario(), detalle.getSubtotal()));
        }
        return this.ventaBO.calcularTotales(detallesAdaptados, descuento);
    }

    @Override
    public boolean cancelarVenta(int idVenta) {
        return this.ventaBO.cancelarVenta(idVenta);
    }
}
