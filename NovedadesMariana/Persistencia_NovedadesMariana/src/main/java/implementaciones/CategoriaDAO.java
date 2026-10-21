
package implementaciones;

import dominio.Categoria;
import entityManager.ConexionJPA;
import interfaces.ICategoriaDAO;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CategoriaDAO implements ICategoriaDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(Categoria categoria) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(categoria);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al guardar categoría: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Categoria categoria) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            Categoria existente = em.find(
                Categoria.class, categoria.getId()
            );

            if (existente == null) {
                throw new Exception(
                    "La categoría no existe."
                );
            }

            existente.setNombre(categoria.getNombre());
            existente.setDescripcion(categoria.getDescripcion());

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al actualizar categoría: " + e.getMessage(), e
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

            Categoria categoria = em.find(Categoria.class, id);

            if (categoria == null) {
                throw new Exception(
                    "La categoría no existe."
                );
            }

            Long cantidadProductos = em.createQuery(
                "SELECT COUNT(p) FROM Producto p " +
                "JOIN p.categorias c " +
                "WHERE c.id = :id",
                Long.class
            )
            .setParameter("id", id)
            .getSingleResult();

            if (cantidadProductos > 0) {
                throw new Exception(
                    "No se puede eliminar la categoría " +
                    "porque tiene productos asociados."
                );
            }

            em.remove(categoria);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al eliminar categoría: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public Categoria buscarPorId(Long id) {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.find(Categoria.class, id);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Categoria> obtenerTodas() {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.createQuery(
                "SELECT c FROM Categoria c",
                Categoria.class
            ).getResultList();

        } finally {
            em.close();
        }
    }
}
