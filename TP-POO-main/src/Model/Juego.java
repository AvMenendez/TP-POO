package Model;
import java.util.ArrayList;
import java.util.List;

public class Juego {

    public  static final int TAM_ESCUADRON  = 10;   // unidades por escuadrón (consigna)
    public  static final int NIVEL_MAX      = 15;   // tope de niveles: al superarlo, se gana el juego
    private static final int DRONES_ACTIVOS = 4;    // máximo de drones simultáneos en pantalla

    // Distancia mínima (m) que debe haber avanzado el último dron antes de que entre el
    // siguiente por ese mismo extremo. Igual a ANCHO_M / DRONES_ACTIVOS para que los 4
    // queden REPARTIDOS a lo ancho de la pantalla (no amontonados en "dúos" junto a los
    // bordes, lo que dejaba grandes zonas sin drones ni misiles).
    private static final double SEPARACION_SPAWN = Config.ANCHO_M / DRONES_ACTIVOS;

    // Pequeña espera ALEATORIA (en ticks) que se suma al espaciado por posición, para que
    // el instante exacto de entrada no sea perfectamente regular (a 30 ms/tick: ~0,3–1,2 s).
    private static final int ESPERA_SPAWN_MIN = 10;
    private static final int ESPERA_SPAWN_MAX = 40;

    // Velocidades base (en metros por tick) del nivel 1; escalan +15% por nivel. Son
    // valores propios (la consigna fija el escalado por nivel, no el arranque).
    private static final double DRON_VEL_BASE       = 22.0;
    private static final double MISIL_VEL_BASE      = 32.0;
    private static final double FREC_DISPARO_BASE   = 0.04;

    // Puntaje
    private static final int PUNTOS_NIVEL = 300;   // por superar un nivel
    private static final int PUNTOS_LEJOS = 40;    // explosión a > 150 m
    private static final int PUNTOS_MEDIA = 20;    // explosión entre 80 y 150 m

    // Daño según distancia de la explosión (metros) y % de energía perdido
    private static final double DIST_LEJOS  = Config.RADIO_DANIO;      // 150
    private static final double DIST_MEDIA  = Config.RADIO_DANIO_ALTO; // 80
    private static final double DIST_CERCA  = Config.RADIO_CRITICO;    // 20
    private static final int    DANIO_MEDIA = 20;  // entre 80 y 150 m
    private static final int    DANIO_CERCA = 40;  // entre 20 y 80 m
    private static final float  ENERGIA_MAXIMA = 100f;

    // Atributos
    private Jugador         jugadorActual;
    private Nivel           nivelActual;
    private Escuadron       escuadronActual;
    private List<Misil>     misilesActivos;
    private List<Explosion> explosionesActivas;
    private EstadoJuego     estado;
    private int             ticksParaProximoSpawn; // cuenta regresiva hasta poder aparecer el próximo dron


    // Crea la partida con el jugador, el nivel y el escuadrón iniciales.
    public Juego(Jugador jugadorActual, Nivel nivelActual, Escuadron escuadronActual) {
        this.jugadorActual      = jugadorActual;
        this.nivelActual        = nivelActual;
        this.escuadronActual    = escuadronActual;
        this.misilesActivos     = new ArrayList<>();
        this.explosionesActivas = new ArrayList<>();
        this.estado             = EstadoJuego.JUGANDO;
    }


    // Prepara una partida nueva: limpia misiles/explosiones y crea los primeros drones.
    public void iniciarJuego() {
        misilesActivos.clear();
        explosionesActivas.clear();
        estado = EstadoJuego.JUGANDO;
        controlarSpawnDrones();
    }


    // Avanza un tick del juego: mueve todo, resuelve explosiones y revisa si se ganó/perdió.
    public void actualizarJuego() {
        if (estado != EstadoJuego.JUGANDO) {
            return;
        }

        Avion avion = jugadorActual.getAvionActivo();

        // El escuadrón mueve sus drones y devuelve los misiles que dispararon.
        misilesActivos.addAll(escuadronActual.actualizar());

        // Los misiles descienden y explotan al alcanzar su altitud de detonación programada
        // O al tocar la nave (no se los puede atravesar). En ambos casos dejan una explosión
        // que daña por DISTANCIA (tabla de la consigna): un choque cercano aplica el daño
        // según qué tan cerca explota, no un golpe fijo.
        List<Misil> misilesAEliminar = new ArrayList<>();
        for (Misil misil : misilesActivos) {
            misil.descender();
            if (misil.haDetonado() || misil.impactaA(avion)) {
                explosionesActivas.add(new Explosion(misil.getPosicionX(), misil.getPosicionY()));
                misilesAEliminar.add(misil);
            }
        }
        misilesActivos.removeAll(misilesAEliminar);

        actualizarExplosiones(avion);

        controlarSpawnDrones();
        verificarCondicionVictoria();
    }

    // Cada explosión vive unos ticks. El avión es afectado si entra en el radio de
    // daño mientras la explosión está activa; si nunca se acerca, se considera a salvo.
    private void actualizarExplosiones(Avion avion) {
        List<Explosion> aEliminar = new ArrayList<>();
        for (Explosion exp : explosionesActivas) {
            exp.envejecer();

            if (!exp.estaResuelta()) {
                double distancia = exp.distanciaAlAvion(avion);
                if (distancia <= DIST_LEJOS) {          // entró en la zona de daño
                    aplicarDanioPorDistancia(distancia, avion);
                    exp.marcarResuelta();
                } else if (exp.terminada()) {           // estuvo siempre lejos: a salvo + puntos
                    jugadorActual.sumarPuntos(PUNTOS_LEJOS);
                    exp.marcarResuelta();
                }
            }

            if (exp.terminada()) {
                aEliminar.add(exp);
            }
        }
        explosionesActivas.removeAll(aEliminar);
    }

