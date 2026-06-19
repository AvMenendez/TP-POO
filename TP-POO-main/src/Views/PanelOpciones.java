package Views;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

// Pantalla de opciones. Por ahora es un marcador para futuras configuraciones
// (volumen, dificultad, controles, etc.).
public class PanelOpciones extends JPanel {

    // Arma la pantalla de opciones (por ahora solo un aviso y el botón volver).
    public PanelOpciones(Runnable onVolver) {
        setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));
        setBackground(new Color(18, 22, 38));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("OPCIONES");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 40));
        titulo.setForeground(new Color(80, 200, 255));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel proximamente = new JLabel("Configuraciones próximamente...");
        proximamente.setFont(new Font("SansSerif", Font.PLAIN, 20));
        proximamente.setForeground(Color.LIGHT_GRAY);
        proximamente.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton volver = new JButton("Volver");
        volver.setFont(new Font("SansSerif", Font.BOLD, 18));
        volver.setAlignmentX(Component.CENTER_ALIGNMENT);
        volver.setFocusable(false);
        volver.addActionListener(e -> onVolver.run());

        add(Box.createVerticalGlue());
        add(titulo);
        add(Box.createRigidArea(new Dimension(0, 24)));
        add(proximamente);
        add(Box.createRigidArea(new Dimension(0, 36)));
        add(volver);
        add(Box.createVerticalGlue());

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
}
