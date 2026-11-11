/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import java.util.ArrayList;

import dominio.Producto;

import dtos.ProductoDTO;
import interfaces.IProductoBO;
import daos.CategoriaDAO;
import daos.ProductoDAO;
import interfaces.ICategoriaDAO;
import interfaces.IProductoDAO;
import mappers.ProductoMapper;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author saula
 */
public class ProductoBO implements IProductoBO {

    private static ProductoBO instance;
    private final IProductoDAO productoDAO;
    private final ICategoriaDAO categoriaDAO;

    private ProductoBO() {
        this(new ProductoDAO(), new CategoriaDAO());
    }

    public ProductoBO(IProductoDAO productoDAO, ICategoriaDAO categoriaDAO) {
        this.productoDAO = java.util.Objects.requireNonNull(productoDAO);
        this.categoriaDAO = java.util.Objects.requireNonNull(categoriaDAO);
    }

    public static synchronized ProductoBO getInstance() {
        if (instance == null) {
            instance = new ProductoBO();
        }
        return instance;
    }

    @Override
    public void guardar(ProductoDTO productoDTO) throws Exception {
        validarProducto(productoDTO);
        if (productoDTO.getId() != null) {
            throw new IllegalArgumentException("Un nuevo producto no debe tener un ID asignado.");
        }
        ProductoDTO productoExistente = obtenerPorSku(productoDTO.getSku());
        if (productoExistente != null) {
            throw new IllegalArgumentException("Ya existe un producto registrado con el SKU: " + productoDTO.getSku());
        }
        ProductoDTO productoConCodigo = obtenerPorCodigoBarras(productoDTO.getCodigoBarras());
        if (productoConCodigo != null) {
            throw new IllegalArgumentException("Ya existe un producto registrado con el código de barras: "
                    + productoDTO.getCodigoBarras());
        }
        validarCategoriasExistentes(productoDTO.getCategoriasIds());
        productoDAO.guardar(ProductoMapper.toEntity(productoDTO));
    }

    @Override
    public void actualizar(ProductoDTO productoDTO) throws Exception {
        validarProducto(productoDTO);

        if (productoDTO.getId() == null || productoDTO.getId() <= 0) {
            throw new IllegalArgumentException("Para actualizar un producto, se requiere un ID válido.");
        }
        ProductoDTO actual = obtenerPorId(productoDTO.getId());
        if (actual == null) {
            throw new IllegalArgumentException("No se encontró ningún producto con el ID: " + productoDTO.getId());
        }

        ProductoDTO productoExistenteSKU = obtenerPorSku(productoDTO.getSku());
        if (productoExistenteSKU != null && !productoExistenteSKU.getId().equals(productoDTO.getId())) {
            throw new IllegalArgumentException("El SKU " + productoDTO.getSku() + " ya está asignado a otro producto.");
        }

        ProductoDTO productoExistenteCodigo = obtenerPorCodigoBarras(productoDTO.getCodigoBarras());
        if (productoExistenteCodigo != null
                && !productoExistenteCodigo.getId().equals(productoDTO.getId())) {
            throw new IllegalArgumentException("El código de barras "
                    + productoDTO.getCodigoBarras() + " ya está asignado a otro producto.");
        }

        validarCategoriasExistentes(productoDTO.getCategoriasIds());
        productoDAO.actualizar(ProductoMapper.toEntity(productoDTO));
    }

    @Override
    public void eliminar(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del producto a eliminar debe ser un número entero positivo.");
        }

        ProductoDTO existente = obtenerPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("No se puede eliminar: el producto con ID " + id + " no existe.");
        }

        productoDAO.eliminar(id);
    }

    @Override
    public List<ProductoDTO> obtenerTodos() {
        List<ProductoDTO> productos = convertirListaADTO(productoDAO.obtenerTodos());
        if (productos == null || productos.isEmpty()) {
            return Collections.emptyList();
        }
        return productos;
    }

    @Override
    public ProductoDTO obtenerPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser un número entero positivo.");
        }
        
        return ProductoMapper.toDTO(productoDAO.buscarProductoPorId(id));
    }

    @Override
    public ProductoDTO obtenerPorSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("El SKU no puede ser nulo ni estar vacío.");
        }
        
        return ProductoMapper.toDTO(productoDAO.buscarPorSku(sku.trim()));
    }

    @Override
    public ProductoDTO obtenerPorCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de barras no puede ser nulo ni estar vacío.");
        }

        return ProductoMapper.toDTO(productoDAO.buscarPorCodigoBarras(codigoBarras.trim()));
    }

    @Override
    public List<ProductoDTO> listarProductosPorCategoria(Long idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            throw new IllegalArgumentException("El ID de la categoría debe ser un número positivo.");
        }

        List<ProductoDTO> productos = convertirListaADTO(productoDAO.listarProductosPorCategoria(idCategoria));
        if (productos == null || productos.isEmpty()) {
            return Collections.emptyList();
        }
        return productos;
    }

    @Override
    public List<ProductoDTO> buscarProductos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            throw new IllegalArgumentException("El criterio de búsqueda no puede estar vacío.");
        }

        List<ProductoDTO> productos = convertirListaADTO(productoDAO.buscarProductos(criterio.trim()));
        if (productos == null || productos.isEmpty()) {
            return Collections.emptyList();
        }
        return productos;
    }

    /**
     * Valida de manera exhaustiva los atributos de entrada de un ProductoDTO.
     */
    public void validarProducto(ProductoDTO productoDTO) {
        if (productoDTO == null) {
            throw new IllegalArgumentException("El DTO del producto no puede ser nulo.");
        }
        if (productoDTO.getCodigoBarras() == null || productoDTO.getCodigoBarras().trim().isEmpty()) {
            throw new IllegalArgumentException("El código de barras es obligatorio.");
        }
        if (productoDTO.getSku() == null || productoDTO.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("El SKU es obligatorio.");
        }
        if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (productoDTO.getDescripcion() == null || productoDTO.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del producto es obligatoria.");
        }
        if (productoDTO.getPrecio() <= 0 || Float.isNaN(productoDTO.getPrecio()) || Float.isInfinite(productoDTO.getPrecio())) {
            throw new IllegalArgumentException("El precio del producto debe ser un número válido mayor a cero.");
        }
        if (productoDTO.getTipoPrenda() == null) {
            throw new IllegalArgumentException("El producto debe tener un tipo de prenda válido asignado.");
        }
        if (productoDTO.getCategoriasIds() == null || productoDTO.getCategoriasIds().isEmpty()) {
            throw new IllegalArgumentException("El producto debe estar asociado al menos a una categoría.");
        }
        for (Long catId : productoDTO.getCategoriasIds()) {
            if (catId == null || catId <= 0) {
                throw new IllegalArgumentException("El conjunto de categorías contiene identificadores no válidos.");
            }
        }
    }

    /**
     * Consulta y verifica que los IDs recibidos correspondan a categorías existentes en BD.
     */
    private void validarCategoriasExistentes(java.util.Set<Long> categoriasIds) {
        for (Long idCat : categoriasIds) {
            dominio.Categoria categoria = categoriaDAO.buscarCategoriaPorId(idCat);
            if (categoria == null) {
                throw new IllegalArgumentException("La categoría con ID " + idCat + " no existe en el sistema.");
            }
        }
    }

    private List<ProductoDTO> convertirListaADTO(List<Producto> entidades) {
        List<ProductoDTO> resultado = new ArrayList<>();
        if (entidades != null) {
            for (Producto entidad : entidades) {
                resultado.add(ProductoMapper.toDTO(entidad));
            }
        }
        return resultado;
    }
}
