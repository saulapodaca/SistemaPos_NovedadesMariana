package fachada;

import dominio.Categoria;
import dominio.Pago;
import dominio.Producto;
import dominio.ProductoTalla;
import dominio.Usuario;
import dominio.Venta;
import implementaciones.CategoriaDAO;
import implementaciones.PagoDAO;
import implementaciones.ProductoDAO;
import implementaciones.ProductoTallaDAO;
import implementaciones.UsuarioDAO;
import implementaciones.VentaDAO;
import interfaces.ICategoriaDAO;
import interfaces.IFachadaPersistencia;
import interfaces.IPagoDAO;
import interfaces.IProductoDAO;
import interfaces.IProductoTallaDAO;
import interfaces.IUsuarioDAO;
import interfaces.IVentaDAO;

public class FachadaPersistencia implements IFachadaPersistencia {

    private static final FachadaPersistencia INSTANCE = new FachadaPersistencia();

    public static FachadaPersistencia getInstance() { return INSTANCE; }

    // Instancias de los DAOs
    private final IProductoDAO productoDAO;
    private final ICategoriaDAO categoriaDAO;
    private final IProductoTallaDAO productoTallaDAO;
    private final IVentaDAO ventaDAO;
    private final IPagoDAO pagoDAO;
    private final IUsuarioDAO usuarioDAO;

    public FachadaPersistencia() {
        // Inicializamos las implementaciones concretas de los DAOs
        this.productoDAO = new ProductoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.productoTallaDAO = new ProductoTallaDAO();
        this.ventaDAO = new VentaDAO();
        this.pagoDAO = new PagoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override public void guardarProducto(Producto p) throws Exception { productoDAO.guardar(p); }
    @Override public void actualizarProducto(Producto p) throws Exception { productoDAO.actualizar(p); }
    @Override public void eliminarProducto(Long id) throws Exception { productoDAO.eliminar(id); }
    @Override public java.util.List<Producto> obtenerProductos() { return productoDAO.obtenerTodos(); }
    @Override public java.util.List<Producto> listarProductosPorCategoria(Long id) { return productoDAO.listarProductosPorCategoria(id); }
    @Override public java.util.List<Producto> buscarProductos(String criterio) { return productoDAO.buscarProductos(criterio); }
    @Override public void guardarCategoria(Categoria c) throws Exception { categoriaDAO.guardar(c); }
    @Override public void actualizarCategoria(Categoria c) throws Exception { categoriaDAO.actualizar(c); }
    @Override public void eliminarCategoria(Long id) throws Exception { categoriaDAO.eliminar(id); }
    @Override public java.util.List<Categoria> obtenerCategorias() { return categoriaDAO.obtenerTodas(); }
    @Override public void guardarProductoTalla(ProductoTalla t) throws Exception { productoTallaDAO.guardar(t); }
    @Override public java.util.List<ProductoTalla> obtenerTallasPorProducto(Long id) { return productoTallaDAO.obtenerPorProducto(id); }
    @Override public void guardarUsuario(Usuario u) throws Exception { usuarioDAO.guardar(u); }
    @Override public void actualizarUsuario(Usuario u) throws Exception { usuarioDAO.actualizar(u); }
    @Override public void eliminarUsuario(Long id) throws Exception { usuarioDAO.eliminar(id); }
    @Override public Usuario buscarUsuarioPorId(Long id) { return usuarioDAO.buscarPorId(id); }
    @Override public java.util.List<Usuario> obtenerUsuarios() { return usuarioDAO.obtenerTodos(); }
    @Override public Usuario buscarUsuarioPorNombre(String usuario) { return usuarioDAO.buscarPorUsuario(usuario); }
    @Override public Venta buscarVentaPorFolio(String folio) { return ventaDAO.buscarPorFolio(folio); }

    // --- Producto y Categoria ---
    @Override
    public Producto buscarProductoPorCodigoBarras(String codigo) {
        return productoDAO.buscarPorCodigoBarras(codigo);
    }

    @Override
    public Producto buscarProductoPorSku(String sku) {
        return productoDAO.buscarPorSku(sku);
    }

    @Override
    public Producto buscarProductoPorId(Long id) {
        return productoDAO.buscarProductoPorId(id);
    }

    @Override
    public Categoria buscarCategoriaPorId(Long id) {
        return categoriaDAO.buscarCategoriaPorId(id);
    }

    // --- ProductoTalla ---
    @Override
    public ProductoTalla buscarProductoTallaPorId(Long id) {
        return productoTallaDAO.buscarProductoTallaPorId(id);
    }

    @Override
    public void actualizarProductoTalla(ProductoTalla talla) throws Exception {
        productoTallaDAO.actualizar(talla);
    }


    @Override
    public void registrarVenta(Venta venta) throws Exception {
        ventaDAO.guardar(venta);
    }

    @Override
    public void registrarVentaConTallas(Venta venta, java.util.Map<dominio.DetalleVenta, Long> tallasPorDetalle) throws Exception {
        ventaDAO.guardarConTallas(venta, tallasPorDetalle);
    }

    @Override
    public boolean cancelarVenta(Long idVenta) throws Exception {
        // Dependiendo de tu lógica en el DAO, podría ser un método eliminar o cambiar un estado
        return ventaDAO.cancelar(idVenta);
    }

    @Override
    public Venta buscarVentaPorId(Long idVenta) {
        return ventaDAO.buscarVentaPorId(idVenta);
    }

    // --- Pago ---
    @Override
    public Pago buscarPagoPorVenta(Long idVenta) {
        return pagoDAO.buscarPorVenta(idVenta);
    }

    // --- Usuario ---
    @Override
    public Usuario buscarUsuarioParaAutenticacion(String usuario) {
        return usuarioDAO.buscarPorUsuario(usuario);
    }
}
