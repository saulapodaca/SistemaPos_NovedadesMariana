/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import dominio.Usuario;
import dtos.UsuarioDTO;
import java.util.List;

/**
 *
 * @author saula
 */
public interface IUsuarioBO {
    public UsuarioDTO autenticar(String usuario, String contrasenia) throws Exception ;
    public void guardar(UsuarioDTO usuario) throws Exception;
    public void actualizar(UsuarioDTO usuario) throws Exception;
    public void eliminar(Long id) throws Exception;
    public UsuarioDTO buscarPorId(Long id) throws Exception;
    public List<UsuarioDTO> obtenerTodos() throws Exception ;
    public Usuario buscarPorUsuario(String nombreUsuario) throws Exception ;
}
