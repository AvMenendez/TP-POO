package Model;


public class Jugador {

	private static final int PUNTOS_POR_VIDA_EXTRA = 1000;

	private String nombreUsuario;
	private int vidas;
	private int puntaje;
	private int vidasExtraOtorgadas; // cuántas vidas extra ya se entregaron por puntaje
	private Avion avionActivo;


	// Crea el jugador con su nombre, vidas y la nave que controla.
	public Jugador(String nombreUsuario, int vidas, Avion avionActivo) {
		this.nombreUsuario = nombreUsuario;
		this.vidas = vidas;
		this.puntaje = 0;
		this.vidasExtraOtorgadas = 0;
		this.avionActivo = avionActivo;
	}

	// Suma o resta vidas (sin bajar de 0).
	public void modificarVidas(int cantidad) {
		   this.vidas = Math.max(0, this.vidas + cantidad);
	}



	// Suma puntos y otorga una vida extra cada 1000 puntos.
	public void sumarPuntos(int cantidad) {
		this.puntaje = Math.max(0, this.puntaje + cantidad);
		// Cada vez que se cruza un múltiplo de 1000 puntos se otorga una vida extra.
		while (puntaje >= (vidasExtraOtorgadas + 1) * PUNTOS_POR_VIDA_EXTRA) {
			vidasExtraOtorgadas++;
			modificarVidas(1);
		}
	}
	
	// Indica si al jugador todavía le quedan vidas.
	public boolean estaVivo() {
		return this.vidas > 0;
	}

	// Devuelve el nombre del jugador.
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	// Fija el nombre del jugador.
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	// Devuelve las vidas actuales.
	public int getVidas() {
		return vidas;
	}

	// Fija las vidas.
	public void setVidas(int vidas) {
		this.vidas = vidas;
	}

	// Devuelve el puntaje actual.
	public int getPuntaje() {
		return puntaje;
	}

	// Fija el puntaje.
	public void setPuntaje(int puntaje) {
		this.puntaje = puntaje;
	}

	// Devuelve la nave que el jugador está usando.
	public Avion getAvionActivo() {
		return avionActivo;
	}

	// Fija la nave que el jugador está usando.
	public void setAvionActivo(Avion avionActivo) {
		this.avionActivo = avionActivo;
	}
	
	
}
