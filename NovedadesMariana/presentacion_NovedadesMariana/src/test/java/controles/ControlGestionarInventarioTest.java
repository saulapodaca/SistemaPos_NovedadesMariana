package controles;

import com.itson.negocios_gestorinventario.IGestorInventario;
import dtos.CategoriaDTO;
import dtos.ProductoDTO;
import dtos.ProductoTallaDTO;
import dtos.TipoPrendaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ControlGestionarInventarioTest {

    private ControlGestionarInventario control;
    private FakeGestorInventario fakeGestor;

    static class FakeGestorInventario implements IGestorInventario {
        final List<ProductoDTO> productos = new ArrayList<>();
        final Map<Integer, List<ProductoTallaDTO>> tallasPorProducto = new HashMap<>();
        final List<CategoriaDTO> categorias = new ArrayList<>();

        @Override
        public ProductoDTO buscarProductoPorCodigo(String codigo) {
            return productos.stream().filter(p -> codigo.equals(p.getCodigoBarras())).findFirst().orElse(null);
        }

        @Override
        public ProductoDTO obtenerProductoPorId(int id) {
            return productos.stream().filter(p -> p.getId() != null && p.getId() == (long) id).findFirst().orElse(null);
        }

        @Override
        public List<ProductoDTO> listarProductosPorCategoria(int idCategoria) {
            return productos.stream()
                    .filter(p -> p.getCategoriasIds() != null && p.getCategoriasIds().contains((long) idCategoria))
                    .toList();
        }

        @Override
        public List<ProductoTallaDTO> obtenerTallasPorProducto(int idProducto) {
            return tallasPorProducto.getOrDefault(idProducto, Collections.emptyList());
        }

        @Override
        public boolean validarStockDisponible(int idProductoTalla, int cantidad) {
            for (List<ProductoTallaDTO> list : tallasPorProducto.values()) {
                for (ProductoTallaDTO t : list) {
                    if (t.getId() == idProductoTalla) {
                        return t.getStockActual() >= cantidad;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean descontarStock(int idProductoTalla, int cantidad) {
            for (List<ProductoTallaDTO> list : tallasPorProducto.values()) {
                for (ProductoTallaDTO t : list) {
                    if (t.getId() == idProductoTalla) {
                        t.setStockActual(t.getStockActual() - cantidad);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean reabastecerStock(int idProductoTalla, int cantidad) {
            for (List<ProductoTallaDTO> list : tallasPorProducto.values()) {
                for (ProductoTallaDTO t : list) {
                    if (t.getId() == idProductoTalla) {
                        t.setStockActual(t.getStockActual() + cantidad);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean ajustarStock(int idProductoTalla, int nuevoStock) {
            for (List<ProductoTallaDTO> list : tallasPorProducto.values()) {
                for (ProductoTallaDTO t : list) {
                    if (t.getId() == idProductoTalla) {
                        t.setStockActual(nuevoStock);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public List<ProductoDTO> buscarProductos(String criterio) {
            return productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(criterio.toLowerCase())
                            || p.getSku().toLowerCase().contains(criterio.toLowerCase()))
                    .toList();
        }

        @Override
        public List<ProductoDTO> buscarProductosPorCategoria(String nombre) {
            CategoriaDTO cat = categorias.stream()
                    .filter(c -> c.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
            if (cat == null) return Collections.emptyList();
            return listarProductosPorCategoria(cat.getId().intValue()).stream()
                    .map(p -> (ProductoDTO) p).toList();
        }

        @Override
        public List<ProductoDTO> obtenerTodosLosProductos() {
            return new ArrayList<>(productos);
        }

        @Override
        public void guardarProducto(ProductoDTO producto) {
            producto.setId((long) (productos.size() + 1));
            productos.add(producto);
        }

        @Override
        public void actualizarProducto(ProductoDTO producto) {
            productos.removeIf(p -> Objects.equals(p.getId(), producto.getId()));
            productos.add(producto);
        }

        @Override
        public void eliminarProducto(Long id) {
            productos.removeIf(p -> Objects.equals(p.getId(), id));
        }

        @Override
        public void agregarTalla(ProductoTallaDTO talla) {
            talla.setId(new Random().nextInt(10000) + 1);
            tallasPorProducto.computeIfAbsent(talla.getIdProducto(), k -> new ArrayList<>()).add(talla);
        }

        @Override
        public void eliminarTalla(int idProductoTalla) {
            for (List<ProductoTallaDTO> list : tallasPorProducto.values()) {
                list.removeIf(t -> t.getId() == idProductoTalla);
            }
        }

        @Override
        public List<CategoriaDTO> obtenerCategorias() {
            return new ArrayList<>(categorias);
        }
    }

    @BeforeEach
    void setUp() {
        fakeGestor = new FakeGestorInventario();
        control = new ControlGestionarInventario(fakeGestor);

        // Datos iniciales
        CategoriaDTO cat1 = new CategoriaDTO(1L, "Niño", "Ropa de niño");
        CategoriaDTO cat2 = new CategoriaDTO(2L, "Niña", "Ropa de niña");
        fakeGestor.categorias.add(cat1);
        fakeGestor.categorias.add(cat2);

        ProductoDTO p1 = new ProductoDTO(1L, "CB-101", "SKU-101", "Playera Rayas", "Algodón", 150f, TipoPrendaDTO.PLAYERA, Set.of(1L));
        fakeGestor.productos.add(p1);

        ProductoTallaDTO t1 = new ProductoTallaDTO(101, 1, "2T", 8);
        ProductoTallaDTO t2 = new ProductoTallaDTO(102, 1, "4T", 12);
        fakeGestor.tallasPorProducto.put(1, new ArrayList<>(List.of(t1, t2)));
    }

    @Test
    void cargarInventarioDevuelveProductosRegistrados() {
        List<ProductoDTO> prods = control.cargarInventario();
        assertEquals(1, prods.size());
        assertEquals("Playera Rayas", prods.get(0).getNombre());
    }

    @Test
    void buscarProductosFiltraCorrectamentePorCriterio() {
        List<ProductoDTO> encontrados = control.buscarProductos("Rayas");
        assertEquals(1, encontrados.size());

        List<ProductoDTO> noEncontrados = control.buscarProductos("Inexistente");
        assertTrue(noEncontrados.isEmpty());
    }

    @Test
    void calcularStockTotalSumaExistenciasDeTodasLasTallas() {
        int total = control.calcularStockTotal(1);
        assertEquals(20, total); // 8 + 12
    }

    @Test
    void reabastecerStockIncrementaExistencias() {
        boolean exito = control.reabastecerStock(101, 5);
        assertTrue(exito);

        List<ProductoTallaDTO> tallas = control.obtenerTallas(1);
        assertEquals(13, tallas.get(0).getStockActual()); // 8 + 5
    }

    @Test
    void rechazaReabastecerStockConCantidadInvalida() {
        assertThrows(IllegalArgumentException.class, () -> control.reabastecerStock(101, 0));
        assertThrows(IllegalArgumentException.class, () -> control.reabastecerStock(101, -3));
    }

    @Test
    void ajustarStockFijaExistenciaExacta() {
        boolean exito = control.ajustarStock(101, 25);
        assertTrue(exito);

        List<ProductoTallaDTO> tallas = control.obtenerTallas(1);
        assertEquals(25, tallas.get(0).getStockActual());

        // Ajustar a cero (merma total o agotado)
        assertTrue(control.ajustarStock(101, 0));
        assertEquals(0, control.obtenerTallas(1).get(0).getStockActual());
    }

    @Test
    void rechazaAjusteStockNegativo() {
        assertThrows(IllegalArgumentException.class, () -> control.ajustarStock(101, -1));
    }

    @Test
    void agregarTallaRegistraNuevaExistencia() throws Exception {
        control.agregarTalla(1, "6T", 15);
        List<ProductoTallaDTO> tallas = control.obtenerTallas(1);
        assertEquals(3, tallas.size());
        assertEquals(35, control.calcularStockTotal(1)); // 8 + 12 + 15
    }

    @Test
    void rechazaAgregarTallaConDatosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> control.agregarTalla(1, "", 10));
        assertThrows(IllegalArgumentException.class, () -> control.agregarTalla(1, "   ", 10));
        assertThrows(IllegalArgumentException.class, () -> control.agregarTalla(1, "8T", -2));
        assertThrows(IllegalArgumentException.class, () -> control.agregarTalla(0, "8T", 5));
    }

    @Test
    void guardarProductoNuevoConTallasIniciales() throws Exception {
        ProductoDTO nuevo = new ProductoDTO("CB-202", "SKU-202", "Vestido Flores", "Fiesta", 299f, TipoPrendaDTO.VESTIDO, Set.of(2L));
        List<ProductoTallaDTO> tallas = List.of(
                new ProductoTallaDTO(0, 0, "4T", 6),
                new ProductoTallaDTO(0, 0, "6T", 8)
        );

        control.guardarProducto(nuevo, tallas);
        assertNotNull(nuevo.getId());
        assertEquals(2, control.cargarInventario().size());

        List<ProductoTallaDTO> tallasGuardadas = control.obtenerTallas(nuevo.getId().intValue());
        assertEquals(2, tallasGuardadas.size());
        assertEquals(14, control.calcularStockTotal(nuevo.getId().intValue()));
    }
}
