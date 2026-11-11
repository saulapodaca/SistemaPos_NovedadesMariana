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
import interfaces.IProductoDTO;
import interfaces.IProductoTallaDTO;
import interfaces.IResumenVentaDTO;
import interfaces.IUsuarioDTO;
import interfaces.IVentaDTO;
import java.util.ArrayList;
import java.util.HashSet;
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
        return adaptarProducto(this.gestorInventario.buscarProductoPorCodigo(codigo));
    }

    @Override
    public ProductoDTO obtenerProductoPorId(int id) {
        return adaptarProducto(this.gestorInventario.obtenerProductoPorId(id));
    }

    @Override
    public List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto) {
        List<? extends IProductoTallaDTO> tallas = this.gestorInventario.obtenerTallasPorProducto(idProducto);
        if (tallas == null) {
            return null;
        }
        List<ProductoTallaDTO> resultado = new ArrayList<>();
        for (IProductoTallaDTO talla : tallas) {
            resultado.add(talla == null ? null : new ProductoTallaDTO(
                    talla.getId(), talla.getIdProducto(), talla.getTalla(), talla.getStockActual()));
        }
        return resultado;
    }

    @Override
    public boolean validarStockDisponible(int idProductoTalla, int cantidad) {
        return this.gestorInventario.validarStockDisponible(idProductoTalla, cantidad);
    }

    // --- Métodos delegados a GestorVentas ---
    @Override
    public ResumenVentaDTO calcularTotales(List<DetalleVentaDTO> detalles, float descuento) {
        IResumenVentaDTO resumen = this.gestorVentas.calcularTotales(detalles, descuento);
        return resumen == null ? null : new ResumenVentaDTO(
                resumen.getSubtotal(), resumen.getIva(), resumen.getDescuento(), resumen.getTotal());
    }

    @Override
    public float calcularCambio(float totalVenta, float montoRecibido) {
        return this.gestorPagos.calcularCambio(totalVenta, montoRecibido);
    }

    @Override
    public VentaDTO obtenerVentaPorId(int id) {
        return adaptarVenta(this.gestorVentas.obtenerVentaPorId(id));
    }

    @Override
    public VentaDTO obtenerVentaPorFolio(String folio) {
        return adaptarVenta(this.gestorVentas.obtenerVentaPorFolio(folio));
    }

    // --- Métodos delegados a GestorEmpleado ---
    @Override
    public UsuarioDTO autenticarUsuario(String usuario, String contrasenia) {
        return adaptarUsuario(this.gestorEmpleados.autenticar(usuario, contrasenia));
    }

    private ProductoDTO adaptarProducto(IProductoDTO producto) {
        if (producto == null) {
            return null;
        }
        return new ProductoDTO(producto.getId(), producto.getCodigoBarras(), producto.getSku(),
                producto.getNombre(), producto.getDescripcion(), producto.getPrecio(),
                producto.getTipoPrenda(), producto.getCategoriasIds() == null
                        ? null : new HashSet<>(producto.getCategoriasIds()));
    }

    private VentaDTO adaptarVenta(IVentaDTO venta) {
        if (venta == null) {
            return null;
        }
        return new VentaDTO(venta.getId(), venta.getFolio(), venta.getFechaHora(),
                venta.getSubtotal(), venta.getIva(), venta.getTotal(),
                venta.getIdUsuario(), venta.getDetalles(), venta.getPagos());
    }

    private UsuarioDTO adaptarUsuario(IUsuarioDTO usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioDTO(usuario.getId(), usuario.getUsuario(), usuario.getContrasenia(),
                usuario.getNombres(), usuario.getPrimerApellido(), usuario.getSegundoApellido(),
                usuario.getRolEmpleado());
    }
    @Override
    public List<ProductoDTO> buscarProductos(String criterio) {
        List<ProductoDTO> resultado = new ArrayList<>();
        List<? extends IProductoDTO> productos = gestorInventario.buscarProductos(criterio);
        if (productos != null) {
            for (IProductoDTO producto : productos) {
                if (producto != null) resultado.add(adaptarProducto(producto));
            }
        }
        return resultado;
    }
    @Override
    public List<ProductoDTO> buscarProductosPorCategoria(String nombre) {
        List<ProductoDTO> productos = new ArrayList<>();
        for (IProductoDTO producto : gestorInventario.buscarProductosPorCategoria(nombre)) {
            productos.add(adaptarProducto(producto));
        }
        return productos;
    }
}
