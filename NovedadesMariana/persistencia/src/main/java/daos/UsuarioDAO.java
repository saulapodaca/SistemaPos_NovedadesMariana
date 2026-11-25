
package daos;

import dominio.Usuario;
import dtos.UsuarioDTO;
import conexion.ConexionJPA;
import interfaces.IUsuarioDAO;
import jakarta.persistence.EntityManager;
import java.util.List;

public class UsuarioDAO implements IUsuarioDAO{

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(Usuario usuario) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(usuario);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al guardar usuario: "
                + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public void actualizar(Usuario usuario) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            Usuario existente = em.find(
                Usuario.class, usuario.getId()
            );

            // Actualizar datos personales
            existente.setUsuario(usuario.getUsuario());
            if (usuario.getContrasenia() != null) {
                existente.setContrasenia(usuario.getContrasenia());
            }
            existente.setNombres(usuario.getNombres());
            existente.setPrimerApellido(
                usuario.getPrimerApellido()
            );
            existente.setSegundoApellido(
                usuario.getSegundoApellido()
            );
            existente.setRol(usuario.getRol());

            // Las ventas asociadas no se modifican

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al actualizar usuario: "
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

            Usuario usuario = em.find(
                Usuario.class, id
            );

            if (usuario == null) throw new IllegalArgumentException("El usuario no existe.");
            usuario.setActivo(false);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al desactivar usuario: "
                + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    @Override
    public Usuario buscarPorId(Long id) {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.find(Usuario.class, id);

        } finally {
            em.close();
        }
    }

    @Override
    public List<Usuario> obtenerTodos() {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.createQuery(
                "SELECT u FROM Usuario u",
                Usuario.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // Buscar usuario para iniciar sesion
    public Usuario buscarPorUsuario(String nombreUsuario) {
        EntityManager em = conexion.getEntityManager();

        try {
            return em.createQuery(
                "SELECT u FROM Usuario u " +
                "WHERE u.usuario = :usuario",
                Usuario.class
            )
            .setParameter("usuario", nombreUsuario)
            .getResultStream()
            .findFirst()
            .orElse(null);

        } finally {
            em.close();
        }
    }


    @Override
    public long contarVentas(Long id) {
        EntityManager em = conexion.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(v) FROM Venta v " +
                    "WHERE v.usuario.id = :id",
                    Long.class
                )
                .setParameter("id", id)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
}
