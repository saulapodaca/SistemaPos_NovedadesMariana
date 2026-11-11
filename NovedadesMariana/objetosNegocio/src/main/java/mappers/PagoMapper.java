package mappers;

import dominio.Pago;
import dtos.PagoDTO;

public final class PagoMapper {
    private PagoMapper() { }
    public static PagoDTO toDTO(Pago pago) {
        if (pago == null) return null;
        PagoDTO dto = new PagoDTO();
        dto.setId(pago.getId() == null ? 0 : pago.getId().intValue());
        dto.setMetodoPago(pago.getMetodoPago() == null ? null : pago.getMetodoPago().name());
        dto.setMonto(pago.getMonto() == null ? 0f : pago.getMonto());
        dto.setMontoRecibido(pago.getMontoRecibido() == null ? 0f : pago.getMontoRecibido());
        dto.setCambio(pago.getCambio() == null ? 0f : pago.getCambio());
        dto.setFechaHora(pago.getFechaHora());
        return dto;
    }
    public static Pago toEntity(PagoDTO dto) {
        if (dto == null) return null;
        Pago pago = new Pago();
        if (dto.getId() > 0) pago.setId((long) dto.getId());
        if (dto.getMetodoPago() != null && !dto.getMetodoPago().isBlank()) pago.setMetodoPago(enums.MetodoPago.valueOf(dto.getMetodoPago()));
        pago.setMonto(dto.getMonto()); pago.setMontoRecibido(dto.getMontoRecibido());
        pago.setCambio(dto.getCambio()); pago.setFechaHora(dto.getFechaHora());
        return pago;
    }
}
