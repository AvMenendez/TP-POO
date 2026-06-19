package Model;

// Estado global de la partida; lo usa la capa de presentación para saber
// cuándo detener el bucle y mostrar la pantalla de Game Over.
public enum EstadoJuego {
    JUGANDO,
    GAME_OVER
}
