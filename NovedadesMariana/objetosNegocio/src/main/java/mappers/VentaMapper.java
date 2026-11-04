package mappers;

import dominio.DetalleVenta;
import dominio.ProductoTalla;
import dominio.Usuario;
import dominio.Venta;
import dtos.DetalleVentaDTO;
import dtos.PagoDTO;
import dtos.VentaDTO;
import interfaces.IFachadaPersistencia;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Mapea la venta transaccional y resuelve la talla antes de enviarla a persistencia. */
public final class VentaMapper {
    private VentaMapper() { }

    public static Venta toEntity(VentaDTO dto, IFachadaPersistencia persistencia) {
        if (dto == null) return null;
        Venta venta = new Venta();
        if (dto.getId() > 0) venta.setId((long) dto.getId());
        venta.setFolio(dto.getFolio());
        venta.setFechaHora(dto.getFechaHora());
        venta.setSubtotal(dto.getSubtotal());
        venta.setIva(dto.getIva());
        venta.setTotal(dto.getTotal());
        Usuario usuario = new Usuario();
        if (dto.getIdUsuario() > 0) usuario.setId((long) dto.getIdUsuario());
        venta.setUsuario(usuario);
        if (dto.getPagos() != null && !dto.getPagos().isEmpty()) {
            venta.setPago(PagoMapper.toEntity(dto.getPagos().get(0)));
        }
        if (dto.getDetalles() != null) {
            for (DetalleVentaDTO detalleDTO : dto.getDetalles()) {
                ProductoTalla talla = persistencia.buscarProductoTallaPorId((long) detalleDTO.getIdProductoTalla());
                if (talla == null || talla.getProducto() == null) {
                    throw new IllegalArgumentException("No existe la talla de producto " + detalleDTO.getIdProductoTalla());
                }
                DetalleVenta detalle = new DetalleVenta(detalleDTO.getCantidad(), detalleDTO.getPrecioUnitario(),
                        detalleDTO.getSubtotal(), talla.getProducto());
                venta.agregarDetalle(detalle);
            }
        }
        return venta;
    }

    public static Map<DetalleVenta, Long> tallasPorDetalle(Venta venta, VentaDTO dto) {
        Map<DetalleVenta, Long> resultado = new IdentityHashMap<>();
        if (venta == null || dto == null || venta.getDetalles() == null || dto.getDetalles() == null) return resultado;
        for (int i = 0; i < Math.min(venta.getDetalles().size(), dto.getDetalles().size()); i++) {
            resultado.put(venta.getDetalles().get(i), (long) dto.getDetalles().get(i).getIdProductoTalla());
        }
        return resultado;
    }

    /** La entidad DetalleVenta no guarda la talla, por eso el ID de talla no se puede reconstruir al consultar. */
    public static VentaDTO toDTO(Venta venta) {
        if (venta == null) return null;
        List<DetalleVentaDTO> detalles = new ArrayList<>();
        if (venta.getDetalles() != null) for (DetalleVenta d : venta.getDetalles()) {
            DetalleVentaDTO dto = new DetalleVentaDTO();
            dto.setId(d.getId() == null ? 0 : d.getId().intValue());
            dto.setIdVenta(venta.getId() == null ? 0 : venta.getId().intValue());
            dto.setIdProductoTalla(0);
            dto.setNombreProducto(d.getProducto() == null ? null : d.getProducto().getNombre());
            dto.setCantidad(d.getCantidad()); dto.setPrecioUnitario(d.getPrecioUnitario()); dto.setSubtotal(d.getSubtotal());
            detalles.add(dto);
        }
        PagoDTO pago = PagoMapper.toDTO(venta.getPago());
        if (pago != null) pago.setIdVenta(venta.getId() == null ? 0 : venta.getId().intValue());
        List<PagoDTO> pagos = pago == null ? List.of() : List.of(pago);
        return new VentaDTO(venta.getId() == null ? 0 : venta.getId().intValue(), venta.getFolio(), venta.getFechaHora(),
                venta.getSubtotal(), venta.getIva(), venta.getTotal(),
                venta.getUsuario() == null || venta.getUsuario().getId() == null ? 0 : venta.getUsuario().getId().intValue(), detalles, pagos);
    }
}
