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

// Pantalla inicial de Sky Defense con sus cuatro opciones.
public class MenuPrincipal extends JPanel {

    // Arma el menú con el título y los cuatro botones (cada uno ejecuta su acción).
    public MenuPrincipal(Runnable onJugar, Runnable onLeaderboard, Runnable onOpciones, Runnable onExit) {
        setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));
        setBackground(new Color(18, 22, 38));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("SKY DEFENSE");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 52));
        titulo.setForeground(new Color(80, 200, 255));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(titulo);
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(crearBoton("Jugar", onJugar));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(crearBoton("Leaderboard", onLeaderboard));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(crearBoton("Opciones", onOpciones));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(crearBoton("Exit", onExit));
        add(Box.createVerticalGlue());

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    // Crea un botón con su texto y la acción que ejecuta al hacer clic.
    private JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 20));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(240, 44));
        boton.setFocusable(false);
        boton.addActionListener(e -> accion.run());
        return boton;
    }
}
