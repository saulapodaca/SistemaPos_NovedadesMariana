/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import dtos.CategoriaDTO;
import implementaciones.CategoriaDAO;
import interfaces.ICategoriaBO;
import interfaces.ICategoriaDAO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saula
 */
public class CategoriaBO implements ICategoriaBO{

    private static CategoriaBO instance;
    private ICategoriaDAO categoriaDAO;

    private CategoriaBO() {
        categoriaDAO = new CategoriaDAO();
        
    }

    public static synchronized CategoriaBO getInstance() {
        if (instance == null) {
            instance = new CategoriaBO();
        }
        return instance;
    }

    @Override
    public List<CategoriaDTO> listarCategorias() {
        return null;
    }

    @Override
    public CategoriaDTO obtenerPorId(int id) {
        return null;
    }
}
