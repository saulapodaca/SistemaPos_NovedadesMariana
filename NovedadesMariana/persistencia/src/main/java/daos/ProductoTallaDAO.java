
package daos;

import dominio.Producto;
import dominio.ProductoTalla;
import conexion.ConexionJPA;
import interfaces.IProductoTallaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.function.Consumer;

public class ProductoTallaDAO implements IProductoTallaDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(ProductoTalla productoTalla) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            Producto producto = em.getReference(
                Producto.class,
                productoTalla.getProducto().getId()
            );

            productoTalla.setProducto(producto);

            em.persist(productoTalla);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al guardar talla: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(ProductoTalla productoTalla) throws Exception {
        modificar(productoTalla.getId(), existente -> {
            existente.setTalla(productoTalla.getTalla());
            existente.setStockActual(productoTalla.getStockActual());
        });
    }

    @Override
    public void modificar(Long id, Consumer<ProductoTalla> cambios) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            ProductoTalla existente = em.find(
                ProductoTalla.class,
                id,
                LockModeType.PESSIMISTIC_WRITE
            );

            cambios.accept(existente);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                throw e;
            }
            throw new Exception(
                "Error al actualizar talla: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Long id) throws Exception {
        eliminar(id, talla -> {});
    }

    @Override
    public void eliminar(Long id, Consumer<ProductoTalla> antesDeEliminar) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            ProductoTalla talla = em.find(
                ProductoTalla.class,
                id,
                LockModeType.PESSIMISTIC_WRITE
            );

            antesDeEliminar.accept(talla);
            em.remove(talla);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                throw e;
            }
            throw new Exception(
                "Error al eliminar talla: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public ProductoTalla buscarProductoTallaPorId(Long id) {
        EntityManager em = conexion.getEntityManager();

        try {
            ProductoTalla talla = em.find(
                ProductoTalla.class, id
            );

            if (talla != null) {
                // Inicializar el producto asociado
                talla.getProducto().getNombre();
            }

            return talla;

        } finally {
            em.close();
        }
    }

    @Override
    public List<ProductoTalla> obtenerTodos() {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.createQuery(
                "SELECT pt FROM ProductoTalla pt " +
                "JOIN FETCH pt.producto",
                ProductoTalla.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<ProductoTalla> obtenerPorProducto(Long idProducto) {
        EntityManager em = conexion.getEntityManager();
        try {
            return em.createQuery(
                "SELECT pt FROM ProductoTalla pt " +
                "JOIN FETCH pt.producto p " +
                "WHERE p.id = :idProducto",
                ProductoTalla.class
            )
            .setParameter("idProducto", idProducto)
            .getResultList();
        } finally {
            em.close();
        }
    }

}
