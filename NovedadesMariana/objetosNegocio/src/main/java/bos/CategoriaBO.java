/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import dtos.CategoriaDTO;
import interfaces.ICategoriaBO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saula
 */
public class CategoriaBO implements ICategoriaBO{

    private static CategoriaBO instance;
    private final List<CategoriaDTO> categoriasMock;

    private CategoriaBO() {
        categoriasMock = new ArrayList<>();
        categoriasMock.add(new CategoriaDTO(1, "Bebé", "Ropa y mamelucos para recién nacidos"));
        categoriasMock.add(new CategoriaDTO(2, "Niña", "Vestidos y conjuntos infantiles"));
        categoriasMock.add(new CategoriaDTO(3, "Dama", "Blusas, faldas y vestidos casuales"));
    }

    public static synchronized CategoriaBO getInstance() {
        if (instance == null) {
            instance = new CategoriaBO();
        }
        return instance;
    }

    @Override
    public List<CategoriaDTO> listarCategorias() {
        return new ArrayList<>(categoriasMock);
    }

    @Override
    public CategoriaDTO obtenerPorId(int id) {
        return categoriasMock.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }
}