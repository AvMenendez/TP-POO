package Model;
import java.util.ArrayList;
import java.util.List;

public class Juego {

    public  static final int TAM_ESCUADRON  = 10;   // unidades por escuadrón (consigna)
    private static final int DRONES_ACTIVOS = 4;    // máximo de drones simultáneos en pantalla

    // Velocidades base (en metros por tick); escalan con el multiplicador del nivel.
    private static final double DRON_VEL_BASE       = 16.0;
    private static final double MISIL_VEL_BASE      = 24.0;
    private static final double FREC_DISPARO_BASE   = 0.01;

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

        // Los misiles descienden; al alcanzar su altitud de detonación, explotan
        // dejando una explosión en ese punto (área de daño que dura unos instantes).
        // Un misil detona si llega a su altitud programada O si choca contra la nave:
        // al tocar el hitbox del avión explota en el acto (no se lo puede atravesar).
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
    public void controlarSpawnDrones() {
        while (escuadronActual.getDrones().size() < DRONES_ACTIVOS
                && escuadronActual.getDronesRestantes() > 0) {
            double mult       = nivelActual.getMultiplicadorDificultad();
            double velocidad  = DRON_VEL_BASE     * mult;
            double frecuencia = FREC_DISPARO_BASE * mult;
            double velMisil   = MISIL_VEL_BASE    * mult;

            boolean desdeIzquierda = Math.random() < 0.5;
            float xSpawn    = desdeIzquierda ? 0f : (float) Config.ANCHO_M;
            int   direccion = desdeIzquierda ? 1 : -1;

            Dron nuevoDron = new Dron(xSpawn, (float) Config.ALTITUD_MAX,
                    direccion, velocidad, frecuencia, velMisil);
            escuadronActual.agregarDron(nuevoDron);
        }
    }


    public void verificarCondicionVictoria() {
        if (!jugadorActual.estaVivo()) {
            estado = EstadoJuego.GAME_OVER;
            return;
        }

        if (escuadronActual.estaDestruido()) {
            jugadorActual.sumarPuntos(PUNTOS_NIVEL);
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
