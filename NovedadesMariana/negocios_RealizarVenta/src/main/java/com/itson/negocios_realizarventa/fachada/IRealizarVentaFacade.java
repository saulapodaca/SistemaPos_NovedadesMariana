/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.itson.negocios_realizarventa.fachada;

import dtos.DatosFiscalesDTO;
import dtos.DetalleVentaDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import dtos.ResumenVentaDTO;
import dtos.UsuarioDTO;
import dtos.VentaDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IRealizarVentaFacade {
    // Transacción y cierre
    VentaDTO registrarVenta(VentaDTO venta);
    boolean imprimirTicket(int idVenta);
    boolean solicitarFactura(int idVenta, DatosFiscalesDTO datosFiscales);

    // Consultas y validaciones para la interfaz (PanelVenta / PanelBuscador)
    ProductoDTO buscarProductoPorCodigo(String codigo);
    ProductoDTO obtenerProductoPorId(int id);
    List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto);
    boolean validarStockDisponible(int idProductoTalla, int cantidad);
    ResumenVentaDTO calcularTotales(List<DetalleVentaDTO> detalles, float descuento);

    // Consultas de sesión y ventas
    UsuarioDTO autenticarUsuario(String usuario, String contrasenia);
    VentaDTO obtenerVentaPorId(int id);
    VentaDTO obtenerVentaPorFolio(String folio);
}
