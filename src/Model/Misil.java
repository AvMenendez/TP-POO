package Model;


public class Misil extends ObjetoVolador {
	private int altitudDetonacion;
	private double velocidadCaida;
	
	//el Constructor para el misil.
	/*El constructor es modificable para que los valores de altitudDetonacion y velocidadCaida sean alterables en lugar de constantes. 
	 * Decidan ustedes que yo lo implemento*/
	public Misil(float posicionX, float posicionY) {
        super(posicionX, posicionY);
        this.altitudDetonacion = 50;   
        this.velocidadCaida    = 3.0; 
	}
	public void descender() {
		float deltaY = (float) velocidadCaida;
		actualizarPosicion (0, deltaY);	
	}
	
	public boolean evaluarDetonacion(Avion a) {
        boolean altitudAlcanzada = getPosicionY() >= altitudDetonacion;
        double  distancia = calcularDistanciaAlAvion(a);
        //Decidí el radio de explosión en base a las vibes del día (Desp definimos bien que radio ponerle)
        final double RadioExplosion = 30.0;
 
        return altitudAlcanzada || distancia <= RadioExplosion;
    }
	
	public double calcularDistanciaAlAvion(Avion a) {
		double dx = getPosicionX() - a.getPosicionX();
		double dy = getPosicionY() - a.getPosicionY();
		return Math.sqrt(dx * dx + dy);
	}


	public int getAltitudDetonacion() {
		return altitudDetonacion;
	}

	public void setAltitudDetonacion(int altitudDetonacion) {
		this.altitudDetonacion = altitudDetonacion;
	}

	public double getVelocidadCaida() {
		return velocidadCaida;
	}

	public void setVelocidadCaida(double velocidadCaida) {
		this.velocidadCaida = velocidadCaida;
	}
}