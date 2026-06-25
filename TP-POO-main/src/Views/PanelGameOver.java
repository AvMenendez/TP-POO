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

// Pantalla de fin de partida (derrota o victoria): muestra el puntaje y ofrece reiniciar o salir.
public class PanelGameOver extends JPanel {

    // Arma la pantalla con el puntaje final y los botones de reiniciar/menú/salir.
    // 'gano' decide el título: victoria (superó el último nivel) o derrota.
    public PanelGameOver(int puntaje, boolean gano, Runnable onReiniciar, Runnable onMenu, Runnable onSalir) {
        setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));
        setBackground(new Color(18, 22, 38));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(gano ? "¡GANASTE!" : "GAME OVER");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 48));
        titulo.setForeground(gano ? new Color(90, 220, 120) : new Color(230, 90, 90));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelPuntaje = new JLabel("Puntaje final: " + puntaje);
        labelPuntaje.setFont(new Font("SansSerif", Font.PLAIN, 22));
        labelPuntaje.setForeground(Color.WHITE);
        labelPuntaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton reiniciar = new JButton("Volver a jugar");
        reiniciar.setFont(new Font("SansSerif", Font.BOLD, 18));
        reiniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
        reiniciar.setFocusable(false);
        reiniciar.addActionListener(e -> onReiniciar.run());

        JButton menu = new JButton("Menú principal");
        menu.setFont(new Font("SansSerif", Font.PLAIN, 16));
        menu.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.setFocusable(false);
        menu.addActionListener(e -> onMenu.run());

        JButton salir = new JButton("Salir");
        salir.setFont(new Font("SansSerif", Font.PLAIN, 16));
        salir.setAlignmentX(Component.CENTER_ALIGNMENT);
        salir.setFocusable(false);
        salir.addActionListener(e -> onSalir.run());

        add(Box.createVerticalGlue());
        add(titulo);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(labelPuntaje);
        add(Box.createRigidArea(new Dimension(0, 32)));
        add(reiniciar);
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(menu);
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(salir);
        add(Box.createVerticalGlue());

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
}
