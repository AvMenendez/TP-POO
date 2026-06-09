package Model;

public abstract class ObjetoVolador {
	protected float posicionX;
	protected float posicionY;
	
	
	
	public ObjetoVolador(float posicionX, float posicionY) {
		this.posicionX = posicionX;
		this.posicionY = posicionY;
	}
	
	//getters de posicion x e y
	public float getPosicionX() {
		return posicionX;
	}
	public float getPosicionY() {
		return posicionY;
	}
	
	//funcion para actualizar posicion. 
	protected void actualizarPosicion(float deltaX, float deltaY) {
        this.posicionX += deltaX;
        this.posicionY += deltaY;
    }
}
