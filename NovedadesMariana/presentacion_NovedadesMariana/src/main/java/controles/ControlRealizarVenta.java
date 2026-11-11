package controles;

import com.itson.negocios_realizarventa.fachada.IRealizarVentaFacade;
import com.itson.negocios_realizarventa.fachada.RealizarVentaFacade;
import dtos.*;
import utilerias.GestorSesion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Coordina el carrito y delega las operaciones del caso de uso a su fachada. */
public final class ControlRealizarVenta {
    private static ControlRealizarVenta instance;
    private IRealizarVentaFacade facade;
    private VentaDTO venta;
    private float porcentajeDescuento;
    private String motivoDescuento = "";
    private final GestorSesion sesion = GestorSesion.getInstance();

    private ControlRealizarVenta() {
        cancelarVentaActual();
    }

    private synchronized IRealizarVentaFacade facade() {
        if (facade == null) facade = new RealizarVentaFacade();
        return facade;
    }
    public ControlRealizarVenta(IRealizarVentaFacade facade) {
        this.facade = Objects.requireNonNull(facade);
        cancelarVentaActual();
    }

    public static synchronized ControlRealizarVenta getInstance() {
        if (instance == null) instance = new ControlRealizarVenta();
        return instance;
    }

    public List<ProductoDTO> buscarProductos(String texto) {
        if (texto == null || texto.isBlank()) return List.of();
        List<ProductoDTO> productos = facade().buscarProductos(texto.trim());
        return productos == null ? List.of() : productos;
    }

    public ProductoDTO buscarProductoPorCodigo(String codigo) {
        return facade().buscarProductoPorCodigo(codigo);
    }

    public List<ProductoTallaDTO> obtenerTallas(int idProducto) {
        List<ProductoTallaDTO> tallas = facade().obtenerTallasPorProducto(idProducto);
        return tallas == null ? List.of() : tallas;
    }

    public VentaDTO agregarProductoAVenta(ProductoDTO producto, ProductoTallaDTO talla, int cantidad) {
        validarEditable();
        if (producto == null || talla == null || producto.getId() == null
                || producto.getId().longValue() != talla.getIdProducto()) {
            throw new IllegalArgumentException("Selecciona un producto y su talla.");
        }
        DetalleVentaDTO existente = venta.getDetalles().stream()
                .filter(d -> d.getIdProductoTalla() == talla.getId()).findFirst().orElse(null);
        int nuevaCantidad = cantidad + (existente == null ? 0 : existente.getCantidad());
        if (cantidad <= 0 || !facade().validarStockDisponible(talla.getId(), nuevaCantidad)) {
            throw new IllegalArgumentException("Cantidad inválida o existencias insuficientes.");
        }
        if (existente == null) {
            venta.getDetalles().add(new DetalleVentaDTO(0, 0, talla.getId(), producto.getNombre(),
                    talla.getTalla(), cantidad, producto.getPrecio(), cantidad * producto.getPrecio()));
        } else {
            existente.setCantidad(nuevaCantidad);
            existente.setSubtotal(nuevaCantidad * existente.getPrecioUnitario());
        }
        recalcular();
        return venta;
    }

    public VentaDTO modificarCantidadProducto(int idTalla, int cantidad) {
        validarEditable();
        if (cantidad <= 0 || !facade().validarStockDisponible(idTalla, cantidad)) {
            throw new IllegalArgumentException("Cantidad inválida o existencias insuficientes.");
        }
        DetalleVentaDTO detalle = venta.getDetalles().stream()
                .filter(d -> d.getIdProductoTalla() == idTalla).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Selecciona un artículo de la venta."));
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(cantidad * detalle.getPrecioUnitario());
        recalcular();
        return venta;
    }

    public VentaDTO eliminarProductoDeVenta(int idTalla) {
        validarEditable();
        venta.getDetalles().removeIf(d -> d.getIdProductoTalla() == idTalla);
        recalcular();
        return venta;
    }

    public void cancelarVentaActual() {
        venta = new VentaDTO();
        venta.setDetalles(new ArrayList<>());
        venta.setPagos(new ArrayList<>());
        porcentajeDescuento = 0;
        motivoDescuento = "";
    }

