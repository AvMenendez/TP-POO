# Sky Defense — Contexto del proyecto

Juego de defensa aérea (TP de POO). El jugador controla un avión que **esquiva**
los misiles que lanzan drones enemigos, avanzando de nivel a medida que sobrevive.
Hecho en **Java + Swing**, con estructura **MVC** (Model / Controlador / Views).

---

## Cómo compilar y ejecutar

Desde la carpeta del proyecto (`TP-POO-main/TP-POO-main`):

```powershell
# Compilar
javac -d bin (Get-ChildItem -Recurse -Filter *.java -Path src | % FullName)

# Ejecutar
java -cp bin Main
```

(O con el botón Run/▶ sobre `src/Main.java` en el IDE.)

---

## Estructura

```
src/
  Main.java                      Arranque (lanza el ControladorJuego en el EDT)
  Model/
    ObjetoVolador.java           Base: posición X (m) y Y = altitud (m)
    Avion.java                   Nave del jugador (energía, hitbox)
    Dron.java                    Enemigo (movimiento, disparo con cooldown)
    Misil.java                   Cae y detona a una altitud aleatoria
    Explosion.java               Área de daño que persiste unos instantes
    Escuadron.java               Grupo de 10 drones (máx 4 activos)
    Nivel.java                   Número de nivel y multiplicador de dificultad
    Jugador.java                 Nombre, vidas, puntaje, vida extra
    Juego.java                   Orquesta el modelo (loop, spawn, daño, victoria)
    EstadoJuego.java             enum: JUGANDO / GAME_OVER
    Leaderboard.java             Top 5 partidas, persistido en leaderboard.csv
    Config.java                  TODAS las constantes ajustables (ver abajo)
  Controlador/
    ControladorJuego.java        Game loop (Timer), teclado, navegación de pantallas
    ControladorGameOver.java     Arma la pantalla de Game Over
  Views/
    PanelJuego.java              Dibuja el juego (convierte metros -> píxeles)
    MenuPrincipal.java           Menú: Jugar / Leaderboard / Opciones / Exit
    PanelNombre.java             Pide el nombre antes de jugar
    PanelLeaderboard.java        Muestra el top 5
    PanelOpciones.java           Placeholder para configuraciones futuras
    PanelGameOver.java           Fin de partida (Reiniciar / Menú / Salir)
    Sprites.java                 Carga las imágenes desde Res/Sprites/
Res/Sprites/                     Imágenes de los objetos (PNG transparentes)
```

---

## Cumplimiento de la consigna

| Requisito | Estado | Dónde |
|---|---|---|
| Avión se mueve izq/der y varía altitud **1000–5000 m** | ✅ | `ControladorJuego.moverAvion`, `Config.ALTITUD_*` |
| Misiles caen en línea recta desde el dron | ✅ | `Misil.descender` |
| Detonación automática a altitud aleatoria **1200–4500 m** | ✅ | `Misil` (constructor) |
| Escuadrones de **10**, máx **4** activos a la vez | ✅ | `Juego.TAM_ESCUADRON`, `DRONES_ACTIVOS` |
| Cada dron entra por un **extremo aleatorio** | ✅ | `Juego.controlarSpawnDrones` |
| Sobrevivir → avanza de nivel | ✅ | `Juego.verificarCondicionVictoria` |
| Por nivel aumentan vel. drones, vel. caída misiles y frecuencia **+15%** | ✅ | `Nivel.INCREMENTO_POR_NIVEL` |
| Daño por distancia explosión↔avión (40/20/0 pts; 0/-20%/-40%; <20 vida) | ✅ | `Juego.aplicarDanioPorDistancia` |
| **+300** puntos por superar nivel | ✅ | `Juego.PUNTOS_NIVEL` |
| **Vida extra cada 1000** puntos | ✅ | `Jugador.sumarPuntos` |

Tabla de daño (consigna), medida al **cuerpo** del avión:
- **> 150 m** → +40 puntos, sin daño
- **80–150 m** → +20 puntos, −20% energía
- **20–80 m** → 0 puntos, −40% energía
- **< 20 m** → pierde una vida

---

## Decisiones de diseño (importante para defender el TP)

1. **Modelo en metros, vista en píxeles.** Todo el modelo trabaja en metros (como la
   consigna). La Vista convierte a píxeles con `Config` (misma escala en ambos ejes).
   `posicionX` = horizontal (m); `posicionY` = **altitud** (m, a mayor valor más alto).

2. **Detonación como explosión con área que persiste.** El misil detona a su altitud
   programada y deja una `Explosion` que vive ~0,45 s. El avión recibe el daño de la
   tabla si entra en el radio mientras la explosión está activa; si nunca se acerca,
   queda a salvo y suma puntos. Esto evita que solo cuenten los impactos directos
   instantáneos, **respetando** las distancias de la consigna.

