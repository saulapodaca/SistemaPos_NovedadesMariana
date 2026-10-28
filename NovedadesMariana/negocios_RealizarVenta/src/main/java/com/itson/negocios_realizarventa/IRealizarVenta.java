/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_realizarventa;

import dtos.DatosFiscalesDTO;
import dtos.VentaDTO;

/**
 *
 * @author saula
 */
public interface IRealizarVenta {
    VentaDTO registrarVenta(VentaDTO venta);

    boolean imprimirTicket(int idVenta);

    boolean solicitarFactura(int idVenta, DatosFiscalesDTO datosFiscales);
}
