/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import dominio.Categoria;
import dominio.Producto;
import dtos.ProductoDTO;
import implementaciones.CategoriaDAO;
import implementaciones.ProductoDAO;
import interfaces.ICategoriaDAO;
import interfaces.IProductoBO;
import interfaces.IProductoDAO;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mappers.ProductoMapper;

/**
 *
 * @author saula
 */
public class ProductoBO implements IProductoBO {

    private static ProductoBO instance;
    private final IProductoDAO productoDAO;
    private final ICategoriaDAO categoriaDAO;

    private ProductoBO() {
        this.productoDAO = new ProductoDAO();
        this.categoriaDAO = new CategoriaDAO();
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
        if (productoDTO.getId() != null && productoDTO.getId() > 0) {
            throw new IllegalArgumentException("Un nuevo producto no debe tener un ID asignado.");
        }
        ProductoDTO productoExistente = obtenerPorSku(productoDTO.getSku());
        if (productoExistente != null) {
            throw new IllegalArgumentException("Ya existe un producto registrado con el SKU: " + productoDTO.getSku());
        }
        Set<Categoria> categorias = consultarCategoriasValidas(productoDTO.getCategoriasIds());

        // Convertir DTO a Entidad inyectando las categorías reales
        Producto producto = ProductoMapper.toEntity(productoDTO, categorias);

        productoDAO.guardar(producto);
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

        Set<Categoria> categorias = consultarCategoriasValidas(productoDTO.getCategoriasIds());
        Producto producto = ProductoMapper.toEntity(productoDTO, categorias);

        productoDAO.actualizar(producto);
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
        List<Producto> productos = productoDAO.obtenerTodos();
        if (productos == null || productos.isEmpty()) {
            return Collections.emptyList();
        }

        return productos.stream()
                .map(ProductoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoDTO obtenerPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser un número entero positivo.");
        }
        
        Producto producto = productoDAO.buscarPorId(id);
        return producto != null ? ProductoMapper.toDTO(producto) : null;
    }

    @Override
    public ProductoDTO obtenerPorSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("El SKU no puede ser nulo ni estar vacío.");
        }
        
        Producto producto = productoDAO.obtenerPorSku(sku.trim());
        return producto != null ? ProductoMapper.toDTO(producto) : null;
    }

    @Override
    public List<ProductoDTO> listarProductosPorCategoria(Long idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            throw new IllegalArgumentException("El ID de la categoría debe ser un número positivo.");
        }

        List<Producto> productos = productoDAO.listarProductosPorCategoria(idCategoria);
        if (productos == null || productos.isEmpty()) {
            return Collections.emptyList();
        }

        return productos.stream()
                .map(ProductoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> buscarProductos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<Producto> productos = productoDAO.buscarProductos(criterio.trim());
        if (productos == null || productos.isEmpty()) {
            return Collections.emptyList();
        }

        return productos.stream()
                .map(ProductoMapper::toDTO)
                .collect(Collectors.toList());
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
    private Set<Categoria> consultarCategoriasValidas(Set<Long> categoriasIds) throws Exception {
        Set<Categoria> categorias = new HashSet<>();
        for (Long idCat : categoriasIds) {
            Categoria categoria = categoriaDAO.buscarPorId(idCat);
            if (categoria == null) {
                throw new IllegalArgumentException("La categoría con ID " + idCat + " no existe en el sistema.");
            }
            categorias.add(categoria);
        }
        return categorias;
    }
}