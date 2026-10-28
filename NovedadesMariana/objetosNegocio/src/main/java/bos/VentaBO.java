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
        venta.setId(consecutivoId++);
        venta.setFolio("VEN-" + String.format("%05d", venta.getId()));
        venta.setFechaHora(LocalDateTime.now());
        ventasMock.add(venta);
        return venta;
    }

    @Override
    public VentaDTO obtenerPorId(int id) {
        return ventasMock.stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public VentaDTO obtenerPorFolio(String folio) {
        return ventasMock.stream()
                .filter(v -> v.getFolio().equalsIgnoreCase(folio))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ResumenVentaDTO calcularTotales(List<DetalleVentaDTO> detalles, float descuento) {
        float subtotal = 0.0f;
        if (detalles != null) {
            for (DetalleVentaDTO d : detalles) {
                subtotal += d.getCantidad() * d.getPrecioUnitario();
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
        return ventasMock.removeIf(v -> v.getId() == idVenta);
    }
}