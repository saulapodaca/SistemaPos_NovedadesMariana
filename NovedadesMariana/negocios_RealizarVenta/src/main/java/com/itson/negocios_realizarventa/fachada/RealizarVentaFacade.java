/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.negocios_realizarventa.fachada;

import com.itson.negocios_gestorempleados.GestorEmpleados;
import com.itson.negocios_gestorempleados.IGestorEmpleados;
import com.itson.negocios_gestorinventario.GestorInventario;
import com.itson.negocios_gestorinventario.IGestorInventario;
import com.itson.negocios_gestorpagos.GestorPagos;
import com.itson.negocios_gestorpagos.IGestorPagos;
import com.itson.negocios_gestorventas.GestorVentas;
import com.itson.negocios_gestorventas.IGestorVentas;
import com.itson.negocios_realizarventa.IRealizarVenta;
import com.itson.negocios_realizarventa.RealizarVenta;
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
public class RealizarVentaFacade implements IRealizarVentaFacade {

    private final IRealizarVenta realizarVenta;
    private final IGestorInventario gestorInventario;
    private final IGestorVentas gestorVentas;
    private final IGestorEmpleados gestorEmpleados;
    private final IGestorPagos gestorPagos;

    public RealizarVentaFacade() {
        this.realizarVenta = new RealizarVenta();
        this.gestorInventario = new GestorInventario();
        this.gestorVentas = new GestorVentas();
        this.gestorEmpleados = new GestorEmpleados();
        this.gestorPagos = new GestorPagos();
    }

    // --- Métodos delegados a RealizarVenta (Transacción) ---
    @Override
    public VentaDTO registrarVenta(VentaDTO venta) {
        return this.realizarVenta.registrarVenta(venta);
    }

    @Override
    public boolean imprimirTicket(int idVenta) {
        return this.realizarVenta.imprimirTicket(idVenta);
    }

    @Override
    public boolean solicitarFactura(int idVenta, DatosFiscalesDTO datosFiscales) {
        return this.realizarVenta.solicitarFactura(idVenta, datosFiscales);
    }

    // --- Métodos delegados a GestorInventario ---
    @Override
    public ProductoDTO buscarProductoPorCodigo(String codigo) {
        return this.gestorInventario.buscarProductoPorCodigo(codigo);
    }

    @Override
    public ProductoDTO obtenerProductoPorId(int id) {
        return this.gestorInventario.obtenerProductoPorId(id);
    }

    @Override
    public List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto) {
        return this.gestorInventario.obtenerTallasPorProducto(idProducto);
    }

    @Override
    public boolean validarStockDisponible(int idProductoTalla, int cantidad) {
        return this.gestorInventario.validarStockDisponible(idProductoTalla, cantidad);
    }

    // --- Métodos delegados a GestorVentas ---
    @Override
    public ResumenVentaDTO calcularTotales(List<DetalleVentaDTO> detalles, float descuento) {
        return this.gestorVentas.calcularTotales(detalles, descuento);
    }

    @Override
    public VentaDTO obtenerVentaPorId(int id) {
        return this.gestorVentas.obtenerVentaPorId(id);
    }

    @Override
    public VentaDTO obtenerVentaPorFolio(String folio) {
        return this.gestorVentas.obtenerVentaPorFolio(folio);
    }

    // --- Métodos delegados a GestorEmpleado ---
    @Override
    public UsuarioDTO autenticarUsuario(String usuario, String contrasenia) {
        return this.gestorEmpleados.autenticar(usuario, contrasenia);
    }
}