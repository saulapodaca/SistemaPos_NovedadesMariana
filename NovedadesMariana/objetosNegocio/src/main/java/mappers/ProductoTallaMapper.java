package mappers;

import dominio.Producto;
import dominio.ProductoTalla;
import dtos.ProductoTallaDTO;

public final class ProductoTallaMapper {
    private ProductoTallaMapper() { }
    public static ProductoTallaDTO toDTO(ProductoTalla talla) {
        if (talla == null) return null;
        return new ProductoTallaDTO(talla.getId() == null ? 0 : talla.getId().intValue(),
                talla.getProducto() == null || talla.getProducto().getId() == null ? 0 : talla.getProducto().getId().intValue(),
                talla.getTalla(), talla.getStockActual() == null ? 0 : talla.getStockActual());
    }
    public static ProductoTalla toEntity(ProductoTallaDTO dto) {
        if (dto == null) return null;
        ProductoTalla talla = new ProductoTalla();
        if (dto.getId() > 0) talla.setId((long) dto.getId());
        talla.setTalla(dto.getTalla());
        talla.setStockActual(dto.getStockActual());
        if (dto.getIdProducto() > 0) { Producto producto = new Producto(); producto.setId((long) dto.getIdProducto()); talla.setProducto(producto); }
        return talla;
    }
    public static java.util.List<ProductoTallaDTO> toDTOList(java.util.List<ProductoTalla> items) {
        return items == null ? java.util.Collections.emptyList()
                : items.stream().map(ProductoTallaMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }
}
