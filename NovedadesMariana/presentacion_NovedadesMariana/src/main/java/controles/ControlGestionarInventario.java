package controles;

import com.itson.negocios_gestorinventario.GestorInventario;
import com.itson.negocios_gestorinventario.IGestorInventario;
import dtos.CategoriaDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controlador de presentación para el caso de uso Gestionar Inventario.
 * Coordina las consultas, filtros y modificaciones de stock y productos del punto de venta.
 */
public final class ControlGestionarInventario {

    private static ControlGestionarInventario instance;
    private final IGestorInventario gestorInventario;

    public ControlGestionarInventario() {
        this(new GestorInventario());
    }

    public ControlGestionarInventario(IGestorInventario gestorInventario) {
        this.gestorInventario = Objects.requireNonNull(gestorInventario, "El gestor de inventario es requerido.");
    }

    public static synchronized ControlGestionarInventario getInstance() {
        if (instance == null) {
            instance = new ControlGestionarInventario();
        }
        return instance;
    }

    /**
     * Obtiene el listado completo de productos registrados.
     */
    public List<ProductoDTO> cargarInventario() {
        List<ProductoDTO> productos = gestorInventario.obtenerTodosLosProductos();
        return productos == null ? new ArrayList<>() : productos;
    }

    /**
     * Busca productos por código, SKU o nombre.
     */
    public List<ProductoDTO> buscarProductos(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return cargarInventario();
        }
        List<? extends interfaces.IProductoDTO> encontrados = gestorInventario.buscarProductos(criterio.trim());
        List<ProductoDTO> resultado = new ArrayList<>();
        if (encontrados != null) {
            for (interfaces.IProductoDTO p : encontrados) {
                if (p instanceof ProductoDTO dto) {
                    resultado.add(dto);
                }
            }
        }
        return resultado;
    }

    /**
     * Filtra productos pertenecientes a una categoría específica por su nombre.
     */
    public List<ProductoDTO> buscarPorCategoria(String nombreCategoria) {
        if (nombreCategoria == null || nombreCategoria.isBlank() || "Todas".equalsIgnoreCase(nombreCategoria.trim())) {
            return cargarInventario();
        }
        List<? extends interfaces.IProductoDTO> encontrados = gestorInventario.buscarProductosPorCategoria(nombreCategoria.trim());
        List<ProductoDTO> resultado = new ArrayList<>();
        if (encontrados != null) {
            for (interfaces.IProductoDTO p : encontrados) {
                if (p instanceof ProductoDTO dto) {
                    resultado.add(dto);
                }
            }
        }
        return resultado;
    }

    /**
     * Consulta las tallas y existencias de un producto.
     */
    public List<ProductoTallaDTO> obtenerTallas(int idProducto) {
        List<? extends interfaces.IProductoTallaDTO> tallas = gestorInventario.obtenerTallasPorProducto(idProducto);
        List<ProductoTallaDTO> resultado = new ArrayList<>();
        if (tallas != null) {
            for (interfaces.IProductoTallaDTO t : tallas) {
                if (t instanceof ProductoTallaDTO dto) {
                    resultado.add(dto);
                }
            }
        }
        return resultado;
    }

    /**
     * Calcula la suma de existencias de todas las tallas de un producto.
     */
    public int calcularStockTotal(int idProducto) {
        List<ProductoTallaDTO> tallas = obtenerTallas(idProducto);
        int total = 0;
        for (ProductoTallaDTO t : tallas) {
            total += t.getStockActual();
        }
        return total;
    }

    /**
     * Incrementa las existencias de una talla por entrada de mercancía.
     */
    public boolean reabastecerStock(int idProductoTalla, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reabastecer debe ser mayor a cero.");
        }
        return gestorInventario.reabastecerStock(idProductoTalla, cantidad);
    }

    /**
     * Ajusta el stock a un conteo físico exacto.
     */
    public boolean ajustarStock(int idProductoTalla, int nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser un valor negativo.");
        }
        return gestorInventario.ajustarStock(idProductoTalla, nuevoStock);
    }
    
    public void guardarProducto(ProductoDTO producto, List<ProductoTallaDTO> tallasIniciales) throws Exception {
        Objects.requireNonNull(producto, "El producto a registrar es obligatorio.");
        gestorInventario.guardarProducto(producto);
        if (tallasIniciales != null && producto.getId() != null) {
            for (ProductoTallaDTO t : tallasIniciales) {
                t.setIdProducto(producto.getId().intValue());
                gestorInventario.agregarTalla(t);
            }
        }
    }
    
    /**
     * Actualiza la información básica de un producto existente.
     */
    public void actualizarProducto(ProductoDTO producto) throws Exception {
        Objects.requireNonNull(producto, "El producto a actualizar es obligatorio.");
        gestorInventario.actualizarProducto(producto);
    }

    /**
     * Elimina un producto si no cuenta con ventas registradas.
     */
    public void eliminarProducto(Long idProducto) throws Exception {
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("ID de producto inválido.");
        }
        gestorInventario.eliminarProducto(idProducto);
    }

    /**
     * Agrega una nueva talla con stock inicial a un producto existente.
     */
    public void agregarTalla(int idProducto, String nombreTalla, int stockInicial) throws Exception {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser válido.");
        }
        if (nombreTalla == null || nombreTalla.isBlank()) {
            throw new IllegalArgumentException("La talla no puede estar vacía.");
        }
        if (stockInicial < 0) {
            throw new IllegalArgumentException("El stock inicial no puede ser negativo.");
        }
        ProductoTallaDTO nuevaTalla = new ProductoTallaDTO(0, idProducto, nombreTalla.trim().toUpperCase(), stockInicial);
        gestorInventario.agregarTalla(nuevaTalla);
    }

    /**
     * Elimina una talla de producto si su stock es 0 y no tiene ventas.
     */
    public void eliminarTalla(int idProductoTalla) throws Exception {
        gestorInventario.eliminarTalla(idProductoTalla);
    }

    /**
     * Obtiene el catálogo de categorías registradas en el sistema.
     */
    public List<CategoriaDTO> obtenerCategorias() {
        List<CategoriaDTO> categorias = gestorInventario.obtenerCategorias();
        return categorias == null ? new ArrayList<>() : categorias;
    }
}