    public VentaDTO aplicarDescuento(float porcentaje, String motivo) {
        validarEditable();
        if (!Float.isFinite(porcentaje) || porcentaje < 0 || porcentaje >= 100) {
            throw new IllegalArgumentException("Indica un porcentaje entre 0 y menos de 100.");
        }
        if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("Indica el motivo.");
        if (venta.getDetalles().isEmpty()) throw new IllegalArgumentException("Agrega productos primero.");
        porcentajeDescuento = porcentaje;
        motivoDescuento = motivo;
        recalcular();
        return venta;
    }

    public VentaDTO quitarDescuento() {
        validarEditable();
        porcentajeDescuento = 0;
        motivoDescuento = "";
        recalcular();
        return venta;
    }

    public float calcularCambio(float recibido) {
        if (!Float.isFinite(recibido)) throw new IllegalArgumentException("Indica un importe válido.");
        // Compensa únicamente el error binario de float al introducir un importe con dos decimales.
        float importe = normalizarRecibido(recibido);
        return facade().calcularCambio(venta.getTotal(), importe);
    }

    public UsuarioDTO autenticar(String nombre, String clave) {
        UsuarioDTO autenticado = facade().autenticarUsuario(nombre, clave);
        if (autenticado == null) throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        sesion.iniciarSesion(autenticado);
        return sesion.getEmpleadoActual();
    }

    public VentaDTO procesarVenta(String metodo, float recibido) {
        validarEditable();
        UsuarioDTO usuario = sesion.getEmpleadoActual();
        if (usuario == null) throw new IllegalArgumentException("Identifica al empleado antes de cobrar.");
        if (venta.getDetalles().isEmpty() || venta.getTotal() <= 0) {
            throw new IllegalArgumentException("Agrega productos antes de cobrar.");
        }
        if (!List.of("EFECTIVO", "TARJETA", "TRANSFERENCIA").contains(metodo)) {
            throw new IllegalArgumentException("Selecciona un método de pago.");
        }
        float montoRecibido = metodo.equals("EFECTIVO") ? normalizarRecibido(recibido) : venta.getTotal();
        float cambio = calcularCambio(montoRecibido);
        if (montoRecibido < venta.getTotal()) throw new IllegalArgumentException("El importe recibido es insuficiente.");
        venta.setIdUsuario(usuario.getId().intValue());
        venta.setPagos(List.of(new PagoDTO(0, 0, metodo, venta.getTotal(), montoRecibido,
                cambio, LocalDateTime.now())));
        VentaDTO registrada = facade().registrarVenta(venta);
        if (registrada == null || registrada.getId() <= 0) {
            throw new IllegalStateException("La fachada no devolvió una venta registrada.");
        }
        venta = registrada;
        return venta;
    }

    public boolean imprimirTicket() {
        if (venta.getId() <= 0) throw new IllegalStateException("Registra la venta primero.");
        return facade().imprimirTicket(venta.getId());
    }

    public VentaDTO getVenta() { return venta; }
    public UsuarioDTO getUsuario() { return sesion.getEmpleadoActual(); }
    public String getMotivoDescuento() { return motivoDescuento; }
    public float getDescuento() { return venta.getSubtotal() * porcentajeDescuento / 100; }

    private float normalizarRecibido(float recibido) {
        if (Math.abs(recibido - venta.getTotal()) < 0.0001f) return venta.getTotal();
        return recibido;
    }
    private void validarEditable() {
        if (venta.getId() > 0) throw new IllegalStateException("Inicia una nueva venta.");
    }

    private void recalcular() {
        if (venta.getDetalles().isEmpty()) {
            porcentajeDescuento = 0;
            motivoDescuento = "";
            venta.setSubtotal(0);
            venta.setIva(0);
            venta.setTotal(0);
            return;
        }
        ResumenVentaDTO resumen = facade().calcularTotales(venta.getDetalles(), porcentajeDescuento);
        venta.setSubtotal(resumen.getSubtotal());
        venta.setIva(resumen.getIva());
        venta.setTotal(resumen.getTotal());
    }
    public List<ProductoDTO> buscarProductosPorCategoria(String nombre) {
        if (nombre == null || nombre.isBlank()) return List.of();
        List<ProductoDTO> productos = facade().buscarProductosPorCategoria(nombre.trim());
        return productos == null ? List.of() : productos;
    }
}