package Model;

public class Dron extends ObjetoVolador{
	
	private int direccionHorizontal; //acoté "direcciónHorizontal" al nombre que tiene ahora porque era largo
	private double velocidadMovimiento; //Lo mismo acá. 
	private double frecuenciaDisparo;
	
	
    public Dron(float posicionX, float posicionY,
    	int direccionHorizontal, double velocidadMovimiento, double frecuenciaDisparo) {
    	
    	super(posicionX, posicionY);
    	this.direccionHorizontal = direccionHorizontal;
    	this.velocidadMovimiento = velocidadMovimiento;
    	this.frecuenciaDisparo = frecuenciaDisparo;
}

	public void desplazarse() {
		float deltaX = (float) (direccionHorizontal * velocidadMovimiento);
		actualizarPosicion(deltaX, 0);
	}
	
	public Misil evaluarDisparo() {
		if (Math.random() < frecuenciaDisparo) {
			return new Misil(getPosicionX(), getPosicionY());
		}
		return null;
	}
	
    public boolean estaFueraDePantalla() {
        // Hay que ver el tema limites
        final float LimIzquierdo = 0f;
        final float LimDerecho  = 800f;
        final float LimSuperior  = 0f;
        final float LimInferior  = 600f;
 
        float x = getPosicionX();
        float y = getPosicionY();
 
        return x < LimIzquierdo
            || x > LimDerecho
            || y < LimSuperior
            || y > LimInferior;
    }

	public int getDireccionHorizontal() {
		return direccionHorizontal;
	}

	public void setDireccionHorizontal(int direccionHorizontal) {
		this.direccionHorizontal = direccionHorizontal;
	}

	public double getVelocidadMovimiento() {
		return velocidadMovimiento;
	}

	public void setVelocidadMovimiento(double velocidadMovimiento) {
		this.velocidadMovimiento = velocidadMovimiento;
	}

	public double getFrecuenciaDisparo() {
		return frecuenciaDisparo;
	}

	public void setFrecuenciaDisparo(double frecuenciaDisparo) {
		this.frecuenciaDisparo = frecuenciaDisparo;
	}
    

}
