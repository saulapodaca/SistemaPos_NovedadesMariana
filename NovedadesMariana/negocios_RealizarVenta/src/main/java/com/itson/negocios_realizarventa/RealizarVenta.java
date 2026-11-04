/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_realizarventa;

import com.itson.negocios_gestorinventario.GestorInventario;
import com.itson.negocios_gestorinventario.IGestorInventario;
import com.itson.negocios_gestorventas.GestorVentas;
import com.itson.negocios_gestorventas.IGestorVentas;
import dtos.DatosFiscalesDTO;
import dtos.DetalleVentaDTO;
import dtos.VentaDTO;
import interfaces.IVentaDTO;

/**
 *
 * @author saula
 */
public class RealizarVenta implements IRealizarVenta {

    private final IGestorVentas gestorVentas;
    private final IGestorInventario gestorInventario;
    //private final IGeneradorTicket generadorTicket;
    //private final IServicioFacturacion servicioFacturacion;

    public RealizarVenta() {
        this.gestorVentas = new GestorVentas();
        this.gestorInventario = new GestorInventario();
        //this.generadorTicket = new GeneradorTicket();
        //this.servicioFacturacion = new ServicioFacturacion();
    }

    @Override
    public VentaDTO registrarVenta(VentaDTO venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula.");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un detalle.");
        }
        // 1. Validar existencias. El descuento definitivo se hace dentro
        //    de la transacción que registra la venta.
        for (DetalleVentaDTO detalle : venta.getDetalles()) {
            if (detalle == null) {
                throw new IllegalArgumentException("La venta contiene un detalle nulo.");
            }
            boolean stockOk = gestorInventario.validarStockDisponible(detalle.getIdProductoTalla(), detalle.getCantidad());
            if (!stockOk) {
                throw new IllegalStateException("Stock insuficiente para: " + detalle.getNombreProducto());
            }
        }
        // 2. Persistir la venta y descontar inventario de forma atómica.
        VentaDTO ventaRegistrada = adaptarVenta(gestorVentas.registrarVenta(venta));

        // El pago ya fue validado por PagoBO y persistido por cascada con la venta.
        return ventaRegistrada;
    }

    @Override
    public boolean imprimirTicket(int idVenta) {
        VentaDTO venta = adaptarVenta(gestorVentas.obtenerVentaPorId(idVenta));
        if (venta == null) {
            return false;
        }
        // No hay una implementación de impresión conectada todavía.
        return false;
    }

    @Override
    public boolean solicitarFactura(int idVenta, DatosFiscalesDTO datosFiscales) {
        VentaDTO venta = adaptarVenta(gestorVentas.obtenerVentaPorId(idVenta));
        if (venta == null) {
            return false;
        }
        // No hay una implementación de facturación conectada todavía.
        return false;
    }

    private VentaDTO adaptarVenta(IVentaDTO venta) {
        if (venta == null) {
            return null;
        }
        return new VentaDTO(venta.getId(), venta.getFolio(), venta.getFechaHora(),
                venta.getSubtotal(), venta.getIva(), venta.getTotal(),
                venta.getIdUsuario(), venta.getDetalles(), venta.getPagos());
    }
}
