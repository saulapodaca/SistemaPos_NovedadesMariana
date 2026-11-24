package bos;

import java.util.ArrayList;

import dominio.ProductoTalla;

import dtos.ProductoTallaDTO;
import interfaces.IProductoTallaBO;
import daos.ProductoTallaDAO;
import interfaces.IProductoTallaDAO;
import mappers.ProductoTallaMapper;
import java.util.List;
import java.util.function.Consumer;

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
        modificarStock(idProductoTalla, talla -> descontarStock(talla, cantidad));
        return true;
    }

    @Override
    public synchronized boolean reabastecerStock(int idProductoTalla, int cantidad) {
        validarEntrada(idProductoTalla, cantidad);
        modificarStock(idProductoTalla, talla -> {
            if (talla.getStockActual() > Integer.MAX_VALUE - cantidad) {
                throw new IllegalArgumentException("El reabastecimiento excede el stock máximo permitido.");
            }
            talla.setStockActual(talla.getStockActual() + cantidad);
        });
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

    public void guardar(ProductoTalla talla) throws Exception {
        validarTalla(talla);
        if (talla.getId() != null) {
            throw new IllegalArgumentException("Una talla nueva no debe tener ID.");
        }
        if (talla.getProducto() == null || talla.getProducto().getId() == null
                || ProductoBO.getInstance().obtenerPorId(talla.getProducto().getId()) == null) {
            throw new IllegalArgumentException("Debe seleccionar un producto registrado.");
        }
        productoTallaDAO.guardar(talla);
    }

    public void actualizar(ProductoTalla talla) throws Exception {
        validarTalla(talla);
        validarId(talla.getId());
        productoTallaDAO.modificar(talla.getId(), existente -> {
            validarExistencia(existente);
            if (talla.getProducto() != null
                    && !existente.getProducto().getId().equals(talla.getProducto().getId())) {
                throw new IllegalArgumentException("No se puede cambiar el producto asociado.");
            }
            existente.setTalla(talla.getTalla());
            existente.setStockActual(talla.getStockActual());
        });
    }

    public void eliminar(Long id) throws Exception {
        validarId(id);
        productoTallaDAO.eliminar(id, talla -> {
            validarExistencia(talla);
            if (talla.getStockActual() > 0) {
                throw new IllegalStateException("No se puede eliminar una talla que tiene existencias disponibles.");
            }
        });
    }

    private static void validarTalla(ProductoTalla talla) {
        if (talla == null || talla.getTalla() == null || talla.getTalla().isBlank()
                || talla.getTalla().length() > 30) {
            throw new IllegalArgumentException("La talla debe contener entre 1 y 30 caracteres.");
        }
        if (talla.getStockActual() == null || talla.getStockActual() < 0) {
            throw new IllegalArgumentException("El stock no puede ser nulo ni negativo.");
        }
    }

    private static void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Debe indicar un ID de talla válido.");
        }
    }

    private static void validarExistencia(ProductoTalla talla) {
        if (talla == null) {
            throw new IllegalArgumentException("La talla no existe.");
        }
    }

    static void descontarStock(ProductoTalla talla, int cantidad) {
        if (talla.getStockActual() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para la talla "
                    + talla.getTalla() + ". Disponible: " + talla.getStockActual()
                    + ", solicitado: " + cantidad + ".");
        }
        talla.setStockActual(talla.getStockActual() - cantidad);
    }

    private void modificarStock(long id, Consumer<ProductoTalla> cambios) {
        try {
            productoTallaDAO.modificar(id, talla -> {
                validarExistencia(talla);
                cambios.accept(talla);
            });
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
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
