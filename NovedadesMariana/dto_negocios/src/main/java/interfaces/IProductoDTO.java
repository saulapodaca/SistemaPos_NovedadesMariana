package interfaces;

import dtos.TipoPrendaDTO;
import java.util.Set;

public interface IProductoDTO {
    Long getId();
    void setId(Long id);
    String getCodigoBarras();
    void setCodigoBarras(String codigoBarras);
    String getSku();
    void setSku(String sku);
    String getNombre();
    void setNombre(String nombre);
    String getDescripcion();
    void setDescripcion(String descripcion);
    float getPrecio();
    void setPrecio(float precio);
    TipoPrendaDTO getTipoPrenda();
    void setTipoPrenda(TipoPrendaDTO tipoPrenda);
    Set<Long> getCategoriasIds();
    void setCategoriasIds(Set<Long> categoriasIds);
}
