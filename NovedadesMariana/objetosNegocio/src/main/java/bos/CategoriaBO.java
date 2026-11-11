/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import java.util.ArrayList;

import dominio.Categoria;

import dtos.CategoriaDTO;
import interfaces.ICategoriaBO;
import daos.CategoriaDAO;
import interfaces.ICategoriaDAO;
import mappers.CategoriaMapper;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author saula
 */
public class CategoriaBO implements ICategoriaBO {

    private static CategoriaBO instance;
    private final ICategoriaDAO categoriaDAO;

    private CategoriaBO() {
        this(new CategoriaDAO());
    }

    public CategoriaBO(ICategoriaDAO categoriaDAO) {
        this.categoriaDAO = java.util.Objects.requireNonNull(categoriaDAO);
    }

    public static synchronized CategoriaBO getInstance() {
        if (instance == null) {
            instance = new CategoriaBO();
        }
        return instance;
    }

    @Override
    public void guardar(CategoriaDTO categoriaDTO) throws Exception {
        validarCategoria(categoriaDTO);

        if (categoriaDTO.getId() != null) {
            throw new IllegalArgumentException("Una categoría nueva no debe incluir un ID.");
        }

        validarNombreUnico(categoriaDTO.getNombre(), null);

        categoriaDAO.guardar(CategoriaMapper.toEntity(categoriaDTO));
    }

    @Override
    public void actualizar(CategoriaDTO categoriaDTO) throws Exception {
        validarCategoria(categoriaDTO);

        if (categoriaDTO.getId() == null || categoriaDTO.getId() <= 0) {
            throw new IllegalArgumentException("Para actualizar una categoría se requiere un ID válido.");
        }

        CategoriaDTO existente = obtenerPorId(categoriaDTO.getId());
        if (existente == null) {
            throw new IllegalArgumentException("No se encontró la categoría con ID: " + categoriaDTO.getId());
        }

        validarNombreUnico(categoriaDTO.getNombre(), categoriaDTO.getId());

        categoriaDAO.actualizar(CategoriaMapper.toEntity(categoriaDTO));
    }

    @Override
    public void eliminar(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID de la categoría a eliminar debe ser un número entero positivo.");
        }

        CategoriaDTO existente = obtenerPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No se puede eliminar: la categoría con ID " + id + " no existe.");
        }

        categoriaDAO.eliminar(id);
    }

    @Override
    public CategoriaDTO obtenerPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID de la categoría debe ser un número entero positivo.");
        }

        return CategoriaMapper.toDTO(categoriaDAO.buscarCategoriaPorId(id));
    }

    @Override
    public CategoriaDTO buscarPorId(Long id) {
        // Redirige al método obtenerPorId para mantener coherencia
        return obtenerPorId(id);
    }

    @Override
    public List<CategoriaDTO> listarCategorias() {
        return obtenerTodas();
    }

    @Override
    public List<CategoriaDTO> obtenerTodas() {
        List<CategoriaDTO> categorias = convertirListaADTO(categoriaDAO.obtenerTodas());
        if (categorias == null || categorias.isEmpty()) {
            return Collections.emptyList();
        }
        return categorias;
    }


    /**
     * Valida la estructura y restricciones de un CategoriaDTO.
     */
    public void validarCategoria(CategoriaDTO categoriaDTO) {
        if (categoriaDTO == null) {
            throw new IllegalArgumentException("El DTO de la categoría no puede ser nulo.");
        }
        if (categoriaDTO.getNombre() == null || categoriaDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        if (categoriaDTO.getNombre().trim().length() > 50) {
            throw new IllegalArgumentException("El nombre de la categoría no puede exceder los 50 caracteres.");
        }
    }

    /**
     * Comprueba que no exista otra categoría registrada con el mismo nombre.
     */
    private void validarNombreUnico(String nombre, Long idActual) {
        List<CategoriaDTO> todas = convertirListaADTO(categoriaDAO.obtenerTodas());
        if (todas == null || todas.isEmpty()) {
            return;
        }

        String nombreLimpio = nombre.trim();
        for (CategoriaDTO cat : todas) {
            boolean mismoNombre = cat.getNombre() != null && cat.getNombre().trim().equalsIgnoreCase(nombreLimpio);
            boolean esMismoRegistro = idActual != null && idActual.equals(cat.getId());

            if (mismoNombre && !esMismoRegistro) {
                throw new IllegalArgumentException("Ya existe una categoría registrada con el nombre: " + nombreLimpio);
            }
        }
    }

    private List<CategoriaDTO> convertirListaADTO(List<Categoria> entidades) {
        List<CategoriaDTO> resultado = new ArrayList<>();
        if (entidades != null) {
            for (Categoria entidad : entidades) {
                resultado.add(CategoriaMapper.toDTO(entidad));
            }
        }
        return resultado;
    }
}
