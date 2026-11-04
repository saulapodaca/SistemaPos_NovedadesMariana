package interfaces.persistencia;

/** Contratos de acceso invocados por BO; no expone implementaciones. */
public interface IDAOFactory {
    ICategoriaDAO categorias();
    IProductoDAO productos();
    IProductoTallaDAO productoTallas();
    IUsuarioDAO usuarios();
    IPagoDAO pagos();
    IVentaDAO ventas();
}
