package Controlador;

import Model.Avion;
import Model.Config;
import Model.EstadoJuego;
import Model.Escuadron;
import Model.Juego;
import Model.Jugador;
import Model.Leaderboard;
import Model.Nivel;
import Views.MenuPrincipal;
import Views.PanelJuego;
import Views.PanelLeaderboard;
import Views.PanelNombre;
import Views.PanelOpciones;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

// Orquesta la partida: crea el modelo, ejecuta el game loop, traduce el teclado
// en movimiento del avión y navega entre menú / juego / leaderboard / opciones / game over.
public class ControladorJuego extends KeyAdapter {

    private static final int    DELAY_MS = Config.MS_POR_TICK; // duración de cada tick (~33 fps)
    private static final double VEL_HORIZONTAL = 56;    // m por tick
    private static final double VEL_ALTITUD    = 56;    // m por tick
    private static final int    ALTITUD_INICIAL = 1500; // metros

    private final JFrame frame = new JFrame("Sky Defense");
    private final ControladorGameOver controladorGameOver = new ControladorGameOver();
    private final Leaderboard leaderboard = new Leaderboard();

    private Juego      juego;
    private PanelJuego panelJuego;
    private Timer      timer;
    private String     nombreJugador = "Anónimo";

    private boolean izquierda, derecha, arriba, abajo;

    // Arma la ventana principal y muestra el menú inicial.
    public void iniciar() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        mostrarMenu();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Muestra el menú principal y detiene el juego si estaba corriendo.
    private void mostrarMenu() {
        detenerTimer();
        mostrar(new MenuPrincipal(this::mostrarPedirNombre, this::mostrarLeaderboard, this::mostrarOpciones, this::salir));
    }

    // Muestra la pantalla para ingresar el nombre y luego empieza la partida.
    private void mostrarPedirNombre() {
        mostrar(new PanelNombre(nombre -> {
            this.nombreJugador = nombre;
            nuevaPartida();
        }, this::mostrarMenu));
    }

    // Muestra la tabla de mejores puntajes.
    private void mostrarLeaderboard() {
        mostrar(new PanelLeaderboard(leaderboard.obtenerTop(), this::mostrarMenu));
    }

    // Muestra la pantalla de opciones.
    private void mostrarOpciones() {
        mostrar(new PanelOpciones(this::mostrarMenu));
    }

    // Crea una partida nueva (modelo, vista y bucle de juego) y la arranca.
    private void nuevaPartida() {
        izquierda = derecha = arriba = abajo = false;

        Avion avion         = new Avion((float) (Config.ANCHO_M / 2), ALTITUD_INICIAL, 100);
        Jugador jugador     = new Jugador(nombreJugador, 3, avion);
        Nivel nivel         = new Nivel(1, 1.0);
        Escuadron escuadron = new Escuadron(1, Juego.TAM_ESCUADRON);

        juego = new Juego(jugador, nivel, escuadron);
        juego.iniciarJuego();

        panelJuego = new PanelJuego(juego);
        panelJuego.addKeyListener(this);
        mostrar(panelJuego);

        detenerTimer();
        timer = new Timer(DELAY_MS, e -> tick());
        timer.start();
    }

    // Un paso del juego: mueve la nave, actualiza el modelo y redibuja.
    private void tick() {
        moverAvion();
        juego.actualizarJuego();
        panelJuego.repaint();

        EstadoJuego estado = juego.getEstado();
        if (estado != EstadoJuego.JUGANDO) {        // fin de partida: derrota o victoria
            detenerTimer();
            int puntaje = juego.getJugadorActual().getPuntaje();
            leaderboard.registrar(juego.getJugadorActual().getNombreUsuario(), puntaje);
            mostrarFin(puntaje, estado == EstadoJuego.VICTORIA);
        }
    }

    // Mueve la nave según las teclas presionadas, sin salirse de los límites.
    private void moverAvion() {
        Avion avion = juego.getJugadorActual().getAvionActivo();
        float x   = avion.getPosicionX();   // horizontal (m)
        float alt = avion.getPosicionY();   // altitud (m)

        if (izquierda && x > 0)                  avion.desplazarLateral((int) -VEL_HORIZONTAL);
        if (derecha   && x < Config.ANCHO_M)     avion.desplazarLateral((int)  VEL_HORIZONTAL);
        // "Arriba" sube la altitud; "abajo" la baja, dentro del rango permitido.
        if (arriba    && alt < Config.ALTITUD_MAX)
            avion.variarAltitud((int) Math.min(Config.ALTITUD_MAX, alt + VEL_ALTITUD));
        if (abajo     && alt > Config.ALTITUD_MIN)
            avion.variarAltitud((int) Math.max(Config.ALTITUD_MIN, alt - VEL_ALTITUD));
    }

    // Muestra la pantalla de fin de partida (derrota o victoria) con el puntaje obtenido.
    private void mostrarFin(int puntaje, boolean gano) {
        mostrar(controladorGameOver.crearPanel(puntaje, gano, this::nuevaPartida, this::mostrarMenu, this::salir));
    }

    // Detiene el bucle de juego si está activo.
    private void detenerTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    // Cierra la aplicación.
    private void salir() {
        frame.dispose();
        System.exit(0);
    }

    // Cambia la pantalla que se ve en la ventana.
    private void mostrar(JComponent componente) {
        frame.setContentPane(componente);
        frame.revalidate();
        frame.repaint();
        componente.requestFocusInWindow();
    }

    // --- Entrada de teclado ---

    // Se llama al apretar una tecla: la marca como presionada.
    @Override
    public void keyPressed(KeyEvent e) {
        actualizarTecla(e.getKeyCode(), true);
    }

    // Se llama al soltar una tecla: la marca como no presionada.
    @Override
    public void keyReleased(KeyEvent e) {
        actualizarTecla(e.getKeyCode(), false);
    }

    // Activa o desactiva la dirección de movimiento según la tecla.
    private void actualizarTecla(int codigo, boolean presionada) {
        switch (codigo) {
            case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: izquierda = presionada; break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: derecha   = presionada; break;
            case KeyEvent.VK_UP:    case KeyEvent.VK_W: arriba    = presionada; break;
            case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: abajo     = presionada; break;
            default: /* otras teclas se ignoran */ break;
        }
    }
}
