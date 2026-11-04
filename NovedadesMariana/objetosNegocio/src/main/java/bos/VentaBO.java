package bos;

import dtos.DetalleVentaDTO;
import dtos.PagoDTO;
import dtos.ResumenVentaDTO;
import dtos.VentaDTO;
import interfaces.IPagoBO;
import interfaces.IVentaBO;
import fachada.FachadaPersistencia;
import interfaces.IFachadaPersistencia;
import mappers.VentaMapper;
import mappers.PagoMapper;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

/** Reglas de venta, cálculo de importes y coordinación con la persistencia. */
public class VentaBO implements IVentaBO {

    private static final float IVA_RATE = 0.16f;
    private static final double IMPORTE_TOLERANCE = 0.001d;

    private static VentaBO instance;
    private final IFachadaPersistencia persistencia;
    private final IPagoBO pagoBO;

    private VentaBO() {
        this(FachadaPersistencia.getInstance(), PagoBO.getInstance());
    }

    public VentaBO(IFachadaPersistencia persistencia, IPagoBO pagoBO) {
        this.persistencia = java.util.Objects.requireNonNull(persistencia);
        this.pagoBO = java.util.Objects.requireNonNull(pagoBO);
    }

    public static synchronized VentaBO getInstance() {
        if (instance == null) {
            instance = new VentaBO();
        }
        return instance;
    }

    @Override
    public synchronized VentaDTO registrarVenta(VentaDTO ventaDTO) {
        validarVenta(ventaDTO);

        if (ventaDTO.getFolio() == null || ventaDTO.getFolio().isBlank()) {
            ventaDTO.setFolio("VEN-" + UUID.randomUUID().toString().replace("-", ""));
        }
        if (ventaDTO.getFechaHora() == null) {
            ventaDTO.setFechaHora(LocalDateTime.now());
        }
        PagoDTO pagoPreparado = pagoBO.prepararPagoParaVenta(ventaDTO.getPagos().get(0));
        VentaDTO ventaPreparada = new VentaDTO(
                ventaDTO.getId(), ventaDTO.getFolio(), ventaDTO.getFechaHora(),
                ventaDTO.getSubtotal(), ventaDTO.getIva(), ventaDTO.getTotal(),
                ventaDTO.getIdUsuario(), ventaDTO.getDetalles(), List.of(pagoPreparado));

        try {
            dominio.Venta entidad = VentaMapper.toEntity(ventaPreparada, persistencia);
            persistencia.registrarVentaConTallas(entidad, VentaMapper.tallasPorDetalle(entidad, ventaPreparada));
            if (entidad.getId() != null) ventaPreparada.setId(entidad.getId().intValue());
            if (entidad.getId() != null) {
                PagoDTO pagoPersistido = PagoMapper.toDTO(entidad.getPago());
                if (pagoPersistido != null) {
                    pagoPersistido.setIdVenta(ventaPreparada.getId());
                    ventaPreparada.setPagos(List.of(pagoPersistido));
                }
            }
            return ventaPreparada;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo registrar la venta.", e);
        }
    }

