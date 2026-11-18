
package daos;

import dominio.DetalleVenta;
import dominio.Producto;
import dominio.Venta;
import conexion.ConexionJPA;
import interfaces.IDetalleVentaDAO;
import jakarta.persistence.EntityManager;
import java.util.List;

public class DetalleVentaDAO implements IDetalleVentaDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(DetalleVenta detalle) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            Venta venta = em.getReference(
                Venta.class, detalle.getVenta().getId()
            );

            Producto producto = em.getReference(
                Producto.class, detalle.getProducto().getId()
            );

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
            em.getTransaction().begin();

            DetalleVenta existente = em.find(
                DetalleVenta.class, detalle.getId()
            );

            Producto producto = em.getReference(
                Producto.class, detalle.getProducto().getId()
            );

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


}
