/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_gestorpagos;

import bos.PagoBO;
import dtos.PagoDTO;
import interfaces.IPagoDTO;
import interfaces.IPagoBO;
import java.util.List;

/**
 *
 * @author saula
 */
public class GestorPagos implements IGestorPagos {

    private final IPagoBO pagoBO = PagoBO.getInstance();

    public GestorPagos() {
    }

    @Override
    public IPagoDTO registrarPago(IPagoDTO pago) {
        if (pago == null) {
            return this.pagoBO.registrarPago(null);
        }
        PagoDTO pagoAdaptado = new PagoDTO(
                pago.getId(), pago.getIdVenta(), pago.getMetodoPago(), pago.getMonto(),
                pago.getMontoRecibido(), pago.getCambio(), pago.getFechaHora());
        return this.pagoBO.registrarPago(pagoAdaptado);
    }

    @Override
    public float calcularCambio(float totalVenta, float montoRecibido) {
        return this.pagoBO.calcularCambio(totalVenta, montoRecibido);
    }

    @Override
    public List<? extends IPagoDTO> obtenerPagosPorVenta(int idVenta) {
        return this.pagoBO.obtenerPagosPorVenta(idVenta);
    }
}
