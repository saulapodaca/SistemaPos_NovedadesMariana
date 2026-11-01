/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import dtos.PagoDTO;
import interfaces.IPagoBO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author saula
 */
public class PagoBO implements IPagoBO{

    private static PagoBO instance;
    private final List<PagoDTO> pagosMock;
    private int consecutivoId = 1;

    private PagoBO() {
        pagosMock = new ArrayList<>();
    }

    public static synchronized PagoBO getInstance() {
        if (instance == null) {
            instance = new PagoBO();
        }
        return instance;
    }

    @Override
    public synchronized PagoDTO registrarPago(PagoDTO pago) {
        validarPago(pago);
        String metodo = pago.getMetodoPago().trim().toUpperCase();
        pago.setMetodoPago(metodo);
        if ("EFECTIVO".equals(metodo)) {
            pago.setCambio(calcularCambio(pago.getMonto(), pago.getMontoRecibido()));
        } else {
            pago.setMontoRecibido(pago.getMonto());
            pago.setCambio(0.0f);
        }
        pago.setId(consecutivoId++);
        pago.setFechaHora(LocalDateTime.now());
        pagosMock.add(pago);
        return pago;
    }

    @Override
    public float calcularCambio(float totalVenta, float montoRecibido) {
        if (!Float.isFinite(totalVenta) || totalVenta <= 0.0f) {
            throw new IllegalArgumentException("El total de la venta debe ser válido y mayor a cero.");
        }
        if (!Float.isFinite(montoRecibido) || montoRecibido < totalVenta) {
            throw new IllegalArgumentException("El monto recibido debe cubrir el total de la venta.");
        }
        return montoRecibido - totalVenta;
    }

    @Override
    public List<PagoDTO> obtenerPagosPorVenta(int idVenta) {
        if (idVenta <= 0) {
            throw new IllegalArgumentException("El ID de venta debe ser mayor a cero.");
        }
        return pagosMock.stream()
                .filter(p -> p.getIdVenta() == idVenta)
                .collect(Collectors.toList());
    }

    private void validarPago(PagoDTO pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo.");
        }
        if (pago.getIdVenta() <= 0) {
            throw new IllegalArgumentException("El pago debe estar asociado a una venta válida.");
        }
        if (pago.getMetodoPago() == null || pago.getMetodoPago().isBlank()) {
            throw new IllegalArgumentException("Debe indicar el método de pago.");
        }
        String metodo = pago.getMetodoPago().trim().toUpperCase();
        if (!List.of("EFECTIVO", "TARJETA", "TRANSFERENCIA").contains(metodo)) {
            throw new IllegalArgumentException("Método de pago no válido.");
        }
        if (!Float.isFinite(pago.getMonto()) || pago.getMonto() <= 0.0f) {
            throw new IllegalArgumentException("El monto del pago debe ser válido y mayor a cero.");
        }
        if ("EFECTIVO".equals(metodo)
                && (!Float.isFinite(pago.getMontoRecibido()) || pago.getMontoRecibido() < pago.getMonto())) {
            throw new IllegalArgumentException("El efectivo recibido debe cubrir el monto del pago.");
        }
    }
}
