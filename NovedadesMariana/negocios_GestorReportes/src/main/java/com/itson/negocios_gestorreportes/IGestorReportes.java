package com.itson.negocios_gestorreportes;

import dtos.VentaDTO;
import java.time.LocalDate;
import java.util.List;

public interface IGestorReportes {
    /** Consulta las ventas del periodo calendario que contiene la fecha indicada. */
    List<VentaDTO> obtenerVentasDelPeriodo(PeriodoReporte periodo, LocalDate fechaReferencia);

    /** Genera el PDF del reporte para el periodo calendario que contiene la fecha indicada. */
    byte[] generarReportePdf(PeriodoReporte periodo, LocalDate fechaReferencia);
}
