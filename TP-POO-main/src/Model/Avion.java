package Model;

public class Avion extends ObjetoVolador{
	private float energia;
	
	// Crea la nave con su posición, altitud y energía iniciales.
	public Avion(float posicionX, float posicionY, float energia) {
		super (posicionX, posicionY);
		this.energia = energia;
	}

	// Mueve la nave a izquierda/derecha.
	public void desplazarLateral(int deltaX) {
		actualizarPosicion(deltaX, 0);
	}

	// Cambia la altitud de la nave hacia una nueva altura.
	public void variarAltitud(int nuevaY) {
		float deltaY = nuevaY - getPosicionY() ;
		actualizarPosicion(0, deltaY);
	}

	// Resta energía a la nave (sin bajar de 0).
	public void recibirDanio(int porcentaje) {
		this.energia = Math.max(0, this.energia - porcentaje);
	}

	// Indica si la nave todavía tiene energía.
	public boolean estaActivo() {
		return this.energia>0;
	}

	// Radio (en metros) del cuerpo de la nave, para medir distancias de daño al hitbox.
	public float getRadio() {
		return (float) Config.RADIO_AVION;
	}

	// Devuelve la energía actual de la nave.
	public float getEnergia() {
		return energia;
	}

	// Fija la energía de la nave (se usa al reponerla tras perder una vida).
	public void setEnergia(float energia) {
		this.energia = energia;
	}
	
	
}

