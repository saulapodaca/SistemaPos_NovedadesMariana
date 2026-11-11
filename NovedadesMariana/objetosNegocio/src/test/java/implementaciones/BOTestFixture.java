package implementaciones;

import bos.VentaBO;
import daos.CategoriaDAO;
import daos.ProductoDAO;
import daos.UsuarioDAO;
import daos.VentaDAO;
import interfaces.ICategoriaDAO;
import interfaces.IProductoDAO;
import interfaces.IUsuarioDAO;
import interfaces.IVentaDAO;
import dominio.Categoria;
import dominio.Producto;
import dominio.ProductoTalla;
import enums.RolEmpleado;
import enums.TipoPrenda;
import dominio.Usuario;
import dtos.DetalleVentaDTO;
import dtos.PagoDTO;
import dtos.ResumenVentaDTO;
import dtos.VentaDTO;
import java.util.List;
import java.util.UUID;

/** Fixtures de prueba que crean y limpian registros mediante los DAO reales. */
final class BOTestFixture implements AutoCloseable {

    private final ICategoriaDAO categoriaDAO = new CategoriaDAO();
    private final IProductoDAO productoDAO = new ProductoDAO();
    private final IUsuarioDAO usuarioDAO = new UsuarioDAO();
    private final IVentaDAO ventaDAO = new VentaDAO();

    final Categoria categoria;
    final Producto producto;
    final ProductoTalla talla;
    final Usuario usuario;

    private Long idVenta;

    BOTestFixture() throws Exception {
        String codigo = UUID.randomUUID().toString().replace("-", "");

        categoria = new Categoria("BO-" + codigo,
                "Categoría de las pruebas BO");
        categoriaDAO.guardar(categoria);

        producto = new Producto("SKU-BO-" + codigo, "BAR-BO-" + codigo,
                "Producto de prueba", "Fixture para pruebas de negocio",
                250.0f, TipoPrenda.VESTIDO);
        producto.agregarCategoria(categoria);
        talla = new ProductoTalla("M", 20, producto);
        producto.agregarTalla(talla);
        productoDAO.guardar(producto);

        usuario = new Usuario("usuario_" + codigo, "clave_prueba",
                "Ana", "Prueba", "", RolEmpleado.EMPLEADO);
        usuarioDAO.guardar(usuario);
    }

    VentaDTO nuevaVenta(int cantidad) {
        DetalleVentaDTO detalle = new DetalleVentaDTO(0, 0,
                Math.toIntExact(talla.getId()), producto.getNombre(),
                talla.getTalla(), cantidad, producto.getPrecio(),
                producto.getPrecio() * cantidad);
        ResumenVentaDTO resumen = VentaBO.getInstance().calcularTotales(
                List.of(detalle), 0.0f);
        return nuevaVenta(detalle, resumen, cantidad, "EFECTIVO", 50.0f);
    }

    VentaDTO nuevaVenta(int cantidad, String metodoPago, float cambioSolicitado) {
        DetalleVentaDTO detalle = new DetalleVentaDTO(0, 0,
                Math.toIntExact(talla.getId()), producto.getNombre(),
                talla.getTalla(), cantidad, producto.getPrecio(),
                producto.getPrecio() * cantidad);
        ResumenVentaDTO resumen = VentaBO.getInstance().calcularTotales(
                List.of(detalle), 0.0f);
        return nuevaVenta(detalle, resumen, cantidad, metodoPago, cambioSolicitado);
    }

    private VentaDTO nuevaVenta(DetalleVentaDTO detalle, ResumenVentaDTO resumen,
                                int cantidad, String metodoPago, float cambioSolicitado) {
        float recibido = "EFECTIVO".equalsIgnoreCase(metodoPago.trim())
                ? resumen.getTotal() + cambioSolicitado : resumen.getTotal();
        PagoDTO pago = new PagoDTO(0, 0, metodoPago, resumen.getTotal(),
                recibido, 0.0f, null);
        return new VentaDTO(0, null, null, resumen.getSubtotal(),
                resumen.getIva(), resumen.getTotal(),
                Math.toIntExact(usuario.getId()), List.of(detalle), List.of(pago));
    }

    VentaDTO registrarVenta(int cantidad) {
        return registrarVenta(nuevaVenta(cantidad));
    }

    VentaDTO registrarVenta(int cantidad, String metodoPago, float cambioSolicitado) {
        return registrarVenta(nuevaVenta(cantidad, metodoPago, cambioSolicitado));
    }

    VentaDTO registrarVenta(VentaDTO dto) {
        VentaDTO registrada = VentaBO.getInstance().registrarVenta(dto);
        idVenta = (long) registrada.getId();
        return registrada;
    }

    @Override
    public void close() throws Exception {
        if (idVenta != null && ventaDAO.buscarVentaPorId(idVenta) != null) {
            VentaBO.getInstance().cancelarVenta(Math.toIntExact(idVenta));
        }
        if (producto.getId() != null && productoDAO.buscarProductoPorId(producto.getId()) != null) {
            productoDAO.eliminar(producto.getId());
        }
        if (categoria.getId() != null && categoriaDAO.buscarCategoriaPorId(categoria.getId()) != null) {
            categoriaDAO.eliminar(categoria.getId());
        }
        if (usuario.getId() != null && usuarioDAO.buscarPorId(usuario.getId()) != null) {
            usuarioDAO.eliminar(usuario.getId());
        }
    }
}
