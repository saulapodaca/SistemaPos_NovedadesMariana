/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import dtos.DetalleVentaDTO;
import dtos.ResumenVentaDTO;
import dtos.VentaDTO;
import interfaces.IVentaBO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saula
 */
public class VentaBO implements IVentaBO{

    private static VentaBO instance;
    private final List<VentaDTO> ventasMock;
    private int consecutivoId = 1;

    private VentaBO() {
        ventasMock = new ArrayList<>();
    }

    public static synchronized VentaBO getInstance() {
        if (instance == null) {
            instance = new VentaBO();
        }
        return instance;
    }

    @Override
    public synchronized VentaDTO registrarVenta(VentaDTO venta) {
        validarVenta(venta);
        venta.setId(consecutivoId++);
        venta.setFolio("VEN-" + String.format("%05d", venta.getId()));
        venta.setFechaHora(LocalDateTime.now());
        ventasMock.add(venta);
        return venta;
    }

    @Override
    public VentaDTO obtenerPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de venta debe ser mayor a cero.");
        }
        return ventasMock.stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public VentaDTO obtenerPorFolio(String folio) {
        if (folio == null || folio.isBlank()) {
            throw new IllegalArgumentException("El folio de venta no puede estar vacío.");
        }
        String folioBusqueda = folio.trim();
        return ventasMock.stream()
                .filter(v -> v.getFolio().equalsIgnoreCase(folioBusqueda))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ResumenVentaDTO calcularTotales(List<DetalleVentaDTO> detalles, float descuento) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un detalle.");
        }
        if (!Float.isFinite(descuento) || descuento < 0.0f || descuento > 100.0f) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100 por ciento.");
        }
        float subtotal = 0.0f;
        for (DetalleVentaDTO d : detalles) {
            validarDetalle(d);
            subtotal += d.getCantidad() * d.getPrecioUnitario();
            if (!Float.isFinite(subtotal)) {
                throw new IllegalArgumentException("El subtotal excede el rango permitido.");
            }
        }
        float montoDescuento = subtotal * (descuento / 100.0f);
        float baseGravable = subtotal - montoDescuento;
        float iva = baseGravable * 0.16f; // IVA 16%
        float total = baseGravable + iva;

        return new ResumenVentaDTO(subtotal, iva, montoDescuento, total);
    }

    @Override
    public boolean cancelarVenta(int idVenta) {
        if (idVenta <= 0) {
            throw new IllegalArgumentException("El ID de venta debe ser mayor a cero.");
        }
        return ventasMock.removeIf(v -> v.getId() == idVenta);
    }

    private void validarVenta(VentaDTO venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula.");
        }
        if (venta.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("La venta debe tener un empleado válido.");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un detalle.");
        }
        if (!Float.isFinite(venta.getSubtotal()) || venta.getSubtotal() < 0.0f
                || !Float.isFinite(venta.getIva()) || venta.getIva() < 0.0f
                || !Float.isFinite(venta.getTotal()) || venta.getTotal() <= 0.0f) {
            throw new IllegalArgumentException("Los importes de la venta deben ser válidos y no negativos.");
        }
        for (DetalleVentaDTO detalle : venta.getDetalles()) {
            validarDetalle(detalle);
        }
    }

    private void validarDetalle(DetalleVentaDTO detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("La venta contiene un detalle nulo.");
        }
        if (detalle.getIdProductoTalla() <= 0) {
            throw new IllegalArgumentException("Cada detalle debe identificar una talla de producto válida.");
        }
        if (detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad de cada detalle debe ser mayor a cero.");
        }
        if (!Float.isFinite(detalle.getPrecioUnitario()) || detalle.getPrecioUnitario() < 0.0f) {
            throw new IllegalArgumentException("El precio unitario debe ser válido y no negativo.");
        }
    }
}
