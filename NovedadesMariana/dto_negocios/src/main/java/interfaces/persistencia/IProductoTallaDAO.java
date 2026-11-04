package interfaces.persistencia;

import dtos.ProductoTallaDTO;
import java.util.List;

public interface IProductoTallaDAO {
    void guardarDTO(ProductoTallaDTO talla) throws Exception;
    void actualizarDTO(ProductoTallaDTO talla) throws Exception;
    void eliminar(Long id) throws Exception;
    ProductoTallaDTO buscarPorIdDTO(Long id);
    List<ProductoTallaDTO> obtenerTodosDTO();
    List<ProductoTallaDTO> obtenerPorProductoDTO(Long idProducto);
}
