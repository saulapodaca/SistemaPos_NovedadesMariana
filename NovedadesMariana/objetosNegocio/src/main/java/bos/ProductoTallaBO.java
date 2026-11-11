package bos;

import java.util.ArrayList;

import dominio.ProductoTalla;

import dtos.ProductoTallaDTO;
import interfaces.IProductoTallaBO;
import daos.ProductoTallaDAO;
import interfaces.IProductoTallaDAO;
import mappers.ProductoTallaMapper;
import java.util.List;

/** Reglas de inventario respaldadas por la persistencia de tallas. */
public class ProductoTallaBO implements IProductoTallaBO {

    private static ProductoTallaBO instance;
    private final IProductoTallaDAO productoTallaDAO;

    private ProductoTallaBO() {
        this(new ProductoTallaDAO());
    }

    public ProductoTallaBO(IProductoTallaDAO productoTallaDAO) {
        this.productoTallaDAO = java.util.Objects.requireNonNull(productoTallaDAO);
    }

    public static synchronized ProductoTallaBO getInstance() {
        if (instance == null) {
            instance = new ProductoTallaBO();
        }
        return instance;
    }

    @Override
    public boolean validarStockDisponible(int idProductoTalla, int cantidadRequerida) {
        validarEntrada(idProductoTalla, cantidadRequerida);
        ProductoTallaDTO talla = buscarTalla(idProductoTalla);
        if (talla.getStockActual() < cantidadRequerida) {
            throw new IllegalStateException("Stock insuficiente para la talla "
                    + talla.getTalla() + ". Disponible: " + talla.getStockActual()
                    + ", solicitado: " + cantidadRequerida + ".");
        }
        return true;
    }

    @Override
    public synchronized boolean descontarStock(int idProductoTalla, int cantidad) {
        validarEntrada(idProductoTalla, cantidad);
        ProductoTallaDTO talla = buscarTalla(idProductoTalla);
        if (talla.getStockActual() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para la talla "
                    + talla.getTalla() + ". Disponible: " + talla.getStockActual()
                    + ", solicitado: " + cantidad + ".");
        }
        talla.setStockActual(talla.getStockActual() - cantidad);
        actualizar(talla);
        return true;
    }

    @Override
    public synchronized boolean reabastecerStock(int idProductoTalla, int cantidad) {
        validarEntrada(idProductoTalla, cantidad);
        ProductoTallaDTO talla = buscarTalla(idProductoTalla);
        if (talla.getStockActual() > Integer.MAX_VALUE - cantidad) {
            throw new IllegalArgumentException(
                    "El reabastecimiento excede el stock máximo permitido.");
        }
        talla.setStockActual(talla.getStockActual() + cantidad);
        actualizar(talla);
        return true;
    }

    @Override
    public List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("El ID de producto debe ser mayor a cero.");
        }
        return convertirListaADTO(productoTallaDAO.obtenerPorProducto((long) idProducto));
    }

    private ProductoTallaDTO buscarTalla(int idProductoTalla) {
        ProductoTallaDTO talla = ProductoTallaMapper.toDTO(productoTallaDAO.buscarProductoTallaPorId((long) idProductoTalla));
        if (talla == null) {
            throw new IllegalArgumentException(
                    "No existe la talla de producto con ID " + idProductoTalla + ".");
        }
        return talla;
    }

    private void actualizar(ProductoTallaDTO talla) {
        try {
            productoTallaDAO.actualizar(ProductoTallaMapper.toEntity(talla));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo actualizar el inventario.", e);
        }
    }

    private static void validarEntrada(int idProductoTalla, int cantidad) {
        if (idProductoTalla <= 0 || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "El ID de talla y la cantidad deben ser mayores a cero.");
        }
    }

    private List<ProductoTallaDTO> convertirListaADTO(List<ProductoTalla> entidades) {
        List<ProductoTallaDTO> resultado = new ArrayList<>();
        if (entidades != null) {
            for (ProductoTalla entidad : entidades) {
                resultado.add(ProductoTallaMapper.toDTO(entidad));
            }
        }
        return resultado;
    }
}
