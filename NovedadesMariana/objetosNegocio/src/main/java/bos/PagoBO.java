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
        pago.setId(consecutivoId++);
        pago.setFechaHora(LocalDateTime.now());
        pagosMock.add(pago);
        return pago;
    }

    @Override
    public float calcularCambio(float totalVenta, float montoRecibido) {
        if (montoRecibido < totalVenta) {
            return 0.0f;
        }
        return montoRecibido - totalVenta;
    }

    @Override
    public List<PagoDTO> obtenerPagosPorVenta(int idVenta) {
        return pagosMock.stream()
                .filter(p -> p.getIdVenta() == idVenta)
                .collect(Collectors.toList());
    }
}
