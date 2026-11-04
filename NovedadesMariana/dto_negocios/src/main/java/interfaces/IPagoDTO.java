package interfaces;

import java.time.LocalDateTime;

public interface IPagoDTO {
    int getId();
    void setId(int id);
    int getIdVenta();
    void setIdVenta(int idVenta);
    String getMetodoPago();
    void setMetodoPago(String metodoPago);
    float getMonto();
    void setMonto(float monto);
    float getMontoRecibido();
    void setMontoRecibido(float montoRecibido);
    float getCambio();
    void setCambio(float cambio);
    LocalDateTime getFechaHora();
    void setFechaHora(LocalDateTime fechaHora);
}
