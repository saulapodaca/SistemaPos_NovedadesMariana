/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import dominio.Usuario;
import java.util.List;

/**
 *
 * @author rauln
 */
public interface IUsuarioDAO {
    void guardar(Usuario usuario) throws Exception;
    void actualizar(Usuario usuario) throws Exception;
    void eliminar(Long id) throws Exception;
    Usuario buscarPorId(Long id);
    List<Usuario> obtenerTodos();
    Usuario buscarPorUsuario(String nombreUsuario);
}