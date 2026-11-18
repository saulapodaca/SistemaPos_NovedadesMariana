package bos;

import daos.DetalleVentaDAO;
import daos.ProductoDAO;
import daos.VentaDAO;
import dominio.DetalleVenta;
import interfaces.IDetalleVentaDAO;
import interfaces.IProductoDAO;
import interfaces.IVentaDAO;

/** Valida las operaciones individuales sobre detalles de venta. */
public class DetalleVentaBO {

    private final IDetalleVentaDAO detalleDAO;
    private final IProductoDAO productoDAO;
    private final IVentaDAO ventaDAO;

    public DetalleVentaBO() {
        this(new DetalleVentaDAO(), new ProductoDAO(), new VentaDAO());
    }

    public DetalleVentaBO(IDetalleVentaDAO detalleDAO, IProductoDAO productoDAO, IVentaDAO ventaDAO) {
        this.detalleDAO = java.util.Objects.requireNonNull(detalleDAO);
        this.productoDAO = java.util.Objects.requireNonNull(productoDAO);
        this.ventaDAO = java.util.Objects.requireNonNull(ventaDAO);
    }

    public void guardar(DetalleVenta detalle) throws Exception {
        validarDetalle(detalle);
        if (detalle.getVenta() == null || detalle.getVenta().getId() == null
                || ventaDAO.buscarVentaPorId(detalle.getVenta().getId()) == null) {
            throw new IllegalArgumentException("Debe seleccionar una venta registrada.");
        }
        detalleDAO.guardar(detalle);
    }

    public void actualizar(DetalleVenta detalle) throws Exception {
        validarDetalle(detalle);
        validarExistencia(detalle.getId());
        detalleDAO.actualizar(detalle);
    }

    public void eliminar(Long id) throws Exception {
        validarExistencia(id);
        detalleDAO.eliminar(id);
    }

    private void validarExistencia(Long id) {
        if (id == null || id <= 0 || detalleDAO.buscarPorId(id) == null) {
            throw new IllegalArgumentException("El detalle de venta no existe.");
        }
    }

    private void validarDetalle(DetalleVenta detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo.");
        }
        if (detalle.getProducto() == null || detalle.getProducto().getId() == null
                || productoDAO.buscarProductoPorId(detalle.getProducto().getId()) == null) {
            throw new IllegalArgumentException("Debe seleccionar un producto registrado.");
        }
        if (detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (!Float.isFinite(detalle.getPrecioUnitario()) || detalle.getPrecioUnitario() < 0
                || !Float.isFinite(detalle.getSubtotal()) || detalle.getSubtotal() < 0) {
            throw new IllegalArgumentException("Los importes deben ser válidos y no negativos.");
        }
    }
}
