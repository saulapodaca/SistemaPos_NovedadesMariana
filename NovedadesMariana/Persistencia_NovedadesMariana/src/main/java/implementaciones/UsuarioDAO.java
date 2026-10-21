
package implementaciones;

import dominio.Usuario;
import entityManager.ConexionJPA;
import interfaces.IUsuarioDAO;
import jakarta.persistence.EntityManager;
import java.util.List;

public class UsuarioDAO implements IUsuarioDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(Usuario usuario) throws Exception {
        EntityManager em = conexion.getEntityManager();

        try {
            validarUsuario(usuario);

            if (usuario.getId() != null) {
                throw new Exception(
                    "Un usuario nuevo no debe tener ID."
                );
            }

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
            validarUsuario(usuario);

            if (usuario.getId() == null) {
                throw new Exception(
                    "El usuario debe tener un ID."
                );
            }

            em.getTransaction().begin();

            Usuario existente = em.find(
                Usuario.class, usuario.getId()
            );

            if (existente == null) {
                throw new Exception(
                    "El usuario no existe."
                );
            }

            // Actualizar datos personales
            existente.setUsuario(usuario.getUsuario());
            existente.setContrasenia(usuario.getContrasenia());
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
            if (id == null) {
                throw new Exception(
                    "Debe indicar el ID del usuario."
                );
            }

            em.getTransaction().begin();

            Usuario usuario = em.find(
                Usuario.class, id
            );

            if (usuario == null) {
                throw new Exception(
                    "El usuario no existe."
                );
            }

            // Verificar si tiene ventas registradas
            Long cantidadVentas = em.createQuery(
                "SELECT COUNT(v) FROM Venta v " +
                "WHERE v.usuario.id = :id",
                Long.class
            )
            .setParameter("id", id)
            .getSingleResult();

            if (cantidadVentas > 0) {
                throw new Exception(
                    "No se puede eliminar el usuario " +
                    "porque tiene ventas registradas."
                );
            }

            em.remove(usuario);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al eliminar usuario: "
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
            if (id == null) {
                return null;
            }

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
            if (nombreUsuario == null ||
                nombreUsuario.isBlank()) {
                return null;
            }

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

    // Validaciones basicas
    private void validarUsuario(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                "El usuario no puede ser nulo."
            );
        }

        if (usuario.getUsuario() == null ||
            usuario.getUsuario().isBlank() ||
            usuario.getUsuario().length() > 50) {
            throw new IllegalArgumentException(
                "El nombre de usuario no es valido."
            );
        }

        if (usuario.getContrasenia() == null ||
            usuario.getContrasenia().isBlank() ||
            usuario.getContrasenia().length() > 255) {
            throw new IllegalArgumentException(
                "La contrasena almacenada no es valida."
            );
        }

        if (usuario.getNombres() == null ||
            usuario.getNombres().isBlank() ||
            usuario.getNombres().length() > 50) {
            throw new IllegalArgumentException(
                "Los nombres no son validos."
            );
        }

        if (usuario.getPrimerApellido() == null ||
            usuario.getPrimerApellido().isBlank() ||
            usuario.getPrimerApellido().length() > 50) {
            throw new IllegalArgumentException(
                "El primer apellido no es valido."
            );
        }

        if (usuario.getSegundoApellido() != null &&
            usuario.getSegundoApellido().length() > 50) {
            throw new IllegalArgumentException(
                "El segundo apellido es demasiado largo."
            );
        }

        if (usuario.getRol() == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar un rol."
            );
        }
    }
}
