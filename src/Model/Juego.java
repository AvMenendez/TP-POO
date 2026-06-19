package Model;
import java.util.ArrayList;
import java.util.List;
public class Juego {

    // Atributos privados
    private Jugador     jugadorActual;
    private Nivel       nivelActual;
    private Escuadron   escuadronActual;
    private List<Misil> misilesActivos;

    
    public Juego(Jugador jugadorActual, Nivel nivelActual, Escuadron escuadronActual) {
        this.jugadorActual   = jugadorActual;
        this.nivelActual     = nivelActual;
        this.escuadronActual = escuadronActual;
        this.misilesActivos  = new ArrayList<>();
    }

    
    public void iniciarJuego() {
        misilesActivos.clear();
        controlarSpawnDrones();
        System.out.println("Juego iniciado. " + nivelActual);
    }

    
    public void actualizarJuego() {
        Avion avion = jugadorActual.getAvionActivo();

        // Escuadron mueve sus drones y devuelve los misiles que dispararon
        List<Misil> misilesNuevos = escuadronActual.actualizar();
        misilesActivos.addAll(misilesNuevos);

        // Actualizar misiles activos en pantalla
        List<Misil> misilesAEliminar = new ArrayList<>();
        for (Misil misil : misilesActivos) {
            misil.descender();

            if (misil.evaluarDetonacion(avion)) {
                avion.recibirDanio(10); // Cada impacto quita 10%
                misilesAEliminar.add(misil);
            }
        }
        misilesActivos.removeAll(misilesAEliminar);

        // Spawn y verificación de victoria
        controlarSpawnDrones();
        verificarCondicionVictoria();
    }

   
    public void controlarSpawnDrones() {
        if (escuadronActual.getDrones().isEmpty() && !escuadronActual.estaDestruido()) {
            double multiplicador     = nivelActual.getMultiplicadorDificultad();
            double velocidad         = 2.0 * multiplicador;
            double frecuenciaDisparo = 0.01 * multiplicador;

            Dron nuevoDron = new Dron(100, 50, 1, velocidad, frecuenciaDisparo);
            escuadronActual.agregarDron(nuevoDron);

            System.out.println("Nuevo dron spawneado. " + escuadronActual);
        }
    }

   
    public void verificarCondicionVictoria() {
        if (!jugadorActual.estaVivo()) {
            System.out.println("Game Over. Puntaje final: " + jugadorActual.getPuntaje());
            return;
        }

        if (escuadronActual.estaDestruido()) {
            System.out.println("Nivel " + nivelActual.getNumeroNivel() + " completado.");
            jugadorActual.sumarPuntos(100);
            nivelActual.avanzarNivel();

            int nuevosDrones = nivelActual.getNumeroNivel() * 3;
            escuadronActual  = new Escuadron(nivelActual.getNumeroNivel(), nuevosDrones);
            controlarSpawnDrones();
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    public Nivel getNivelActual() {
        return nivelActual;
    }

    public Escuadron getEscuadronActual() {
        return escuadronActual;
    }

    public List<Misil> getMisilesActivos() {
        return misilesActivos;
    }

}