
package implementaciones;

import dominio.Producto;
import dominio.ProductoTalla;
import entityManager.ConexionJPA;
import interfaces.IProductoTallaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.List;

public class ProductoTallaDAO implements IProductoTallaDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(ProductoTalla productoTalla) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            validarProductoTalla(productoTalla);

            if (productoTalla.getId() != null) {
                throw new Exception(
                    "Una talla nueva no debe tener ID."
                );
            }

            if (productoTalla.getProducto() == null ||
                productoTalla.getProducto().getId() == null) {
                throw new Exception(
                    "Debe seleccionar un producto registrado."
                );
            }

            em.getTransaction().begin();

            Producto producto = em.find(
                Producto.class,
                productoTalla.getProducto().getId()
            );

            if (producto == null) {
                throw new Exception(
                    "El producto no existe."
                );
            }

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
        EntityManager em = conexion.getEntityManager();

        try {
            validarProductoTalla(productoTalla);

            if (productoTalla.getId() == null) {
                throw new Exception(
                    "La talla debe tener un ID."
                );
            }

            em.getTransaction().begin();

            ProductoTalla existente = em.find(
                ProductoTalla.class,
                productoTalla.getId(),
                LockModeType.PESSIMISTIC_WRITE
            );

            if (existente == null) {
                throw new Exception(
                    "La talla no existe."
                );
            }

            // No permitir cambiar la talla de producto
            if (productoTalla.getProducto() != null) {

                Long idProducto = productoTalla
                    .getProducto().getId();

                if (idProducto == null ||
                    !idProducto.equals(
                        existente.getProducto().getId()
                    )) {
                    throw new Exception(
                        "No se puede cambiar el producto asociado."
                    );
                }
            }

            existente.setTalla(productoTalla.getTalla());
            existente.setStockActual(
                productoTalla.getStockActual()
            );

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
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
        EntityManager em = conexion.getEntityManager();

        try {
            if (id == null) {
                throw new Exception(
                    "Debe indicar el ID de la talla."
                );
            }

            em.getTransaction().begin();

            ProductoTalla talla = em.find(
                ProductoTalla.class,
                id,
                LockModeType.PESSIMISTIC_WRITE
            );

            if (talla == null) {
                throw new Exception(
                    "La talla no existe."
                );
            }

            if (talla.getStockActual() > 0) {
                throw new Exception(
                    "No se puede eliminar una talla " +
                    "que tiene existencias disponibles."
                );
            }

            em.remove(talla);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al eliminar talla: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public ProductoTalla buscarPorId(Long id) {
        EntityManager em = conexion.getEntityManager();

        try {
            if (id == null) {
                return null;
            }

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

    // Validaciones basicas
    private void validarProductoTalla(ProductoTalla productoTalla) {

        if (productoTalla == null) {
            throw new IllegalArgumentException(
                "La talla no puede ser nula."
            );
        }

        if (productoTalla.getTalla() == null ||
            productoTalla.getTalla().isBlank() ||
            productoTalla.getTalla().length() > 30) {
            throw new IllegalArgumentException(
                "La talla debe contener entre 1 y 30 caracteres."
            );
        }

        if (productoTalla.getStockActual() == null ||
            productoTalla.getStockActual() < 0) {
            throw new IllegalArgumentException(
                "El stock no puede ser nulo ni negativo."
            );
        }
    }
}
