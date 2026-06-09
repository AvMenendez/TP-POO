package Model;

public class Avion extends ObjetoVolador{
	private float energia;
	
	public Avion(float posicionX, float posicionY, float energia) {
		super (posicionX, posicionY);
		this.energia = energia; 
	}
	
	public void desplazarLateral(int deltaX) {
		actualizarPosicion(deltaX, 0);
	}
	
	public void variarAltitud(int nuevaY) {
		float deltaY = nuevaY - getPosicionY() ;
		actualizarPosicion(0, deltaY);
	}
	
	public void recibirDanio(int porcentaje) {
		float danio = (porcentaje / 100.0f) * 100.0f;
		this.energia = Math.max(0, this.energia - danio);
	}
	
	public boolean estaActivo() {
		return this.energia>0;
	}

	public float getEnergia() {
		return energia;
	}

	public void setEnergia(float energia) {
		this.energia = energia;
	}
	
	
}

