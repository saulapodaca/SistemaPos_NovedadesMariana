/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mappers;

import dominio.TipoPrenda;
import dtos.TipoPrendaDTO;

/**
 *
 * @author ramonsebastianzamudioayala
 */
public class TipoPrendaMapper {
    public static TipoPrenda toEntity(TipoPrendaDTO dto) {
        if (dto == null) {
            return null;
        }
        return TipoPrenda.valueOf(dto.name());
    }

    public static TipoPrendaDTO toDto(TipoPrenda entity) {
        if (entity == null) {
            return null;
        }
        return TipoPrendaDTO.valueOf(entity.name());
    }
}