    // Daño y puntaje según la distancia entre la explosión y el avión (consigna).
    private void aplicarDanioPorDistancia(double distancia, Avion avion) {
        if (distancia >= DIST_MEDIA) {            // 80–150 m: puntos y -20% energía
            jugadorActual.sumarPuntos(PUNTOS_MEDIA);
            daniarAvion(avion, DANIO_MEDIA);
        } else if (distancia >= DIST_CERCA) {     // 20–80 m: sin puntos y -40% energía
            daniarAvion(avion, DANIO_CERCA);
        } else {                                  // < 20 m: pierde una vida
            jugadorActual.modificarVidas(-1);
        }
    }

    // Aplica daño de energía; si se agota, el avión pierde una vida y recupera energía.
    private void daniarAvion(Avion avion, int porcentaje) {
        avion.recibirDanio(porcentaje);
        if (!avion.estaActivo()) {
            jugadorActual.modificarVidas(-1);
            avion.setEnergia(ENERGIA_MAXIMA);
        }
    }


    // Mantiene hasta DRONES_ACTIVOS en pantalla; cada dron entra por un extremo aleatorio.
    // Las entradas son a TIEMPOS ALEATORIOS: entra un dron, y el siguiente recién aparece
    // tras una espera al azar (entre ESPERA_SPAWN_MIN y MAX ticks). Además, el extremo de
    // entrada debe estar libre (si está ocupado se prueba el otro; si ambos lo están, se
    // reintenta el próximo tick), para que nunca se apilen dos drones en el mismo punto.
    public void controlarSpawnDrones() {
        if (escuadronActual.getDrones().size() >= DRONES_ACTIVOS
                || escuadronActual.getDronesRestantes() == 0) {
            return;                                 // ya hay 4 (o no quedan drones por enviar)
        }
        if (ticksParaProximoSpawn > 0) {
            ticksParaProximoSpawn--;                // todavía esperando para el próximo dron
            return;
        }

        boolean desdeIzquierda = Math.random() < 0.5;
        if (!entradaLibre(desdeIzquierda)) {
            desdeIzquierda = !desdeIzquierda;       // prueba el extremo opuesto
            if (!entradaLibre(desdeIzquierda)) {
                return;                             // ambos ocupados: reintentar próximo tick
            }
        }

        double mult       = nivelActual.getMultiplicadorDificultad();
        double velocidad  = DRON_VEL_BASE     * mult;
        double frecuencia = FREC_DISPARO_BASE * mult;
        double velMisil   = MISIL_VEL_BASE    * mult;
        // El intervalo entre misiles también escala con el nivel (mínimo 1 tick): a más
        // dificultad, menos cooldown, así los drones rápidos siguen disparando seguido.
        int cooldown = Math.max(1, (int) Math.round(Config.COOLDOWN_DISPARO_TICKS / mult));

        float xSpawn    = desdeIzquierda ? 0f : (float) Config.ANCHO_M;
        int   direccion = desdeIzquierda ? 1 : -1;

        Dron nuevoDron = new Dron(xSpawn, (float) Config.ALTITUD_MAX,
                direccion, velocidad, frecuencia, velMisil, cooldown);
        escuadronActual.agregarDron(nuevoDron);

        // Programa la espera aleatoria hasta el siguiente dron.
        ticksParaProximoSpawn = ESPERA_SPAWN_MIN
                + (int) (Math.random() * (ESPERA_SPAWN_MAX - ESPERA_SPAWN_MIN + 1));
    }

    // ¿El extremo de entrada está libre? (no hay ningún dron a menos de SEPARACION_SPAWN
    // metros), para no hacer aparecer un dron encima de otro.
    private boolean entradaLibre(boolean desdeIzquierda) {
        double xEntrada = desdeIzquierda ? 0.0 : Config.ANCHO_M;
        for (Dron dron : escuadronActual.getDrones()) {
            if (Math.abs(dron.getPosicionX() - xEntrada) < SEPARACION_SPAWN) {
                return false;
            }
        }
        return true;
    }


    public void verificarCondicionVictoria() {
        if (!jugadorActual.estaVivo()) {
            estado = EstadoJuego.GAME_OVER;
            return;
        }

        if (escuadronActual.estaDestruido()) {
            jugadorActual.sumarPuntos(PUNTOS_NIVEL);

            // Tope de niveles: al superar el último nivel, el jugador gana el juego.
            if (nivelActual.getNumeroNivel() >= NIVEL_MAX) {
                estado = EstadoJuego.VICTORIA;
                return;
            }

            nivelActual.avanzarNivel();
            escuadronActual = new Escuadron(nivelActual.getNumeroNivel(), TAM_ESCUADRON);
            controlarSpawnDrones();
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    // Devuelve el estado de la partida (jugando o game over).
    public EstadoJuego getEstado() {
        return estado;
    }

    // Devuelve el jugador actual.
    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    // Devuelve el nivel actual.
    public Nivel getNivelActual() {
        return nivelActual;
    }

    // Devuelve el escuadrón de drones actual.
    public Escuadron getEscuadronActual() {
        return escuadronActual;
    }

    // Devuelve los misiles que están cayendo.
    public List<Misil> getMisilesActivos() {
        return misilesActivos;
    }

    // Devuelve las explosiones que están activas.
    public List<Explosion> getExplosionesActivas() {
        return explosionesActivas;
    }

}
