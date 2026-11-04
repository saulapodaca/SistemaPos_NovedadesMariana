package interfaces.persistencia;

import dtos.PagoDTO;
import java.util.List;

public interface IPagoDAO {
    void guardarDTO(PagoDTO pago) throws Exception;
    void actualizarDTO(PagoDTO pago) throws Exception;
    void eliminar(Long id) throws Exception;
    PagoDTO buscarPorIdDTO(Long id);
    PagoDTO buscarPorVentaDTO(Long idVenta);
    List<PagoDTO> obtenerTodosDTO();
}
