package com.itson.presentacion_novedadesmariana;

import controles.ControlNavegacion;
import utilerias.GestorSesion;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Presentacion_NovedadesMariana {
    private Presentacion_NovedadesMariana() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("defaultFont", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
            GestorSesion.getInstance().iniciarSesionMock();
            ControlNavegacion.getInstance().iniciar();
        });
    }
}