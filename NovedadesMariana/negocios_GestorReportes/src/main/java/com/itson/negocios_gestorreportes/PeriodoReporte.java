package com.itson.negocios_gestorreportes;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Periodo calendario cubierto por un reporte de ventas. */
public enum PeriodoReporte {
    DIA("Día"),
    SEMANA("Semana"),
    MES("Mes");

    private final String etiqueta;

    PeriodoReporte(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Primer día (inclusive) del periodo que contiene la fecha indicada. */
    public LocalDate inicio(LocalDate fechaReferencia) {
        return switch (this) {
            case DIA -> fechaReferencia;
            case SEMANA -> fechaReferencia.with(DayOfWeek.MONDAY);
            case MES -> fechaReferencia.withDayOfMonth(1);
        };
    }

    /** Día siguiente al último del periodo (exclusivo). */
    public LocalDate fin(LocalDate fechaReferencia) {
        LocalDate inicio = inicio(fechaReferencia);
        return switch (this) {
            case DIA -> inicio.plusDays(1);
            case SEMANA -> inicio.plusWeeks(1);
            case MES -> inicio.plusMonths(1);
        };
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
