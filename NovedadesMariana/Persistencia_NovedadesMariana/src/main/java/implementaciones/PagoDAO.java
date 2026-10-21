
package implementaciones;

import dominio.Pago;
import entityManager.ConexionJPA;
import interfaces.IPagoDAO;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PagoDAO implements IPagoDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(Pago pago) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            validarPago(pago);

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
            validarPago(pago);

            if (pago.getId() == null) {
                throw new Exception(
                    "El pago debe tener un ID."
                );
            }

            em.getTransaction().begin();

            Pago existente = em.find(
                Pago.class, pago.getId()
            );

            if (existente == null) {
                throw new Exception(
                    "El pago no existe."
                );
            }

            if (estaAsociadoAVenta(em, pago.getId())) {
                throw new Exception(
                    "No se puede modificar un pago " +
                    "asociado a una venta."
                );
            }

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

            if (pago == null) {
                throw new Exception(
                    "El pago no existe."
                );
            }

            if (estaAsociadoAVenta(em, id)) {
                throw new Exception(
                    "No se puede eliminar un pago " +
                    "asociado a una venta."
                );
            }

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


    private boolean estaAsociadoAVenta(
            EntityManager em, Long idPago) {

        Long cantidad = em.createQuery(
            "SELECT COUNT(v) FROM Venta v " +
            "WHERE v.pago.id = :idPago",
            Long.class
        )
        .setParameter("idPago", idPago)
        .getSingleResult();

        return cantidad > 0;
    }


    private void validarPago(Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException(
                "El pago no puede ser nulo."
            );
        }

        if (pago.getMetodoPago() == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar un metodo de pago."
            );
        }

        if (pago.getMonto() == null ||
            !Float.isFinite(pago.getMonto()) ||
            pago.getMonto() <= 0) {
            throw new IllegalArgumentException(
                "El monto debe ser mayor a cero."
            );
        }

        if (pago.getMontoRecibido() != null &&
            (!Float.isFinite(pago.getMontoRecibido()) ||
             pago.getMontoRecibido() < 0)) {
            throw new IllegalArgumentException(
                "El monto recibido no es valido."
            );
        }

        if (pago.getCambio() != null &&
            (!Float.isFinite(pago.getCambio()) ||
             pago.getCambio() < 0)) {
            throw new IllegalArgumentException(
                "El cambio no es valido."
            );
        }

        if (pago.getFechaHora() == null) {
            throw new IllegalArgumentException(
                "Debe especificar la fecha del pago."
            );
        }
    }
}
