/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mappers;

import dominio.Pago;
import dtos.PagoDTO;

/**
 *
 * @author ramonsebastianzamudioayala
 */
public class PagoMapper {
    public static PagoDTO toDTO(Pago pago){
        return new PagoDTO();
    }
    public static Pago toEntity(PagoDTO pago){
        return new Pago();
    }
}
