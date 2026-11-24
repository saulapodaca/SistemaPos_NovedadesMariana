package com.itson.negocios_gestorreportes.fachada;

import com.itson.negocios_gestorreportes.PeriodoReporte;
import dtos.VentaDTO;
import java.time.LocalDate;
import java.util.List;

/** Punto de entrada de la presentación al caso de uso Generar reporte de ventas. */
public interface IGenerarReporteFacade {

    /** Ventas del periodo, para mostrar una vista previa antes de exportar. */
    List<VentaDTO> consultarVentas(PeriodoReporte periodo, LocalDate fechaReferencia);

    /** Contenido del PDF del reporte del periodo. */
    byte[] generarReportePdf(PeriodoReporte periodo, LocalDate fechaReferencia);
}
