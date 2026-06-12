package Model;


public class Jugador {
	
	private String nombreUsuario;
	private int vidas; 
	private int puntaje;
	private Avion avionActivo	;
	
	
	public Jugador(String nombreUsuario, int vidas, Avion avionActivo) {
		this.nombreUsuario = nombreUsuario;
		this.vidas = vidas;
		this.puntaje = 0;
		this.avionActivo = avionActivo;
	}
	
	public void modificarVidas(int cantidad) {
		   this.vidas = Math.max(0, this.vidas + cantidad);
	}
	

	
	public void sumarPuntos(int cantidad) {
		this.puntaje = Math.max(0, this.puntaje + cantidad);
	}
	
	public boolean estaVivo() {
		return this.vidas > 0;
	}

	//getters y setters
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public int getVidas() {
		return vidas;
	}

	public void setVidas(int vidas) {
		this.vidas = vidas;
	}

	public int getPuntaje() {
		return puntaje;
	}

	public void setPuntaje(int puntaje) {
		this.puntaje = puntaje;
	}

	public Avion getAvionActivo() {
		return avionActivo;
	}

	public void setAvionActivo(Avion avionActivo) {
		this.avionActivo = avionActivo;
	}
	
	
}
