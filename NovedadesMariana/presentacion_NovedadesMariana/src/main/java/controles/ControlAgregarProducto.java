/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controles;


import com.itson.negocios_gestorinventario.GestorInventario;
import com.itson.negocios_gestorinventario.IGestorInventario;
import dtos.CategoriaDTO;
import dtos.ProductoDTO;
import dtos.TipoPrendaDTO;
import dtos.UsuarioDTO;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import utilerias.GestorSesion;

/**
 *
 * @author Usuario
 */
public final class ControlAgregarProducto {

    private static ControlAgregarProducto instance;
    private IGestorInventario facade;
    private final GestorSesion sesion = GestorSesion.getInstance();

    private ControlAgregarProducto() {
    }

    public ControlAgregarProducto(IGestorInventario facade) {
        this.facade = Objects.requireNonNull(facade, "La fachada no puede ser nula.");
    }

    private synchronized IGestorInventario facade() {
        if (facade == null) {
            facade = new GestorInventario();
        }
        return facade;
    }

    public static synchronized ControlAgregarProducto getInstance() {
        if (instance == null) {
            instance = new ControlAgregarProducto();
        }
        return instance;
    }

    /**
     * Valida y registra un nuevo producto en el catálogo a través de la
     * fachada.
     *
     * @param producto DTO con los datos capturados en PanelRegistrarProducto.
     * @return ProductoDTO registrado (con su ID generado).
     */
    public ProductoDTO registrarProducto(ProductoDTO producto) {
        validarProducto(producto);

        ProductoDTO normalizado = new ProductoDTO(
                producto.getCodigoBarras().trim(),
                producto.getSku().trim().toUpperCase(Locale.ROOT),
                producto.getNombre().trim(),
                producto.getDescripcion().trim(),
                producto.getPrecio(),
                producto.getTipoPrenda(),
                Set.copyOf(producto.getCategoriasIds())
        );

        ProductoDTO registrado = facade().registrarProducto(normalizado);
        if (registrado == null || (registrado.getId() != null && registrado.getId() <= 0)) {
            throw new IllegalStateException("La fachada no devolvió un producto registrado válido.");
        }
        return registrado;
    }

    /**
     * Sobrecarga directa por si la vista envía los parámetros individuales.
     */
    public ProductoDTO registrarProducto(String codigoBarras, String sku, String nombre,
            String descripcion, float precio,
            TipoPrendaDTO tipoPrenda, Set<Long> categoriasIds) {
        ProductoDTO dto = new ProductoDTO(
                codigoBarras, sku, nombre, descripcion, precio, tipoPrenda, categoriasIds
        );
        return registrarProducto(dto);
    }

    /**
     * Obtiene la lista de categorías disponibles en el sistema para validación
     * o consulta.
     */
    public List<CategoriaDTO> obtenerCategorias() {
        List<CategoriaDTO> categorias = facade().obtenerCategorias();
        return categorias == null ? List.of() : categorias;
    }

    /**
     * Verifica si un código de barras ya está registrado en el catálogo.
     */
    public boolean existeCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            return false;
        }
        return facade().existeCodigoBarras(codigoBarras.trim());
    }

    /**
     * Verifica si un SKU ya está registrado en el catálogo.
     */
    public boolean existeSku(String sku) {
        if (sku == null || sku.isBlank()) {
            return false;
        }
        return facade().existeSku(sku.trim().toUpperCase(Locale.ROOT));
    }

    public UsuarioDTO getUsuario() {
        return sesion.getEmpleadoActual();
    }

    // ==================== VALIDACIONES DE NEGOCIO ====================
    private void validarProducto(ProductoDTO producto) {
        if (producto == null) {
            throw new IllegalArgumentException("Los datos del producto son obligatorios.");
        }
        if (producto.getCodigoBarras() == null || producto.getCodigoBarras().isBlank()) {
            throw new IllegalArgumentException("El código de barras es obligatorio.");
        }
        if (!producto.getCodigoBarras().trim().matches("^[A-Za-z0-9\\-]{4,50}$")) {
            throw new IllegalArgumentException("El código de barras debe tener entre 4 y 50 caracteres alfanuméricos.");
        }
        if (producto.getSku() == null || producto.getSku().isBlank()) {
            throw new IllegalArgumentException("El SKU es obligatorio.");
        }
        if (!producto.getSku().trim().matches("^[A-Za-z0-9\\-_]{3,30}$")) {
            throw new IllegalArgumentException("El SKU debe tener entre 3 y 30 caracteres válidos.");
        }
        if (producto.getNombre() == null || producto.getNombre().isBlank() || producto.getNombre().trim().length() < 3) {
            throw new IllegalArgumentException("El nombre del producto debe tener al menos 3 caracteres.");
        }
        if (producto.getDescripcion() == null || producto.getDescripcion().isBlank() || producto.getDescripcion().trim().length() < 5) {
            throw new IllegalArgumentException("La descripción del producto debe tener al menos 5 caracteres.");
        }
        if (!Float.isFinite(producto.getPrecio()) || producto.getPrecio() <= 0f) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor a $0.00.");
        }
        if (producto.getTipoPrenda() == null) {
            throw new IllegalArgumentException("Selecciona un tipo de prenda válido.");
        }
        if (producto.getCategoriasIds() == null || producto.getCategoriasIds().isEmpty()) {
            throw new IllegalArgumentException("Debes asignar al menos una categoría al producto.");
        }
        for (Long idCategoria : producto.getCategoriasIds()) {
            if (idCategoria == null || idCategoria <= 0) {
                throw new IllegalArgumentException("Todos los IDs de categoría deben ser números positivos.");
            }
        }
    }
}
