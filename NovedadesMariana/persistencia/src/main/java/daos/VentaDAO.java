
package daos;

import dominio.DetalleVenta;
import dominio.Pago;
import dominio.Producto;
import dominio.ProductoTalla;
import dominio.Usuario;
import dominio.Venta;
import conexion.ConexionJPA;
import interfaces.IVentaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;

public class VentaDAO implements IVentaDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(Venta venta) throws Exception {
        // Guarda la venta, pero no descuenta inventario.
        guardarConTallas(venta, Set.of(), tallas -> {});
    }

    @Override
    public void guardarConTallas(Venta venta, Set<Long> idsTalla,
            Consumer<Map<Long, ProductoTalla>> actualizarTallas) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            // Asociar usuario
            Usuario usuario = em.find(
                Usuario.class, venta.getUsuario().getId(), jakarta.persistence.LockModeType.PESSIMISTIC_READ
            );

            if (usuario == null || !usuario.isActivo()) {
                throw new IllegalStateException("Se requiere un empleado activo para registrar la venta.");
            }
            venta.setUsuario(usuario);

            // Asociar productos de la venta
            for (DetalleVenta detalle : venta.getDetalles()) {

                Producto producto = em.getReference(
                    Producto.class,
                    detalle.getProducto().getId()
                );

                detalle.setProducto(producto);
                detalle.setVenta(venta);
            }

            Map<Long, ProductoTalla> tallas = new TreeMap<>();
            for (Long id : new TreeSet<>(idsTalla)) {
                tallas.put(id, em.find(ProductoTalla.class, id, LockModeType.PESSIMISTIC_WRITE));
            }
            actualizarTallas.accept(tallas);

            // Guarda venta, detalles y pago por cascada
            em.persist(venta);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                throw e;
            }
            throw new Exception(
                "Error al registrar venta: "
                + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Venta venta) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void eliminar(Long id) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancelar(Long id) throws Exception {
        EntityManager em = conexion.getEntityManager();
        try {
            em.getTransaction().begin();
            Venta venta = em.find(Venta.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (venta == null) {
                em.getTransaction().rollback();
                return false;
            }

            // El detalle persistido solo referencia al producto; la talla se
            // recibe por DTO y se usa al vender, pero no queda asociada a la venta.
            if (venta.getPago() != null) {
                Pago pago = venta.getPago();
                em.remove(venta);
                em.flush();
                em.remove(pago);
            } else {
                em.remove(venta);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al cancelar la venta: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Venta buscarVentaPorId(Long id) {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.createQuery(
                "SELECT DISTINCT v FROM Venta v " +
                "JOIN FETCH v.usuario " +
                "JOIN FETCH v.pago " +
                "LEFT JOIN FETCH v.detalles d " +
                "LEFT JOIN FETCH d.producto " +
                "WHERE v.id = :id",
                Venta.class
            )
            .setParameter("id", id)
            .getResultStream()
            .findFirst()
            .orElse(null);

        } finally {
            em.close();
        }
    }

    @Override
    public Venta buscarPorFolio(String folio) {
        EntityManager em = conexion.getEntityManager();
        try {
            return em.createQuery(
                "SELECT DISTINCT v FROM Venta v " +
                "JOIN FETCH v.usuario " +
                "JOIN FETCH v.pago " +
                "LEFT JOIN FETCH v.detalles d " +
                "LEFT JOIN FETCH d.producto " +
                "WHERE LOWER(v.folio) = LOWER(:folio)",
                Venta.class
            )
            .setParameter("folio", folio.trim())
            .getResultStream()
            .findFirst()
            .orElse(null);
        } finally {
            em.close();
        }
    }

    public List<Venta> obtenerTodas() {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.createQuery(
                "SELECT DISTINCT v FROM Venta v " +
                "JOIN FETCH v.usuario " +
                "JOIN FETCH v.pago " +
                "LEFT JOIN FETCH v.detalles d " +
                "LEFT JOIN FETCH d.producto",
                Venta.class
            ).getResultList();

        } finally {
            em.close();
        }
    }





}
