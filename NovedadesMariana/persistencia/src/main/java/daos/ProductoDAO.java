
package daos;

import dominio.Categoria;
import dominio.Producto;
import dominio.ProductoTalla;
import conexion.ConexionJPA;
import interfaces.IProductoDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductoDAO implements IProductoDAO{

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(Producto producto) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            // Asociar categorias existentes
            Set<Categoria> categorias = cargarCategorias(
                em, producto.getCategorias()
            );

            producto.getCategorias().clear();
            producto.getCategorias().addAll(categorias);

            // Asociar tallas
            for (ProductoTalla talla : producto.getTallas()) {

                talla.setProducto(producto);
            }

            em.persist(producto);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al guardar producto: "
                + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Producto producto) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            Producto existente = em.find(
                Producto.class, producto.getId()
            );

            // Actualizar datos generales
            existente.setSku(producto.getSku());
            existente.setNombre(producto.getNombre());
            existente.setDescripcion(producto.getDescripcion());
            existente.setPrecio(producto.getPrecio());
            existente.setTipoPrenda(producto.getTipoPrenda());

            // Actualizar categorias asociadas
            Set<Categoria> categorias = cargarCategorias(
                em, producto.getCategorias()
            );

            existente.getCategorias().clear();
            existente.getCategorias().addAll(categorias);

            // Las tallas se administran mediante
            // ProductoTallaDAO para evitar cambios
            // accidentales en el inventario.

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al actualizar producto: "
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

            Producto producto = em.find(
                Producto.class, id
            );

            // Eliminar asociaciones con categorias
            producto.getCategorias().clear();

            // Las tallas se eliminan en cascada
            em.remove(producto);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al eliminar producto: "
                + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public Producto buscarProductoPorId(Long id) {
        EntityManager em = conexion.getEntityManager();

        try {
            Producto producto = em.find(
                Producto.class, id
            );

            if (producto != null) {
                // Cargar relaciones antes de cerrar
                // el EntityManager
                producto.getCategorias().size();
                producto.getTallas().size();
            }

            return producto;

        } finally {
            em.close();
        }
    }

    @Override
    public List<Producto> obtenerTodos() {
        EntityManager em = conexion.getEntityManager();

        try {
            List<Producto> productos = em.createQuery(
                "SELECT DISTINCT p FROM Producto p " +
                "LEFT JOIN FETCH p.categorias",
                Producto.class
            ).getResultList();

            // Inicializar tallas
            for (Producto producto : productos) {
                producto.getTallas().size();
            }

            return productos;

        } finally {
            em.close();
        }
    }

    // Recuperar categorias existentes
    private Set<Categoria> cargarCategorias(
            EntityManager em,
            Set<Categoria> categorias) {

        Set<Categoria> resultado = new HashSet<>();

        if (categorias == null) {
            return resultado;
        }

        for (Categoria categoria : categorias) {

            Categoria existente = em.getReference(
                Categoria.class, categoria.getId()
            );

            resultado.add(existente);
        }

        return resultado;
    }

    
    @Override
    public Producto buscarPorSku(String sku) {
        EntityManager em = conexion.getEntityManager();
        try {
            Producto producto = em.createQuery(
                "SELECT DISTINCT p FROM Producto p " +
                "LEFT JOIN FETCH p.categorias " +
                "WHERE LOWER(p.sku) = LOWER(:sku)",
                Producto.class
            )
            .setParameter("sku", sku.trim())
            .getSingleResult();

            // Cargar colección de tallas si aplica
            if (producto.getTallas() != null) {
                producto.getTallas().size();
            }

            return producto;
        } catch (NoResultException e) {
            return null; // Retorna null de forma segura si no existe el SKU
        } finally {
            em.close();
        }
    }

    @Override
    public Producto buscarPorCodigoBarras(String codigoBarras) {
        EntityManager em = conexion.getEntityManager();
        try {
            return em.createQuery(
                "SELECT DISTINCT p FROM Producto p " +
                "LEFT JOIN FETCH p.categorias " +
                "WHERE LOWER(p.codigoBarras) = LOWER(:codigo)",
                Producto.class
            )
            .setParameter("codigo", codigoBarras.trim())
            .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Producto> listarProductosPorCategoria(Long idCategoria) {
        EntityManager em = conexion.getEntityManager();
        try {
            List<Producto> productos = em.createQuery(
                "SELECT DISTINCT p FROM Producto p " +
                "JOIN p.categorias c " +
                "LEFT JOIN FETCH p.categorias " +
                "WHERE c.id = :idCategoria",
                Producto.class
            )
            .setParameter("idCategoria", idCategoria)
            .getResultList();

            for (Producto producto : productos) {
                if (producto.getTallas() != null) {
                    producto.getTallas().size();
                }
            }

            return productos;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Producto> buscarProductos(String criterio) {
        EntityManager em = conexion.getEntityManager();
        try {
            String patron = "%" + criterio.trim().toLowerCase() + "%";

            List<Producto> productos = em.createQuery(
                "SELECT DISTINCT p FROM Producto p " +
                "LEFT JOIN FETCH p.categorias " +
                "WHERE LOWER(p.nombre) LIKE :patron " +
                "OR LOWER(p.sku) LIKE :patron " +
                "OR LOWER(p.codigoBarras) LIKE :patron",
                Producto.class
            )
            .setParameter("patron", patron)
            .getResultList();

            for (Producto producto : productos) {
                if (producto.getTallas() != null) {
                    producto.getTallas().size();
                }
            }

            return productos;
        } finally {
            em.close();
        }
    }

    @Override
    public long contarDetallesVenta(Long id) {
        EntityManager em = conexion.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(d) FROM DetalleVenta d " +
                    "WHERE d.producto.id = :id",
                    Long.class
                )
                .setParameter("id", id)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}
