package Model;

// Parámetros del mundo de juego. El modelo trabaja en METROS (como la consigna);
// la Vista convierte a píxeles con la misma escala en ambos ejes.
public final class Config {

    private Config() { }

    // Pantalla
    public static final int ANCHO_PX = 1000;
    public static final int ALTO_PX  = 780;

    // Tiempo del bucle de juego.
    public static final int MS_POR_TICK = 30; // duración de cada tick (~33 fps)
    // Intervalo mínimo entre misiles de un mismo dron: 1 segundo, en ticks.
    public static final int COOLDOWN_DISPARO_TICKS = Math.max(1, Math.round(1000f / MS_POR_TICK));

    // Escala única para ambos ejes (metros por píxel). Más chico = cámara más cerca
    // = objetos más grandes en pantalla (el rango de altitud debe seguir entrando).
    public static final double METROS_POR_PIXEL = 6.0;

    // Mundo en metros
    public static final double ANCHO_M     = ANCHO_PX * METROS_POR_PIXEL; // 6400 m de ancho

    // Altitudes (metros) — reglas de la consigna
    public static final double ALTITUD_MIN    = 1000;  // altitud mínima del avión
    public static final double ALTITUD_MAX    = 5000;  // altitud máxima del avión y altura de los drones
    public static final double DETONACION_MIN = 1200;  // altitud mínima de detonación de un misil
    public static final double DETONACION_MAX = 4500;  // altitud máxima de detonación de un misil

    // Distancias de DAÑO de la consigna (metros), exactas: >150 a salvo + puntos;
    // 80–150 -20%; 20–80 -40%; <20 pierde una vida. (ESCALA = 1.0 = consigna pura.)
    public static final double ESCALA_RADIO_EXPLOSION = 1.0;
    public static final double RADIO_DANIO      = 150 * ESCALA_RADIO_EXPLOSION; // daño; fuera, a salvo + puntos
    public static final double RADIO_DANIO_ALTO = 80  * ESCALA_RADIO_EXPLOSION; // daño mayor
    public static final double RADIO_CRITICO    = 20  * ESCALA_RADIO_EXPLOSION; // pierde una vida

    // Tamaño físico del avión (metros): la distancia de daño se mide al CUERPO de la
    // nave, no a su punto central, porque la aeronave ocupa espacio (hitbox).
    public static final double RADIO_AVION = 130;

    // Tamaño físico del misil (metros): radio de su cuerpo para detectar el impacto
    // directo contra la nave. Si el misil toca el hitbox del avión, detona en el acto.
    public static final double RADIO_MISIL = 60;

    // Escala SOLO visual de la explosión (no afecta el daño): la bola de fuego se
    // dibuja más grande que el radio de daño para que se vea dramática.
    public static final double ESCALA_VISUAL_EXPLOSION = 2.5;

    // Margen superior en píxeles: deja una franja arriba para el HUD, de modo que los
    // drones (que vuelan a la altitud máxima) queden por debajo de la barra superior.
    private static final int MARGEN_SUP_PX = 70;

    // Conversión horizontal: metros -> píxel.
    public static int xAPixel(double xMetros) {
        return (int) (xMetros / METROS_POR_PIXEL);
    }

    // Conversión vertical: una altitud mayor se dibuja más arriba (y menor).
    public static int altitudAPixel(double altitudMetros) {
        return (int) (MARGEN_SUP_PX + (ALTITUD_MAX - altitudMetros) / METROS_POR_PIXEL);
    }
}
