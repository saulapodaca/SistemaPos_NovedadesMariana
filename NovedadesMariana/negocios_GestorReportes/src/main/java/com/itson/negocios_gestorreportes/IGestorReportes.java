package com.itson.negocios_gestorreportes;

import java.time.LocalDate;

public interface IGestorReportes {
    /** Genera el PDF del reporte para el periodo calendario que contiene la fecha indicada. */
    byte[] generarReportePdf(PeriodoReporte periodo, LocalDate fechaReferencia);
}
