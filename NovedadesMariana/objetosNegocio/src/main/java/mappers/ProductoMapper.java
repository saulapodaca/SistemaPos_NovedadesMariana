package mappers;

import dominio.Categoria;
import dominio.Producto;
import dtos.ProductoDTO;
import java.util.stream.Collectors;

/** Conversiones entre Producto y el DTO usado por la capa de negocio. */
public final class ProductoMapper {
    private ProductoMapper() { }

    public static ProductoDTO toDTO(Producto producto) {
        if (producto == null) return null;
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setSku(producto.getSku());
        dto.setCodigoBarras(producto.getCodigoBarras());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio() == null ? 0f : producto.getPrecio());
        if (producto.getTipoPrenda() != null) {
            dto.setTipoPrenda(dtos.TipoPrendaDTO.valueOf(producto.getTipoPrenda().name()));
        }
        if (producto.getCategorias() != null) {
            dto.setCategoriasIds(producto.getCategorias().stream()
                    .map(Categoria::getId).filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    /** Las categorías se representan como referencias con ID; la persistencia debe resolverlas. */
    public static Producto toEntity(ProductoDTO dto) {
        if (dto == null) return null;
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setSku(dto.getSku());
        producto.setCodigoBarras(dto.getCodigoBarras());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        if (dto.getTipoPrenda() != null) {
            producto.setTipoPrenda(enums.TipoPrenda.valueOf(dto.getTipoPrenda().name()));
        }
        if (dto.getCategoriasIds() != null) {
            for (Long id : dto.getCategoriasIds()) {
                if (id != null) {
                    Categoria categoria = new Categoria();
                    categoria.setId(id);
                    producto.agregarCategoria(categoria);
                }
            }
        }
        return producto;
    }

}
