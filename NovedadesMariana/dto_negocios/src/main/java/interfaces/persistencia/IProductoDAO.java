package interfaces.persistencia;

import dtos.ProductoDTO;
import java.util.List;

public interface IProductoDAO {
    void guardarDTO(ProductoDTO producto) throws Exception;
    void actualizarDTO(ProductoDTO producto) throws Exception;
    void eliminar(Long id) throws Exception;
    ProductoDTO buscarPorIdDTO(Long id);
    List<ProductoDTO> obtenerTodosDTO();
    ProductoDTO obtenerPorSkuDTO(String sku);
    ProductoDTO obtenerPorCodigoBarrasDTO(String codigoBarras);
    List<ProductoDTO> listarProductosPorCategoriaDTO(Long idCategoria);
    List<ProductoDTO> buscarProductosDTO(String criterio);
}
