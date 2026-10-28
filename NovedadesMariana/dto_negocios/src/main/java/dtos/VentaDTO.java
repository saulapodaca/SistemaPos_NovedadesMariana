/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author saula
 */
public class VentaDTO {
    private int id;
    private String folio;
    private LocalDateTime fechaHora;
    private float subtotal;
    private float iva;
    private float total;
    private int idUsuario;
    private List<DetalleVentaDTO> detalles;
    private List<PagoDTO> pagos;

    public VentaDTO() {
    }

    public VentaDTO(int id, String folio, LocalDateTime fechaHora, float subtotal, float iva, float total, int idUsuario, List<DetalleVentaDTO> detalles, List<PagoDTO> pagos) {
        this.id = id;
        this.folio = folio;
        this.fechaHora = fechaHora;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.idUsuario = idUsuario;
        this.detalles = detalles;
        this.pagos = pagos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }

    public List<PagoDTO> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoDTO> pagos) {
        this.pagos = pagos;
    }
    
}
