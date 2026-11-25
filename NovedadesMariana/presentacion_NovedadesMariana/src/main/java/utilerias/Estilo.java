package utilerias;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class Estilo {
    public static final Color AZUL = new Color(17, 30, 62);
    public static final Color FONDO = new Color(242, 245, 250);
    public static final Color VERDE = new Color(20, 158, 77);
    public static final Color CABECERA_COBRO = new Color(13, 24, 48);
    public static final Color VERDE_COBRO = new Color(22, 163, 74);
    public static final Color VERDE_SUAVE = new Color(236, 253, 243);
    public static final Color VERDE_OSCURO = new Color(21, 128, 61);
    public static final Color BORDE_VERDE = new Color(187, 247, 208);
    public static final Color TEXTO_SECUNDARIO = new Color(100, 116, 139);
    public static final Color TEXTO_OSCURO = new Color(15, 23, 42);
    public static final Color FONDO_TECLAS = new Color(241, 245, 249);
    public static final Color BORDE_TARJETA = new Color(203, 213, 225);
    public static final Color AZUL_CLARO = new Color(147, 197, 253);
    public static final Color BORDE_ACTIVO = new Color(59, 130, 246);
    public static final Color FONDO_EXACTO = new Color(238, 242, 255);
    public static final Color TEXTO_EXACTO = new Color(67, 56, 202);
    public static final Color BORDE_EXACTO = new Color(199, 210, 254);
    public static final Color ERROR = new Color(185, 28, 28);
    public static final Color AZUL_DESCUENTO = new Color(29, 78, 216);
    public static final Color FONDO_DESCUENTO = new Color(239, 246, 255);
    public static final Color BORDE_DESCUENTO = new Color(191, 219, 254);
    public static final Color TEXTO_DESCUENTO = new Color(71, 85, 105);
    public static final Color VERDE_DESCUENTO = new Color(134, 212, 169);
    public static final Color FONDO_VENTA = new Color(248, 250, 252);
    public static final Color TEXTO_VENTA = new Color(30, 41, 59);
    public static final Color FONDO_CANCELAR = new Color(254, 226, 226);
    public static final Color BORDE_CANCELAR = new Color(252, 165, 165);
    public static final Color FONDO_TALLA = new Color(219, 234, 254);
    public static final Color TEXTO_TALLA = new Color(30, 64, 175);
    public static final Color FONDO_BEBE = new Color(255, 247, 237);
    public static final Color TEXTO_BEBE = new Color(194, 65, 12);
    public static final Color FONDO_NINA = new Color(253, 242, 248);
    public static final Color TEXTO_NINA = new Color(190, 24, 93);
    public static final Color FONDO_ACCESORIOS = new Color(254, 252, 232);
    public static final Color TEXTO_ACCESORIOS = new Color(161, 98, 7);

    private Estilo() { }

    public static String dinero(float valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX")).format(valor);
    }

    /**
     * Crea un botón de alto contraste con renderizado propio para garantizar
     * legibilidad en cualquier sistema operativo (macOS Aqua, Windows, Linux).
     */
    public static JButton boton(String texto, boolean principal) {
        return botonPersonalizado(
                texto,
                principal ? VERDE_COBRO : new Color(241, 245, 249),
                principal ? VERDE_OSCURO : new Color(203, 213, 225),
                principal ? Color.WHITE : TEXTO_OSCURO
        );
    }

    /**
     * Botón con fondo y bordes redondeados garantizados que nunca pierde contraste de texto.
     */
    public static JButton botonPersonalizado(String texto, Color fondo, Color borde, Color textoColor) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                if (!isEnabled()) {
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(new Color(226, 232, 240));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                } else if (getModel().isPressed()) {
                    g2.setColor(fondo.darker());
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(borde.darker());
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                } else if (getModel().isRollover()) {
                    g2.setColor(fondo.brighter());
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(borde);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                } else {
                    g2.setColor(fondo);
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                    g2.setColor(borde);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setForeground(textoColor);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        return btn;
    }

    public static JPanel vertical() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    public static JLabel titulo(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setForeground(AZUL);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        return titulo;
    }
}
