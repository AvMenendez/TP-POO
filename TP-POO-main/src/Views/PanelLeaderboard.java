package Views;

import Model.Leaderboard;

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
import java.util.List;

// Muestra las 5 mejores partidas registradas.
public class PanelLeaderboard extends JPanel {

    // Arma la pantalla listando las mejores partidas (o un aviso si no hay ninguna).
    public PanelLeaderboard(List<Leaderboard.Entrada> top, Runnable onVolver) {
        setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));
        setBackground(new Color(18, 22, 38));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("LEADERBOARD");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 40));
        titulo.setForeground(new Color(255, 200, 80));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(titulo);
        add(Box.createRigidArea(new Dimension(0, 28)));

        if (top.isEmpty()) {
            add(crearLinea("Todavía no hay partidas registradas.", false));
        } else {
            for (int i = 0; i < top.size(); i++) {
                Leaderboard.Entrada e = top.get(i);
                add(crearLinea((i + 1) + ".  " + e.getNombre() + "  —  " + e.getPuntaje() + " pts", true));
                add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        add(Box.createRigidArea(new Dimension(0, 28)));
        JButton volver = new JButton("Volver");
        volver.setFont(new Font("SansSerif", Font.BOLD, 18));
        volver.setAlignmentX(Component.CENTER_ALIGNMENT);
        volver.setFocusable(false);
        volver.addActionListener(ev -> onVolver.run());
        add(volver);
        add(Box.createVerticalGlue());

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    // Crea una línea de texto del listado (con o sin fuente monoespaciada).
    private JLabel crearLinea(String texto, boolean monospace) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font(monospace ? "Monospaced" : "SansSerif", Font.PLAIN, 22));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}
