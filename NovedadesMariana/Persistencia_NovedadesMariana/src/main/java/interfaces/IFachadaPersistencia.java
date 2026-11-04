package interfaces;

import dominio.Categoria;
import dominio.Pago;
import dominio.Producto;
import dominio.ProductoTalla;
import dominio.Usuario;
import dominio.Venta;
import java.util.List;
import java.util.Map;
import dominio.DetalleVenta;

public interface IFachadaPersistencia {

    void guardarProducto(Producto producto) throws Exception;
    void actualizarProducto(Producto producto) throws Exception;
    void eliminarProducto(Long id) throws Exception;
    List<Producto> obtenerProductos();
    List<Producto> listarProductosPorCategoria(Long idCategoria);
    List<Producto> buscarProductos(String criterio);
    void guardarCategoria(Categoria categoria) throws Exception;
    void actualizarCategoria(Categoria categoria) throws Exception;
    void eliminarCategoria(Long id) throws Exception;
    java.util.List<Categoria> obtenerCategorias();


    // --- Producto y Categoria ---
    // Encuentra elementos existentes y devuelve null si el ID o código no existe.
    Producto buscarProductoPorCodigoBarras(String codigo);
    Producto buscarProductoPorSku(String sku);
    Producto buscarProductoPorId(Long id);
    Categoria buscarCategoriaPorId(Long id);

    // --- ProductoTalla ---
    // Consultar talla, validar stock y descontarlo/reabastecerlo.
    ProductoTalla buscarProductoTallaPorId(Long id);
    void actualizarProductoTalla(ProductoTalla talla) throws Exception;
    void guardarProductoTalla(ProductoTalla talla) throws Exception;
    List<ProductoTalla> obtenerTallasPorProducto(Long idProducto);
    void guardarUsuario(Usuario usuario) throws Exception;
    void actualizarUsuario(Usuario usuario) throws Exception;
    void eliminarUsuario(Long id) throws Exception;
    Usuario buscarUsuarioPorId(Long id);
    List<Usuario> obtenerUsuarios();
    Usuario buscarUsuarioPorNombre(String usuario);
    Venta buscarVentaPorFolio(String folio);

    // --- Venta ---
    // Registrar una venta válida, asigna ID, folio y fecha.
    void registrarVenta(Venta venta) throws Exception;
    void registrarVentaConTallas(Venta venta, Map<DetalleVenta, Long> tallasPorDetalle) throws Exception;
    // Elimina una venta existente y devuelve false si el ID no existe.
    boolean cancelarVenta(Long idVenta) throws Exception;
    Venta buscarVentaPorId(Long idVenta);

    // --- Pago ---
    // Registra un pago válido (asociado a la venta en cascada).
    Pago buscarPagoPorVenta(Long idVenta);

    // --- Usuario ---
    // Autentica las credenciales de prueba devolviendo la entidad con contraseña.
    Usuario buscarUsuarioParaAutenticacion(String usuario);
}
