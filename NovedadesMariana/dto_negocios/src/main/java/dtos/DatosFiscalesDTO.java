/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author saula
 */
public class DatosFiscalesDTO {
    private String rfc;
    private String razonSocial;
    private String codigoPostal;
    private String regimenFiscal;
    private String usoCfdi;
    private String correo;

    public DatosFiscalesDTO() {
    }

    public DatosFiscalesDTO(String rfc, String razonSocial, String codigoPostal, String regimenFiscal, String usoCfdi, String correo) {
        this.rfc = rfc;
        this.razonSocial = razonSocial;
        this.codigoPostal = codigoPostal;
        this.regimenFiscal = regimenFiscal;
        this.usoCfdi = usoCfdi;
        this.correo = correo;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getRegimenFiscal() {
        return regimenFiscal;
    }

    public void setRegimenFiscal(String regimenFiscal) {
        this.regimenFiscal = regimenFiscal;
    }

    public String getUsoCfdi() {
        return usoCfdi;
    }

    public void setUsoCfdi(String usoCfdi) {
        this.usoCfdi = usoCfdi;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
}
