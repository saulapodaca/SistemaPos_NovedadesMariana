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
        return productosMock.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public ProductoDTO obtenerPorSku(String sku) {
        return productosMock.stream()
                .filter(p -> p.getSku().equalsIgnoreCase(sku) || p.getCodigoBarras().equals(sku))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ProductoDTO> listarProductosPorCategoria(int idCategoria) {
        return productosMock.stream()
                .filter(p -> p.getIdCategoria() == idCategoria)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> buscarProductos(String criterio) {
        String lower = criterio.toLowerCase();
        return productosMock.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(lower)
                        || p.getCodigoBarras().contains(criterio)
                        || p.getSku().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
