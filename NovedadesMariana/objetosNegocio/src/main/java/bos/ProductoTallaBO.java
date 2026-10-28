/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bos;

import dtos.ProductoTallaDTO;
import interfaces.IProductoTallaBO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author saula
 */
public class ProductoTallaBO implements IProductoTallaBO{

    private static ProductoTallaBO instance;
    private final List<ProductoTallaDTO> tallasMock;

    private ProductoTallaBO() {
        tallasMock = new ArrayList<>();
        // Producto 1 (Vestido)
        tallasMock.add(new ProductoTallaDTO(1, 1, "4", 8));
        tallasMock.add(new ProductoTallaDTO(2, 1, "6", 5));
        // Producto 2 (Mameluco)
        tallasMock.add(new ProductoTallaDTO(3, 2, "3-6M", 12));
        tallasMock.add(new ProductoTallaDTO(4, 2, "6-12M", 6));
        // Producto 3 (Blusa)
        tallasMock.add(new ProductoTallaDTO(5, 3, "CH", 10));
        tallasMock.add(new ProductoTallaDTO(6, 3, "M", 15));
        tallasMock.add(new ProductoTallaDTO(7, 3, "G", 4));
    }

    public static synchronized ProductoTallaBO getInstance() {
        if (instance == null) {
            instance = new ProductoTallaBO();
        }
        return instance;
    }

    @Override
    public boolean validarStockDisponible(int idProductoTalla, int cantidadRequerida) {
        return tallasMock.stream()
                .filter(t -> t.getId() == idProductoTalla)
                .findFirst()
                .map(t -> t.getStockActual() >= cantidadRequerida)
                .orElse(false);
    }

    @Override
    public synchronized boolean descontarStock(int idProductoTalla, int cantidad) {
        for (ProductoTallaDTO talla : tallasMock) {
            if (talla.getId() == idProductoTalla) {
                if (talla.getStockActual() >= cantidad) {
                    talla.setStockActual(talla.getStockActual() - cantidad);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean reabastecerStock(int idProductoTalla, int cantidad) {
        for (ProductoTallaDTO talla : tallasMock) {
            if (talla.getId() == idProductoTalla) {
                talla.setStockActual(talla.getStockActual() + cantidad);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto) {
        return tallasMock.stream()
                .filter(t -> t.getIdProducto() == idProducto)
                .collect(Collectors.toList());
    }
}