/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_realizarventa;

import com.itson.negocios_gestorinventario.GestorInventario;
import com.itson.negocios_gestorinventario.IGestorInventario;
import com.itson.negocios_gestorpagos.GestorPagos;
import com.itson.negocios_gestorpagos.IGestorPagos;
import com.itson.negocios_gestorventas.GestorVentas;
import com.itson.negocios_gestorventas.IGestorVentas;
import dtos.DatosFiscalesDTO;
import dtos.DetalleVentaDTO;
import dtos.PagoDTO;
import dtos.VentaDTO;

/**
 *
 * @author saula
 */
public class RealizarVenta implements IRealizarVenta {

    private final IGestorVentas gestorVentas;
    private final IGestorInventario gestorInventario;
    private final IGestorPagos gestorPagos;
    //private final IGeneradorTicket generadorTicket;
    //private final IServicioFacturacion servicioFacturacion;

    public RealizarVenta() {
        this.gestorVentas = new GestorVentas();
        this.gestorInventario = new GestorInventario();
        this.gestorPagos = new GestorPagos();
        //this.generadorTicket = new GeneradorTicket();
        //this.servicioFacturacion = new ServicioFacturacion();
    }

    @Override
    public VentaDTO registrarVenta(VentaDTO venta) {
        // 1. Validar y descontar stock por cada prenda/talla
        for (DetalleVentaDTO detalle : venta.getDetalles()) {
            boolean stockOk = gestorInventario.validarStockDisponible(detalle.getIdProductoTalla(), detalle.getCantidad());
            if (!stockOk) {
                throw new IllegalStateException("Stock insuficiente para: " + detalle.getNombreProducto());
            }
        }
        for (DetalleVentaDTO detalle : venta.getDetalles()) {
            gestorInventario.descontarStock(detalle.getIdProductoTalla(), detalle.getCantidad());
        }

        // 2. Persistir venta consolidada
        VentaDTO ventaRegistrada = gestorVentas.registrarVenta(venta);

        // 3. Registrar los pagos asociados a la venta
        if (venta.getPagos() != null) {
            for (PagoDTO pago : venta.getPagos()) {
                pago.setIdVenta(ventaRegistrada.getId());
                gestorPagos.registrarPago(pago);
            }
        }

        return ventaRegistrada;
    }

    @Override
    public boolean imprimirTicket(int idVenta) {
        VentaDTO venta = gestorVentas.obtenerVentaPorId(idVenta);
        if (venta == null) {
            return false;
        }
        //return generadorTicket.generarTicketVenta(venta);
        return true;
    }

    @Override
    public boolean solicitarFactura(int idVenta, DatosFiscalesDTO datosFiscales) {
        VentaDTO venta = gestorVentas.obtenerVentaPorId(idVenta);
        if (venta == null) {
            return false;
        }
        //return servicioFacturacion.timbrarFactura(venta, datosFiscales);
        return true;
    }
}
