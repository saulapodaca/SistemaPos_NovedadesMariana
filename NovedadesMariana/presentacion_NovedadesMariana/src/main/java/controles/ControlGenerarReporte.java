package controles;

import com.itson.negocios_gestorreportes.PeriodoReporte;
import com.itson.negocios_gestorreportes.fachada.GenerarReporteFacade;
import com.itson.negocios_gestorreportes.fachada.IGenerarReporteFacade;
import dtos.DetalleVentaDTO;
import dtos.VentaDTO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Coordina la pantalla de reportes y delega el caso de uso a su fachada. */
public final class ControlGenerarReporte {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_MES =
            DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale.forLanguageTag("es-MX"));

    private static ControlGenerarReporte instance;
    private IGenerarReporteFacade facade;

    private ControlGenerarReporte() { }

    public ControlGenerarReporte(IGenerarReporteFacade facade) {
        this.facade = Objects.requireNonNull(facade);
    }

    public static synchronized ControlGenerarReporte getInstance() {
        if (instance == null) instance = new ControlGenerarReporte();
        return instance;
    }

    private synchronized IGenerarReporteFacade facade() {
        if (facade == null) facade = new GenerarReporteFacade();
        return facade;
    }

    public List<VentaDTO> consultarVentas(PeriodoReporte periodo, LocalDate fecha) {
        List<VentaDTO> ventas = facade().consultarVentas(periodo, fecha);
        return ventas == null ? List.of() : ventas;
    }

    /** Genera el PDF y lo escribe en el archivo elegido por el usuario. */
    public File exportarPdf(PeriodoReporte periodo, LocalDate fecha, File destino) throws IOException {
        Objects.requireNonNull(destino, "Selecciona dónde guardar el reporte.");
        File archivo = destino.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")
                ? destino : destino.toPath().resolveSibling(destino.getName() + ".pdf").toFile();
        Files.write(archivo.toPath(), facade().generarReportePdf(periodo, fecha));
        return archivo;
    }

    public String describirPeriodo(PeriodoReporte periodo, LocalDate fecha) {
        if (periodo == null || fecha == null) return "";
        LocalDate inicio = periodo.inicio(fecha);
        return switch (periodo) {
            case DIA -> inicio.format(FORMATO_FECHA);
            case SEMANA -> "Del " + inicio.format(FORMATO_FECHA) + " al "
                    + periodo.fin(fecha).minusDays(1).format(FORMATO_FECHA);
            case MES -> {
                String mes = inicio.format(FORMATO_MES);
                yield Character.toUpperCase(mes.charAt(0)) + mes.substring(1);
            }
        };
    }

    public String nombreArchivoSugerido(PeriodoReporte periodo, LocalDate fecha) {
        String sufijo = switch (periodo) {
            case DIA -> "dia_" + fecha;
            case SEMANA -> "semana_" + periodo.inicio(fecha);
            case MES -> "mes_" + fecha.getYear() + "-" + String.format("%02d", fecha.getMonthValue());
        };
        return "reporte_ventas_" + sufijo + ".pdf";
    }

    public static int contarArticulos(VentaDTO venta) {
        int total = 0;
        if (venta.getDetalles() != null) {
            for (DetalleVentaDTO detalle : venta.getDetalles()) {
                if (detalle != null) total += detalle.getCantidad();
            }
        }
        return total;
    }
}
