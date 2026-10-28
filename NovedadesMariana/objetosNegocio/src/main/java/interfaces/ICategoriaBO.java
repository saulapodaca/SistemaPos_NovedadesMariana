/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import dtos.CategoriaDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface ICategoriaBO {
    List<CategoriaDTO> listarCategorias();

    CategoriaDTO obtenerPorId(int id);
}
