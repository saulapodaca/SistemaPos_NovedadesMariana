
package implementaciones;

import dominio.DetalleVenta;
import dominio.Pago;
import dominio.Producto;
import dominio.ProductoTalla;
import dominio.Usuario;
import dominio.Venta;
import entityManager.ConexionJPA;
import interfaces.IVentaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VentaDAO implements IVentaDAO {

    private final ConexionJPA conexion = ConexionJPA.getInstance();

    @Override
    public void guardar(Venta venta) throws Exception {
        // Guarda la venta, pero no descuenta inventario.
        registrarVenta(venta, null);
    }

    // Registrar venta con descuento de inventario
    public void guardarConTallas(
            Venta venta,
            Map<DetalleVenta, Long> tallasPorDetalle)
            throws Exception {

        if (tallasPorDetalle == null) {
            throw new IllegalArgumentException(
                "Debe indicar las tallas vendidas."
            );
        }

        registrarVenta(venta, tallasPorDetalle);
    }

    private void registrarVenta(
            Venta venta,
            Map<DetalleVenta, Long> tallasPorDetalle)
            throws Exception {

        EntityManager em = conexion.getEntityManager();

        try {
            validarVenta(venta);

            em.getTransaction().begin();

            // Verificar usuario
            Usuario usuario = em.find(
                Usuario.class, venta.getUsuario().getId()
            );

            if (usuario == null) {
                throw new Exception(
                    "El usuario no existe."
                );
            }

            venta.setUsuario(usuario);

            // Verificar productos de la venta
            for (DetalleVenta detalle : venta.getDetalles()) {

                Producto producto = em.find(
                    Producto.class,
                    detalle.getProducto().getId()
                );

                if (producto == null) {
                    throw new Exception(
                        "Uno de los productos no existe."
                    );
                }

                detalle.setProducto(producto);
                detalle.setVenta(venta);
            }

            // Descontar existencias cuando se indiquen
            // las tallas correspondientes.
            if (tallasPorDetalle != null) {
                descontarInventario(
                    em, venta, tallasPorDetalle
                );
            }

            // Guarda venta, detalles y pago por cascada
            em.persist(venta);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception(
                "Error al registrar venta: "
                + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    // Descontar inventario dentro de la transaccion
    private void descontarInventario(
            EntityManager em,
            Venta venta,
            Map<DetalleVenta, Long> tallasPorDetalle)
            throws Exception {

        if (tallasPorDetalle.size()
                != venta.getDetalles().size()) {
            throw new Exception(
                "Debe seleccionar una talla para cada detalle."
            );
        }

        // Agrupar cantidades por talla
        Map<Long, Integer> cantidades = new TreeMap<>();
        Map<Long, Long> productosPorTalla = new HashMap<>();

        for (DetalleVenta detalle : venta.getDetalles()) {

            Long idTalla = tallasPorDetalle.get(detalle);

            if (idTalla == null) {
                throw new Exception(
                    "Falta seleccionar la talla de un producto."
                );
            }

            Long idProducto = detalle.getProducto().getId();

            Long anterior = productosPorTalla.putIfAbsent(
                idTalla, idProducto
            );

            if (anterior != null &&
                !anterior.equals(idProducto)) {
                throw new Exception(
                    "Una talla no corresponde al producto."
                );
            }

            cantidades.merge(
                idTalla,
                detalle.getCantidad(),
                (a, b) -> Math.addExact(a, b)
            );
        }

        // Bloquear y actualizar existencias
        for (Map.Entry<Long, Integer> entrada
                : cantidades.entrySet()) {

            Long idTalla = entrada.getKey();
            int cantidad = entrada.getValue();

            ProductoTalla talla = em.find(
                ProductoTalla.class,
                idTalla,
                LockModeType.PESSIMISTIC_WRITE
            );

            if (talla == null) {
                throw new Exception(
                    "La talla seleccionada no existe."
                );
            }

            if (!talla.getProducto().getId().equals(
                    productosPorTalla.get(idTalla))) {
                throw new Exception(
                    "La talla no pertenece al producto."
                );
            }

            if (talla.getStockActual() < cantidad) {
                throw new Exception(
                    "Stock insuficiente para la talla "
                    + talla.getTalla()
                );
            }

            talla.setStockActual(
                talla.getStockActual() - cantidad
            );
        }
    }

    @Override
    public void actualizar(Venta venta) throws Exception {
        throw new Exception(
            "No se permite modificar ventas registradas. "
            + "Se requiere un proceso de cancelacion "
            + "que controle el inventario y el pago."
        );
    }

    @Override
    public void eliminar(Long id) throws Exception {
        throw new Exception(
            "No se permite eliminar ventas registradas "
            + "porque se perderia el historial. "
            + "Utilice un proceso de cancelacion."
        );
    }

    @Override
    public Venta buscarPorId(Long id) {
        EntityManager em = conexion.getEntityManager();

        try {
            if (id == null) {
                return null;
            }

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

    // Validaciones basicas de la venta
    private void validarVenta(Venta venta) {

        if (venta == null) {
            throw new IllegalArgumentException(
                "La venta no puede ser nula."
            );
        }

        if (venta.getId() != null) {
            throw new IllegalArgumentException(
                "Una venta nueva no debe tener ID."
            );
        }

        if (venta.getFolio() == null ||
            venta.getFolio().isBlank() ||
            venta.getFolio().length() > 50) {
            throw new IllegalArgumentException(
                "El folio no es valido."
            );
        }

        if (venta.getFechaHora() == null) {
            throw new IllegalArgumentException(
                "Debe indicar la fecha de la venta."
            );
        }

        if (venta.getUsuario() == null ||
            venta.getUsuario().getId() == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar un usuario registrado."
            );
        }

        if (!Float.isFinite(venta.getSubtotal()) ||
            !Float.isFinite(venta.getIva()) ||
            !Float.isFinite(venta.getTotal()) ||
            venta.getSubtotal() < 0 ||
            venta.getIva() < 0 ||
            venta.getTotal() <= 0) {
            throw new IllegalArgumentException(
                "Los importes de la venta no son validos."
            );
        }

        Pago pago = venta.getPago();

        if (pago == null ||
            pago.getId() != null ||
            pago.getMetodoPago() == null ||
            pago.getMonto() == null ||
            !Float.isFinite(pago.getMonto()) ||
            pago.getMonto() <= 0 ||
            pago.getFechaHora() == null) {
            throw new IllegalArgumentException(
                "Debe proporcionar un pago nuevo y valido."
            );
        }

        if (venta.getDetalles() == null ||
            venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                "La venta debe contener productos."
            );
        }

        for (DetalleVenta detalle : venta.getDetalles()) {

            if (detalle == null ||
                detalle.getId() != null ||
                detalle.getProducto() == null ||
                detalle.getProducto().getId() == null) {
                throw new IllegalArgumentException(
                    "Uno de los detalles no es valido."
                );
            }

            if (detalle.getCantidad() <= 0 ||
                !Float.isFinite(detalle.getPrecioUnitario()) ||
                !Float.isFinite(detalle.getSubtotal()) ||
                detalle.getPrecioUnitario() < 0 ||
                detalle.getSubtotal() < 0) {
                throw new IllegalArgumentException(
                    "Cantidad o importes del detalle invalidos."
                );
            }
        }
    }
}