    @Override
    public VentaDTO obtenerPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de venta debe ser mayor a cero.");
        }
        return VentaMapper.toDTO(persistencia.buscarVentaPorId((long) id));
    }

    @Override
    public VentaDTO obtenerPorFolio(String folio) {
        if (folio == null || folio.isBlank()) {
            throw new IllegalArgumentException("El folio de venta no puede estar vacío.");
        }
        return VentaMapper.toDTO(persistencia.buscarVentaPorFolio(folio));
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
        for (DetalleVentaDTO detalle : detalles) {
            validarDetalle(detalle);
            subtotal += detalle.getCantidad() * detalle.getPrecioUnitario();
            if (!Float.isFinite(subtotal)) {
                throw new IllegalArgumentException("El subtotal excede el rango permitido.");
            }
        }
        float montoDescuento = subtotal * (descuento / 100.0f);
        float baseGravable = subtotal - montoDescuento;
        float iva = baseGravable * IVA_RATE;
        float total = baseGravable + iva;
        if (!Float.isFinite(montoDescuento)
                || !Float.isFinite(iva)
                || !Float.isFinite(total)) {
            throw new IllegalArgumentException("Los importes calculados exceden el rango permitido.");
        }
        return new ResumenVentaDTO(subtotal, iva, montoDescuento, total);
    }

    @Override
    public boolean cancelarVenta(int idVenta) {
        if (idVenta <= 0) {
            throw new IllegalArgumentException("El ID de venta debe ser mayor a cero.");
        }
        try {
            return persistencia.cancelarVenta((long) idVenta);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cancelar la venta.", e);
        }
    }

    private static void validarVenta(VentaDTO venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula.");
        }
        if (venta.getId() != 0) {
            throw new IllegalArgumentException("Una venta nueva no debe incluir un ID asignado.");
        }
        if (venta.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("La venta debe tener un empleado válido.");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un detalle.");
        }
        if (venta.getPagos() == null || venta.getPagos().size() != 1) {
            throw new IllegalArgumentException("La venta debe incluir exactamente un pago.");
        }
        PagoDTO pago = venta.getPagos().get(0);
        if (pago == null || !Float.isFinite(pago.getMonto())
                || Math.abs(pago.getMonto() - venta.getTotal()) > 0.001f) {
            throw new IllegalArgumentException(
                    "El monto del pago debe coincidir con el total de la venta.");
        }
        if (!Float.isFinite(venta.getSubtotal()) || venta.getSubtotal() < 0.0f
                || !Float.isFinite(venta.getIva()) || venta.getIva() < 0.0f
                || !Float.isFinite(venta.getTotal()) || venta.getTotal() <= 0.0f) {
            throw new IllegalArgumentException("Los importes de la venta deben ser válidos y no negativos.");
        }
        double subtotalDetalles = 0.0d;
        for (DetalleVentaDTO detalle : venta.getDetalles()) {
            validarDetalle(detalle);
            double subtotalDetalleEsperado = (double) detalle.getCantidad()
                    * detalle.getPrecioUnitario();
            if (!Double.isFinite(detalle.getSubtotal())
                    || detalle.getSubtotal() < 0.0f
                    || !coincideImporte(detalle.getSubtotal(), subtotalDetalleEsperado)) {
                throw new IllegalArgumentException(
                        "El subtotal de cada detalle debe coincidir con cantidad por precio.");
            }
            subtotalDetalles += subtotalDetalleEsperado;
        }

        if (!coincideImporte(venta.getSubtotal(), subtotalDetalles)) {
            throw new IllegalArgumentException(
                    "El subtotal de la venta debe coincidir con la suma de sus detalles.");
        }

        double baseGravable = (double) venta.getTotal() - venta.getIva();
        if (baseGravable < -IMPORTE_TOLERANCE
                || baseGravable - venta.getSubtotal() > IMPORTE_TOLERANCE) {
            throw new IllegalArgumentException("Los importes de la venta son inconsistentes.");
        }
        double ivaEsperado = Math.max(0.0d, baseGravable) * IVA_RATE;
        double totalEsperado = Math.max(0.0d, baseGravable) + ivaEsperado;
        if (!coincideImporte(venta.getIva(), ivaEsperado)
                || !coincideImporte(venta.getTotal(), totalEsperado)) {
            throw new IllegalArgumentException(
                    "El IVA y el total deben corresponder a la base gravable.");
        }
    }

    private static void validarDetalle(DetalleVentaDTO detalle) {
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

    private static boolean coincideImporte(double actual, double esperado) {
        return Double.isFinite(actual)
                && Double.isFinite(esperado)
                && Math.abs(actual - esperado) <= IMPORTE_TOLERANCE;
    }

}
