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
    public List<CategoriaDTO> listarCategorias();
    public CategoriaDTO obtenerPorId(Long id);
    public void guardar(CategoriaDTO categoria) throws Exception;
    public void actualizar(CategoriaDTO categoria) throws Exception;
    public void eliminar(Long id) throws Exception;
    public CategoriaDTO buscarPorId(Long id);
    public List<CategoriaDTO> obtenerTodas();
}
