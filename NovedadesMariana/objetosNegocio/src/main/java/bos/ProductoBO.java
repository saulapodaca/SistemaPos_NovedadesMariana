/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import dtos.ProductoDTO;
import interfaces.IProductoBO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author saula
 */
public class ProductoBO implements IProductoBO {

    private static ProductoBO instance;
    private final List<ProductoDTO> productosMock;

    private ProductoBO() {
        productosMock = new ArrayList<>();
        // Mock de prendas
        productosMock.add(new ProductoDTO(1, "7501001", "SKU-VES-01", "Vestido Flores Niña", "Vestido casual de algodón", 350.00f, "VESTIDO", 2));
        productosMock.add(new ProductoDTO(2, "7501002", "SKU-MAM-02", "Mameluco Osito Bebé", "Mameluco térmico afelpado", 220.00f, "MAMELUCO", 1));
        productosMock.add(new ProductoDTO(3, "7501003", "SKU-BLU-03", "Blusa Cuello V Dama", "Blusa de lino manga corta", 280.00f, "BLUSA", 3));
    }

    public static synchronized ProductoBO getInstance() {
        if (instance == null) {
            instance = new ProductoBO();
        }
        return instance;
    }

    @Override
    public ProductoDTO obtenerPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de producto debe ser mayor a cero.");
        }
        return productosMock.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public ProductoDTO obtenerPorSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("El SKU o código de barras no puede estar vacío.");
        }
        String codigo = sku.trim();
        return productosMock.stream()
                .filter(p -> p.getSku().equalsIgnoreCase(codigo) || p.getCodigoBarras().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ProductoDTO> listarProductosPorCategoria(int idCategoria) {
        if (idCategoria <= 0) {
            throw new IllegalArgumentException("El ID de categoría debe ser mayor a cero.");
        }
        return productosMock.stream()
                .filter(p -> p.getIdCategoria() == idCategoria)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> buscarProductos(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            throw new IllegalArgumentException("El criterio de búsqueda no puede estar vacío.");
        }
        String busqueda = criterio.trim();
        String lower = busqueda.toLowerCase();
        return productosMock.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(lower)
                        || p.getCodigoBarras().contains(busqueda)
                        || p.getSku().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
