
package implementaciones;

import dominio.Categoria;
import dominio.Producto;
import dominio.ProductoTalla;
import entityManager.ConexionJPA;
import interfaces.IProductoDAO;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductoDAO implements IProductoDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(Producto producto) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            validarProducto(producto);

            if (producto.getId() != null) {
                throw new Exception(
                    "Un producto nuevo no debe tener ID."
                );
            }

            em.getTransaction().begin();

            // Asociar categorias existentes
            Set<Categoria> categorias = cargarCategorias(
                em, producto.getCategorias()
            );

            producto.getCategorias().clear();
            producto.getCategorias().addAll(categorias);

            // Validar y asociar tallas
            for (ProductoTalla talla : producto.getTallas()) {

                if (talla == null ||
                    talla.getTalla() == null ||
                    talla.getTalla().isBlank()) {
                    throw new Exception(
                        "La talla no es valida."
                    );
                }

                if (talla.getStockActual() == null ||
                    talla.getStockActual() < 0) {
                    throw new Exception(
                        "El stock no puede ser negativo."
                    );
                }

                if (talla.getId() != null) {
                    throw new Exception(
                        "Una talla nueva no debe tener ID."
                    );
                }

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
            validarProducto(producto);

            if (producto.getId() == null) {
                throw new Exception(
                    "El producto debe tener un ID."
                );
            }

            em.getTransaction().begin();

            Producto existente = em.find(
                Producto.class, producto.getId()
            );

            if (existente == null) {
                throw new Exception(
                    "El producto no existe."
                );
            }

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
            if (id == null) {
                throw new Exception(
                    "Debe indicar el ID del producto."
                );
            }

            em.getTransaction().begin();

            Producto producto = em.find(
                Producto.class, id
            );

            if (producto == null) {
                throw new Exception(
                    "El producto no existe."
                );
            }

            // Comprobar si tiene ventas registradas
            Long cantidadDetalles = em.createQuery(
                "SELECT COUNT(d) FROM DetalleVenta d " +
                "WHERE d.producto.id = :id",
                Long.class
            )
            .setParameter("id", id)
            .getSingleResult();

            if (cantidadDetalles > 0) {
                throw new Exception(
                    "No se puede eliminar el producto " +
                    "porque tiene ventas registradas."
                );
            }

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
    public Producto buscarPorId(Long id) {
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
            Set<Categoria> categorias) throws Exception {

        Set<Categoria> resultado = new HashSet<>();

        if (categorias == null) {
            return resultado;
        }

        for (Categoria categoria : categorias) {

            if (categoria == null ||
                categoria.getId() == null) {
                throw new Exception(
                    "La categoria debe estar registrada."
                );
            }

            Categoria existente = em.find(
                Categoria.class, categoria.getId()
            );

            if (existente == null) {
                throw new Exception(
                    "No existe la categoria con ID: "
                    + categoria.getId()
                );
            }

            resultado.add(existente);
        }

        return resultado;
    }

    // Validaciones basicas del producto
    private void validarProducto(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                "El producto no puede ser nulo."
            );
        }

        if (producto.getSku() == null ||
            producto.getSku().isBlank()) {
            throw new IllegalArgumentException(
                "El SKU es obligatorio."
            );
        }

        if (producto.getNombre() == null ||
            producto.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                "El nombre es obligatorio."
            );
        }

        if (producto.getDescripcion() == null) {
            throw new IllegalArgumentException(
                "La descripcion es obligatoria."
            );
        }

        if (producto.getPrecio() == null ||
            !Float.isFinite(producto.getPrecio()) ||
            producto.getPrecio() < 0) {
            throw new IllegalArgumentException(
                "El precio no es valido."
            );
        }

        if (producto.getTipoPrenda() == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar un tipo de prenda."
            );
        }
    }
}
