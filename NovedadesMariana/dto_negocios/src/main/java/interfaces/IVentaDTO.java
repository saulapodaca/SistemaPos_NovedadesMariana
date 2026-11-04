package interfaces;

import dtos.DetalleVentaDTO;
import dtos.PagoDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface IVentaDTO {
    int getId();
    void setId(int id);
    String getFolio();
    void setFolio(String folio);
    LocalDateTime getFechaHora();
    void setFechaHora(LocalDateTime fechaHora);
    float getSubtotal();
    void setSubtotal(float subtotal);
    float getIva();
    void setIva(float iva);
    float getTotal();
    void setTotal(float total);
    int getIdUsuario();
    void setIdUsuario(int idUsuario);
    List<DetalleVentaDTO> getDetalles();
    void setDetalles(List<DetalleVentaDTO> detalles);
    List<PagoDTO> getPagos();
    void setPagos(List<PagoDTO> pagos);
}