3. **Hitbox del avión.** La distancia de daño se mide al **cuerpo** de la nave
   (`RADIO_AVION = 130 m`), no a su punto central, porque la aeronave ocupa espacio.
   Es más fiel que tratarla como un punto y hace que los hits ocurran de forma natural.

   Además, un misil **detona al chocar contra la nave** (no se lo puede atravesar): si
   su cuerpo (`RADIO_MISIL = 60 m`) toca el hitbox del avión, explota en el acto. La
   colisión la decide el propio misil (`Misil.impactaA`, Information Expert) y el
   choque de frente cuenta como impacto directo (cuesta una vida).

4. **Intervalo entre misiles (cooldown).** Cada dron espera **1 segundo** entre un
   misil y el siguiente (`Config.COOLDOWN_DISPARO_TICKS`).

5. **Sprites desde archivo.** Cada objeto usa su PNG real desde `Res/Sprites/`
   (`Sprites.java`). Si falta un archivo, se usa un **dibujo de respaldo** por código.

6. **Daño exacto vs visual de la explosión.** El daño usa las distancias exactas de la
   consigna (`ESCALA_RADIO_EXPLOSION = 1.0`). La bola de fuego se dibuja más grande
   solo por estética (`ESCALA_VISUAL_EXPLOSION = 2.5`); no afecta el daño.

7. **Proporciones / "zoom".** Para que los objetos no se vean diminutos se acercó la
   cámara (`METROS_POR_PIXEL = 6`) y se agrandó la ventana (1000×780), sin cambiar los
   valores en metros de la consigna.

---

## Constantes ajustables (`Model/Config.java`)

| Constante | Valor | Para qué |
|---|---|---|
| `ANCHO_PX` / `ALTO_PX` | 1000 / 780 | Tamaño de ventana |
| `METROS_POR_PIXEL` | 6.0 | "Zoom" (menos = objetos más grandes) |
| `MS_POR_TICK` | 30 | Duración del tick (~33 fps) |
| `COOLDOWN_DISPARO_TICKS` | 33 (=1 s) | Intervalo mínimo entre misiles de un dron |
| `ALTITUD_MIN/MAX` | 1000 / 5000 | Rango de altitud del avión (consigna) |
| `DETONACION_MIN/MAX` | 1200 / 4500 | Altitud de detonación (consigna) |
| `RADIO_DANIO / _ALTO / _CRITICO` | 150 / 80 / 20 | Distancias de daño (consigna) |
| `ESCALA_RADIO_EXPLOSION` | 1.0 | 1.0 = consigna exacta |
| `RADIO_AVION` | 130 | Tamaño del hitbox de la nave |
| `RADIO_MISIL` | 60 | Tamaño del misil para detectar el choque con la nave |
| `ESCALA_VISUAL_EXPLOSION` | 2.5 | Tamaño visual de la explosión (no daña) |

Otros (en `Juego.java`): `TAM_ESCUADRON=10`, `DRONES_ACTIVOS=4`,
`DRON_VEL_BASE=16`, `MISIL_VEL_BASE=24`, `FREC_DISPARO_BASE=0.01`,
`PUNTOS_NIVEL=300`. Dificultad por nivel en `Nivel.INCREMENTO_POR_NIVEL=0.15`.
Vidas iniciales 3 (`ControladorJuego.nuevaPartida`).

---

## Imágenes (`Res/Sprites/`)

Nombres esperados (PNG con **fondo transparente**): `dron.png`, `misil.png`,
`explosion.png`, `nave.png`. Detalle en `Res/Sprites/LEEME.txt`.

- `dron.png`, `explosion.png`, `nave.png` → provistos por el usuario, **cargan OK**.
- `misil.png` → **recortado de `dron.png`** (el mismo misil que cuelga del dron:
  cabezal rojo + cuerpo verde, punta abajo), así coincide con el del dron. El original
  quedó respaldado en `misil_backup.png`.

---

## Pendientes / ideas

- [ ] Pantalla de **Opciones** (hoy es placeholder): volumen, dificultad, borrar leaderboard.
- [ ] Posible feedback de daño (flash de pantalla, texto flotante "+40 / −20%").
- [ ] Sonido.

---

## Notas de POO / arquitectura

Se mantuvo la estructura MVC. Mejoras aplicadas respecto a la versión original:
- Lógica de drones movida al `Escuadron` (Information Expert).
- Colisión misil↔nave decidida por el propio `Misil` (`impactaA`, Information Expert).
- Daño/detonación coherente: energía ↔ vidas conectadas.
- Constantes nombradas en `Config` (menos "números mágicos").
- La Vista no tiene reglas; el Modelo no hace I/O de presentación.
- Cada función tiene un comentario breve que explica qué hace.

Puntos aún mejorables (si se pide más rigor): varios setters públicos rompen
encapsulamiento; `getDrones()`/`getMisilesActivos()` exponen las listas internas;
`ObjetoVolador` no define un contrato polimórfico (cada subclase tiene su método de
movimiento). No son bloqueantes para el funcionamiento.
