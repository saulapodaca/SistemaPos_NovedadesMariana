package bos;

import dtos.PagoDTO;
import interfaces.IPagoBO;
import fachada.FachadaPersistencia;
import interfaces.IFachadaPersistencia;
import mappers.PagoMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/** Prepara pagos para persistirlos en cascada con la venta y los consulta después. */
public class PagoBO implements IPagoBO {

    private static PagoBO instance;
    private final IFachadaPersistencia persistencia;

    private PagoBO() {
        this(FachadaPersistencia.getInstance());
    }

    public PagoBO(IFachadaPersistencia persistencia) {
        this.persistencia = java.util.Objects.requireNonNull(persistencia);
    }

    public static synchronized PagoBO getInstance() {
        if (instance == null) {
            instance = new PagoBO();
        }
        return instance;
    }

    /** Valida y normaliza el pago que se persistirá como parte de la venta. */
    @Override
    public PagoDTO prepararPagoParaVenta(PagoDTO pagoDTO) {
        validarPago(pagoDTO, false);
        String metodo = pagoDTO.getMetodoPago().trim().toUpperCase(Locale.ROOT);
        float cambio = "EFECTIVO".equals(metodo)
                ? calcularCambio(pagoDTO.getMonto(), pagoDTO.getMontoRecibido())
                : 0.0f;
        float recibido = "EFECTIVO".equals(metodo)
                ? pagoDTO.getMontoRecibido() : pagoDTO.getMonto();

        return new PagoDTO(0, pagoDTO.getIdVenta(), metodo,
                pagoDTO.getMonto(), recibido, cambio, LocalDateTime.now());
    }

    @Override
    public synchronized PagoDTO registrarPago(PagoDTO pagoDTO) {
        // El pago ya se guardó junto con la venta por la operación de registro.
        // Este método recupera y confirma ese registro; no crea otra fila.
        validarPago(pagoDTO, true);
        PagoDTO pagoRegistrado = PagoMapper.toDTO(persistencia.buscarPagoPorVenta((long) pagoDTO.getIdVenta()));
        if (pagoRegistrado != null) pagoRegistrado.setIdVenta(pagoDTO.getIdVenta());
        if (pagoRegistrado == null) {
            throw new IllegalStateException(
                    "El pago debe quedar asociado al registrar la venta.");
        }
        if (pagoDTO.getId() > 0 && pagoRegistrado.getId() != pagoDTO.getId()) {
            throw new IllegalArgumentException("El pago no corresponde a la venta indicada.");
        }
        if (!pagoRegistrado.getMetodoPago().equalsIgnoreCase(
                pagoDTO.getMetodoPago().trim())
                || Math.abs(pagoRegistrado.getMonto() - pagoDTO.getMonto()) > 0.001f) {
            throw new IllegalArgumentException("Los datos del pago no coinciden con la venta.");
        }
        return pagoRegistrado;
    }

    @Override
    public float calcularCambio(float totalVenta, float montoRecibido) {
        if (!Float.isFinite(totalVenta) || totalVenta <= 0.0f) {
            throw new IllegalArgumentException(
                    "El total de la venta debe ser válido y mayor a cero.");
        }
        if (!Float.isFinite(montoRecibido) || montoRecibido < totalVenta) {
            throw new IllegalArgumentException(
                    "El monto recibido debe cubrir el total de la venta.");
        }
        return montoRecibido - totalVenta;
    }

    @Override
    public List<PagoDTO> obtenerPagosPorVenta(int idVenta) {
        if (idVenta <= 0) {
            throw new IllegalArgumentException("El ID de venta debe ser mayor a cero.");
        }
        PagoDTO pago = PagoMapper.toDTO(persistencia.buscarPagoPorVenta((long) idVenta));
        if (pago != null) pago.setIdVenta(idVenta);
        return pago == null ? List.of()
                : List.of(pago);
    }

    private static void validarPago(PagoDTO pago, boolean requiereVenta) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo.");
        }
        if (requiereVenta && pago.getIdVenta() <= 0) {
            throw new IllegalArgumentException(
                    "El pago debe estar asociado a una venta válida.");
        }
        if (pago.getMetodoPago() == null || pago.getMetodoPago().isBlank()) {
            throw new IllegalArgumentException("Debe indicar el método de pago.");
        }
        String metodo = pago.getMetodoPago().trim().toUpperCase(Locale.ROOT);
        if (!List.of("EFECTIVO", "TARJETA", "TRANSFERENCIA").contains(metodo)) {
            throw new IllegalArgumentException("Método de pago no válido.");
        }
        if (!Float.isFinite(pago.getMonto()) || pago.getMonto() <= 0.0f) {
            throw new IllegalArgumentException(
                    "El monto del pago debe ser válido y mayor a cero.");
        }
        if ("EFECTIVO".equals(metodo)
                && (!Float.isFinite(pago.getMontoRecibido())
                || pago.getMontoRecibido() < pago.getMonto())) {
            throw new IllegalArgumentException(
                    "El efectivo recibido debe cubrir el monto del pago.");
        }
    }
}
