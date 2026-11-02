/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mappers;

import dominio.Categoria;
import dtos.CategoriaDTO;

/**
 *
 * @author ramonsebastianzamudioayala
 */
public class CategoriaMapper {
    
    public static CategoriaDTO toDto(Categoria categoria){
        return new CategoriaDTO(categoria.getId(),categoria.getNombre(),categoria.getDescripcion());
    }
    
    public static Categoria toEntity(CategoriaDTO categoria){
        return new Categoria(categoria.getNombre(),categoria.getDescripcion());
    }
    
    
}
