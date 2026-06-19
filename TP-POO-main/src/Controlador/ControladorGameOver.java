package Controlador;

import Views.PanelGameOver;

import javax.swing.JPanel;

// Arma la pantalla de Game Over y la conecta con las acciones de reiniciar / menú / salir.
public class ControladorGameOver {

    // Crea el panel de Game Over con el puntaje y los botones de reiniciar/menú/salir.
    public JPanel crearPanel(int puntaje, Runnable onReiniciar, Runnable onMenu, Runnable onSalir) {
        return new PanelGameOver(puntaje, onReiniciar, onMenu, onSalir);
    }
}
