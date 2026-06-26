package Model;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
public class GestorSonido {
    private static Clip musicaFondo;


    public static void reproducir(String rutaRelativa) {
        try {

            URL url = GestorSonido.class.getResource(rutaRelativa);

            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } else {
                System.out.println("Archivo no encontrado: " + rutaRelativa);
            }
        } catch (Exception e) {
            System.out.println("Error al reproducir: " + e.getMessage());
        }
    }

    public static void reproducirMusicaEnLoop(String rutaRelativa) {
        try {
            detenerMusica(); // Frena y cierra el canal anterior
            URL url = GestorSonido.class.getResource(rutaRelativa);

            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                musicaFondo = AudioSystem.getClip();
                musicaFondo.open(audioIn);

                musicaFondo.setFramePosition(0);
                musicaFondo.loop(Clip.LOOP_CONTINUOUSLY); // Arranca y repite automáticamente

                System.out.println("Música loopeando correctamente: " + rutaRelativa);
            } else {
                System.out.println("Error de ruta de loop: " + rutaRelativa);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Esto va a imprimir el error técnico en rojo si algo falla
        }
    }

    public static void detenerMusica() {
        if (musicaFondo != null) {
            if (musicaFondo.isRunning()) {
                musicaFondo.stop();
            }
            musicaFondo.close(); // ¡Línea crítica para liberar memoria!
        }
    }





}


