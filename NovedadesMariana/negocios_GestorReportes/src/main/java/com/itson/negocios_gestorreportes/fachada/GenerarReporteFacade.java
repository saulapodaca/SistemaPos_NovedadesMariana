package com.itson.negocios_gestorreportes.fachada;

import com.itson.negocios_gestorreportes.GestorReportes;
import com.itson.negocios_gestorreportes.IGestorReportes;
import com.itson.negocios_gestorreportes.PeriodoReporte;
import dtos.VentaDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GenerarReporteFacade implements IGenerarReporteFacade {

    private final IGestorReportes gestorReportes;

    public GenerarReporteFacade() {
        this(new GestorReportes());
    }

    public GenerarReporteFacade(IGestorReportes gestorReportes) {
        this.gestorReportes = Objects.requireNonNull(gestorReportes, "El gestor de reportes es obligatorio.");
    }

    @Override
    public List<VentaDTO> consultarVentas(PeriodoReporte periodo, LocalDate fechaReferencia) {
        List<VentaDTO> ventas = this.gestorReportes.obtenerVentasDelPeriodo(periodo, fechaReferencia);
        List<VentaDTO> resultado = new ArrayList<>();
        if (ventas != null) {
            for (VentaDTO venta : ventas) {
                if (venta != null) {
                    resultado.add(venta);
                }
            }
        }
        return resultado;
    }

    @Override
    public byte[] generarReportePdf(PeriodoReporte periodo, LocalDate fechaReferencia) {
        return this.gestorReportes.generarReportePdf(periodo, fechaReferencia);
    }
}
