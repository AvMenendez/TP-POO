package Model;

import java.util.ArrayList;
import java.util.List;


public class Escuadron {

    private int        dronesRestantes;
    private int        numeroEscuadron;
    private List<Dron> drones;


    // Constructor
     public Escuadron(int numeroEscuadron, int dronesRestantes) {
        this.numeroEscuadron = numeroEscuadron;
        this.dronesRestantes = dronesRestantes;
        this.drones          = new ArrayList<>();
    }

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

 
    public int gestDronesRestantes() {
        return dronesRestantes;
    }
    
    public boolean estaDestruido() {
        return dronesRestantes == 0 && drones.isEmpty();
    }

    public void agregarDron(Dron dron) {
        if (dronesRestantes > 0) {
            drones.add(dron);
            dronesRestantes--;
        }
    }

    public void eliminarDron(Dron dron) {
        drones.remove(dron);
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    public int getNumeroEscuadron() {
        return numeroEscuadron;
    }

    public void setNumeroEscuadron(int numeroEscuadron) {
        this.numeroEscuadron = numeroEscuadron;
    }

    public void setDronesRestantes(int dronesRestantes) {
        this.dronesRestantes = dronesRestantes;
    }

    public List<Dron> getDrones() {
        return drones;
    }

   
}