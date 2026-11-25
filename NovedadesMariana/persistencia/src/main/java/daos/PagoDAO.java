
package daos;

import dominio.Pago;
import dtos.PagoDTO;
import conexion.ConexionJPA;
import interfaces.IPagoDAO;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PagoDAO implements IPagoDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(Pago pago) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(pago);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al guardar pago: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Pago pago) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            Pago existente = em.find(
                Pago.class, pago.getId()
            );

            existente.setMetodoPago(pago.getMetodoPago());
            existente.setMonto(pago.getMonto());
            existente.setMontoRecibido(pago.getMontoRecibido());
            existente.setCambio(pago.getCambio());
            existente.setFechaHora(pago.getFechaHora());

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al actualizar pago: " + e.getMessage(), e
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

            Pago pago = em.find(Pago.class, id);

            em.remove(pago);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al eliminar pago: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public Pago buscarPorId(Long id) {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.find(Pago.class, id);

        } finally {
            em.close();
        }
    }

    @Override
    public Pago buscarPorVenta(Long idVenta) {
        EntityManager em = conexion.getEntityManager();
        try {
            return em.createQuery(
                "SELECT v.pago FROM Venta v " +
                "WHERE v.id = :idVenta AND v.pago IS NOT NULL",
                Pago.class
            )
            .setParameter("idVenta", idVenta)
            .getResultStream()
            .findFirst()
            .orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Pago> obtenerTodos() {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.createQuery(
                "SELECT p FROM Pago p",
                Pago.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public long contarVentas(Long idPago) {
        EntityManager em = conexion.getEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(v) FROM Venta v " +
                "WHERE v.pago.id = :idPago",
                Long.class
            )
            .setParameter("idPago", idPago)
            .getSingleResult();
        } finally {
            em.close();
        }
    }
}
