/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mappers;

import dominio.Categoria;
import dominio.Producto;
import dtos.ProductoDTO;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author ramonsebastianzamudioayala
 */
public class ProductoMapper {
    public static ProductoDTO toDTO(Producto producto){
        Set<Long> categoriasIds = producto.getCategorias() == null 
            ? new HashSet<>() 
            : producto.getCategorias().stream()
                    .map(Categoria::getId) 
                    .collect(Collectors.toSet());
        return new ProductoDTO(producto.getId(),producto.getCodigoBarras(), producto.getSku(), 
                producto.getNombre(), producto.getDescripcion(), producto.getPrecio(),
                TipoPrendaMapper.toDto(producto.getTipoPrenda()),categoriasIds);
    }
    
    public static Producto toEntity(ProductoDTO dto, Set<Categoria> categorias) {
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setCodigoBarras(dto.getCodigoBarras());
        producto.setSku(dto.getSku());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setTipoPrenda(TipoPrendaMapper.toEntity(dto.getTipoPrenda()));
        producto.setCategorias(categorias != null ? categorias : new HashSet<>());
        return producto;
    }
}
