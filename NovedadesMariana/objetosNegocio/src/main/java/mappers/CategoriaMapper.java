package mappers;

import dominio.Categoria;
import dtos.CategoriaDTO;


/**
 * Mapeador para transformar entre la entidad de dominio Categoria 
 * y su respectivo CategoriaDTO.
 */
public class CategoriaMapper {

    /**
     * Convierte una Entidad Categoria a CategoriaDTO.
     */
    public static CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }

    /**
     * Convierte un CategoriaDTO a la Entidad Categoria de dominio.
     */
    public static Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Categoria categoria = new Categoria();
        if (dto.getId() != null) {
            categoria.setId(dto.getId().longValue());
        }
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }

}