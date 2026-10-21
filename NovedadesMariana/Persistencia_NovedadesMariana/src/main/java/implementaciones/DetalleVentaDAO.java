
package implementaciones;

import dominio.DetalleVenta;
import dominio.Producto;
import dominio.Venta;
import entityManager.ConexionJPA;
import interfaces.IDetalleVentaDAO;
import jakarta.persistence.EntityManager;
import java.util.List;

public class DetalleVentaDAO implements IDetalleVentaDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(DetalleVenta detalle) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            validarDetalle(detalle);

            em.getTransaction().begin();

            if (detalle.getVenta() == null ||
                detalle.getVenta().getId() == null) {
                throw new Exception(
                    "La venta debe estar registrada."
                );
            }

            Venta venta = em.find(
                Venta.class, detalle.getVenta().getId()
            );

            Producto producto = em.find(
                Producto.class, detalle.getProducto().getId()
            );

            if (venta == null || producto == null) {
                throw new Exception(
                    "La venta o el producto no existe."
                );
            }

            detalle.setVenta(venta);
            detalle.setProducto(producto);

            em.persist(detalle);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al guardar detalle de venta: "
                + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(DetalleVenta detalle) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            validarDetalle(detalle);

            if (detalle.getId() == null) {
                throw new Exception(
                    "El detalle debe tener un ID."
                );
            }

            em.getTransaction().begin();

            DetalleVenta existente = em.find(
                DetalleVenta.class, detalle.getId()
            );

            if (existente == null) {
                throw new Exception(
                    "El detalle de venta no existe."
                );
            }

            Producto producto = em.find(
                Producto.class, detalle.getProducto().getId()
            );

            if (producto == null) {
                throw new Exception(
                    "El producto no existe."
                );
            }

            existente.setCantidad(detalle.getCantidad());
            existente.setPrecioUnitario(detalle.getPrecioUnitario());
            existente.setSubtotal(detalle.getSubtotal());
            existente.setProducto(producto);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al actualizar detalle de venta: "
                + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Long id) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            DetalleVenta detalle = em.find(
                DetalleVenta.class, id
            );

            if (detalle == null) {
                throw new Exception(
                    "El detalle de venta no existe."
                );
            }

            em.remove(detalle);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al eliminar detalle de venta: "
                + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public DetalleVenta buscarPorId(Long id) {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.find(DetalleVenta.class, id);

        } finally {
            em.close();
        }
    }

    @Override
    public List<DetalleVenta> obtenerTodos() {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.createQuery(
                "SELECT d FROM DetalleVenta d",
                DetalleVenta.class
            ).getResultList();

        } finally {
            em.close();
        }
    }


    private void validarDetalle(DetalleVenta detalle) {

        if (detalle == null) {
            throw new IllegalArgumentException(
                "El detalle no puede ser nulo."
            );
        }

        if (detalle.getProducto() == null ||
            detalle.getProducto().getId() == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar un producto registrado."
            );
        }

        if (detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException(
                "La cantidad debe ser mayor a cero."
            );
        }

        if (Float.isNaN(detalle.getPrecioUnitario()) ||
            Float.isInfinite(detalle.getPrecioUnitario()) ||
            detalle.getPrecioUnitario() < 0 ||
            Float.isNaN(detalle.getSubtotal()) ||
            Float.isInfinite(detalle.getSubtotal()) ||
            detalle.getSubtotal() < 0) {
            throw new IllegalArgumentException(
                "Los importes deben ser validos y no negativos."
            );
        }
    }
}
