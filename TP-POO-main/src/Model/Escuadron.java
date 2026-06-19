package Model;

import java.util.ArrayList;
import java.util.List;


public class Escuadron {

    private int        dronesRestantes;
    private int        numeroEscuadron;
    private List<Dron> drones;


    // Crea el escuadrón con su número y la cantidad de drones que le quedan por enviar.
     public Escuadron(int numeroEscuadron, int dronesRestantes) {
        this.numeroEscuadron = numeroEscuadron;
        this.dronesRestantes = dronesRestantes;
        this.drones          = new ArrayList<>();
    }

    // Mueve todos los drones, quita los que salieron y devuelve los misiles disparados.
    public List<Misil> actualizar() {
        List<Misil> misilesGenerados = new ArrayList<>();
        List<Dron>  dronesAEliminar  = new ArrayList<>();

        for (Dron dron : drones) {
            dron.desplazarse();

            if (dron.estaFueraDePantalla()) {
                dronesAEliminar.add(dron);
                continue;
            }

            Misil misil = dron.evaluarDisparo();
            if (misil != null) {
                misilesGenerados.add(misil);
            }
        }

        for (Dron dron : dronesAEliminar) {
            eliminarDron(dron);
        }

        return misilesGenerados;
    }

 
    // Devuelve cuántos drones faltan por enviar a la pantalla.
    public int getDronesRestantes() {
        return dronesRestantes;
    }

    // Indica si ya no quedan drones por enviar ni en pantalla (nivel superado).
    public boolean estaDestruido() {
        return dronesRestantes == 0 && drones.isEmpty();
    }

    // Agrega un dron a la pantalla y descuenta uno de los que quedaban.
    public void agregarDron(Dron dron) {
        if (dronesRestantes > 0) {
            drones.add(dron);
            dronesRestantes--;
        }
    }

    // Quita un dron de la pantalla.
    public void eliminarDron(Dron dron) {
        drones.remove(dron);
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    // Devuelve el número de escuadrón.
    public int getNumeroEscuadron() {
        return numeroEscuadron;
    }

    // Fija el número de escuadrón.
    public void setNumeroEscuadron(int numeroEscuadron) {
        this.numeroEscuadron = numeroEscuadron;
    }

    // Fija cuántos drones quedan por enviar.
    public void setDronesRestantes(int dronesRestantes) {
        this.dronesRestantes = dronesRestantes;
    }

    // Devuelve la lista de drones que están en pantalla.
    public List<Dron> getDrones() {
        return drones;
    }

   
}