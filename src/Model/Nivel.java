package Model;
public class Nivel {
	private int numeroNivel;
	private double multiplicadorDificultad;
	
	public Nivel(int numeroNivel, double multiplicadorDificultad) {
		this.numeroNivel = numeroNivel;
		this.multiplicadorDificultad = multiplicadorDificultad; 
	}
	
	public void avanzarNivel() {
		this.numeroNivel++;
		this.multiplicadorDificultad *= 1.2; //incrementa en 20% la diff, decidido a ojimetro.
		
	}
	

	public double getMultiplicadorDificultad() {
		return multiplicadorDificultad;
	}

	public int getNumeroNivel() {
		return numeroNivel;
	}

	public void setNumeroNivel(int numeroNivel) {
		this.numeroNivel = numeroNivel;
	}

	public void setMultiplicadorDificultad(double multiplicadorDificultad) {
		this.multiplicadorDificultad = multiplicadorDificultad;
	}
	
	

}