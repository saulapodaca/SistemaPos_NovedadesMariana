/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_gestorpagos;

import interfaces.IPagoDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IGestorPagos {
    IPagoDTO registrarPago(IPagoDTO pago);

    float calcularCambio(float totalVenta, float montoRecibido);

    List<? extends IPagoDTO> obtenerPagosPorVenta(int idVenta);
}
