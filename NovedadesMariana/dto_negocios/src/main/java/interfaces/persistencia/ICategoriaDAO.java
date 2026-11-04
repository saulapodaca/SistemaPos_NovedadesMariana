package interfaces.persistencia;

import dtos.CategoriaDTO;
import java.util.List;

public interface ICategoriaDAO {
    void guardarDTO(CategoriaDTO categoria) throws Exception;
    void actualizarDTO(CategoriaDTO categoria) throws Exception;
    void eliminar(Long id) throws Exception;
    CategoriaDTO buscarPorIdDTO(Long id);
    List<CategoriaDTO> obtenerTodasDTO();
}
