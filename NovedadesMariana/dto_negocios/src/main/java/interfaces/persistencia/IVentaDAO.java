package interfaces.persistencia;

import dtos.VentaDTO;
import java.util.List;

public interface IVentaDAO {
    VentaDTO guardarDTO(VentaDTO venta) throws Exception;
    void actualizarDTO(VentaDTO venta) throws Exception;
    boolean cancelar(Long id) throws Exception;
    VentaDTO buscarPorIdDTO(Long id);
    VentaDTO buscarPorFolioDTO(String folio);
    List<VentaDTO> obtenerTodasDTO();
}
