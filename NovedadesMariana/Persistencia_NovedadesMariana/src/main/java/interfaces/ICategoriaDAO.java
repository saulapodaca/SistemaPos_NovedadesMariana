/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import dominio.Categoria;
import java.util.List;

/**
 *
 * @author rauln
 */
public interface ICategoriaDAO {
    void guardar(Categoria categoria) throws Exception;
    void actualizar(Categoria categoria) throws Exception;
    void eliminar(Long id) throws Exception;
    Categoria buscarPorId(Long id);
    List<Categoria> obtenerTodas();
}