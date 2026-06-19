package Views;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;

// Pide el nombre del jugador antes de comenzar la partida.
public class PanelNombre extends JPanel {

    private final JTextField campoNombre = new JTextField();

    // Arma la pantalla con el campo de nombre y los botones de comenzar/volver.
    public PanelNombre(Consumer<String> onComenzar, Runnable onVolver) {
        setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));
        setBackground(new Color(18, 22, 38));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("¿Quién será el que marcará un nuevo récord?");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setForeground(new Color(80, 200, 255));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Ingresá tu nombre, piloto:");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitulo.setForeground(Color.LIGHT_GRAY);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        campoNombre.setMaximumSize(new Dimension(280, 38));
        campoNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoNombre.setFont(new Font("SansSerif", Font.PLAIN, 20));
        campoNombre.setHorizontalAlignment(JTextField.CENTER);

        JButton comenzar = new JButton("¡Despegar!");
        comenzar.setFont(new Font("SansSerif", Font.BOLD, 20));
        comenzar.setAlignmentX(Component.CENTER_ALIGNMENT);
        comenzar.setFocusable(false);

        JButton volver = new JButton("Volver");
        volver.setFont(new Font("SansSerif", Font.PLAIN, 16));
        volver.setAlignmentX(Component.CENTER_ALIGNMENT);
        volver.setFocusable(false);
        volver.addActionListener(e -> onVolver.run());

        // Confirmar con el botón o con Enter dentro del campo.
        Runnable confirmar = () -> onComenzar.accept(nombreIngresado());
        comenzar.addActionListener(e -> confirmar.run());
        campoNombre.addActionListener(e -> confirmar.run());

        add(Box.createVerticalGlue());
        add(titulo);
        add(Box.createRigidArea(new Dimension(0, 28)));
        add(subtitulo);
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(campoNombre);
        add(Box.createRigidArea(new Dimension(0, 28)));
        add(comenzar);
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(volver);
        add(Box.createVerticalGlue());

        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
    }

    // Devuelve el nombre escrito, o "Anónimo" si quedó vacío.
    private String nombreIngresado() {
        String nombre = campoNombre.getText().trim();
        return nombre.isEmpty() ? "Anónimo" : nombre;
    }

    // Al mostrarse la pantalla, pone el cursor directamente en el campo de nombre.
    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(campoNombre::requestFocusInWindow);
    }
}
