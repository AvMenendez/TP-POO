package Views;

import javax.swing.Box;
import java.awt.Dimension;

// Pantalla de opciones. Por ahora es un marcador para futuras configuraciones
// (volumen, dificultad, controles, etc.).
public class PanelOpciones extends PanelFondo {

    // Arma la pantalla de opciones (por ahora solo un aviso y el botón volver).
    public PanelOpciones(Runnable onVolver) {
        add(Box.createVerticalGlue());
        add(Estilo.titulo("OPCIONES", 40, Estilo.ACENTO));
        add(Box.createRigidArea(new Dimension(0, 24)));
        add(Estilo.etiqueta("Configuraciones próximamente...", 20, Estilo.TEXTO_TENUE));
        add(Box.createRigidArea(new Dimension(0, 36)));
        add(Estilo.botonTransparente("Volver", 18, onVolver));
        add(Box.createVerticalGlue());
    }
}
