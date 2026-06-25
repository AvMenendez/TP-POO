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
    Avion.java                   Nave del jugador (energía, posición)
    Dron.java                    Enemigo (movimiento, disparo con cooldown)
    Misil.java                   Cae y detona a una altitud aleatoria
    Explosion.java               Área de daño que persiste unos instantes
    Escuadron.java               Grupo de 10 drones (máx 4 activos)
    Nivel.java                   Número de nivel y multiplicador de dificultad
    Jugador.java                 Nombre, vidas, puntaje, vida extra
    Juego.java                   Orquesta el modelo (loop, spawn, daño, victoria)
    EstadoJuego.java             enum: JUGANDO / GAME_OVER / VICTORIA
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
| Cada dron entra por un **extremo aleatorio**, a tiempos aleatorios y sin superponerse | ✅ | `Juego.controlarSpawnDrones` / `entradaLibre` |
| Sobrevivir → avanza de nivel | ✅ | `Juego.verificarCondicionVictoria` |
| Por nivel aumentan vel. drones, vel. caída misiles y frecuencia **+15%** | ✅ | `Nivel.INCREMENTO_POR_NIVEL` |
| Daño por distancia explosión↔avión (40/20/0 pts; 0/-20%/-40%; <20 vida) | ✅ | `Juego.aplicarDanioPorDistancia` |
| **+300** puntos por superar nivel | ✅ | `Juego.PUNTOS_NIVEL` |
| **Vida extra cada 1000** puntos | ✅ | `Jugador.sumarPuntos` |

Tabla de daño (consigna), medida **al cuerpo del avión** (distancia de la explosión a la
superficie de la nave = distancia al centro − `RADIO_AVION`, porque la aeronave ocupa espacio):
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

3. **Daño por distancia al cuerpo + choque (extensión).** El avión tiene un radio de
   cuerpo (`RADIO_AVION = 200 m`), porque la aeronave ocupa espacio.
   - **Detonación automática (consigna):** el misil explota a su altitud programada y el
     daño se mide **al cuerpo** del avión (distancia al centro − `RADIO_AVION`), aplicando
     los tramos **20/80/150** literales sobre esa distancia (`Explosion.distanciaAlAvion`).
     Medir al cuerpo (en vez de al centro) da más alcance efectivo sin tocar los números
     de la consigna.
   - **Choque (extensión de jugabilidad, NO está en la consigna):** un misil no se puede
     atravesar. Si su cuerpo (`RADIO_MISIL`) toca el del avión (`RADIO_AVION`), el misil
     **detona en el acto** y el daño sale de la **misma tabla por distancia** (no es un
     golpe fijo de una vida). La colisión la decide el propio misil (`Misil.impactaA`,
     Information Expert).

4. **Intervalo entre misiles (cooldown) que escala con el nivel.** El cooldown base es
   **1 segundo** (`Config.COOLDOWN_DISPARO_TICKS`), pero se **divide por el multiplicador
   del nivel** (mínimo 1 tick) y se le pasa a cada dron al crearlo. Así la *frecuencia de
   disparo* sube de verdad por nivel (la consigna): sin esto, en niveles altos los drones
   cruzan tan rápido que el cooldown fijo les permitía ~1 misil y el juego se hacía fácil.

5. **Sprites desde archivo.** Cada objeto usa su PNG real desde `Res/Sprites/`
   (`Sprites.java`). Si falta un archivo, se usa un **dibujo de respaldo** por código.

6. **Daño exacto vs visual de la explosión.** El daño usa las distancias exactas de la
   consigna (`ESCALA_RADIO_EXPLOSION = 1.0`). La bola de fuego se dibuja más grande
   solo por estética (`ESCALA_VISUAL_EXPLOSION = 2.5`); no afecta el daño.

7. **Proporciones / "zoom".** Para que los objetos no se vean diminutos se acercó la
   cámara (`METROS_POR_PIXEL = 6`) y se agrandó la ventana (1000×780), sin cambiar los
   valores en metros de la consigna. El tamaño de los sprites se fija en píxeles
   (constantes `DRON_PX`/`NAVE_PX`/`MISIL_W`/`MISIL_H` en `PanelJuego`) y es puramente
   visual: no influye en el daño (que depende solo de la distancia de la explosión).

8. **Drones repartidos a lo ancho (entran de a uno).** No se crean todos en el mismo
   instante (si no, los del mismo extremo quedaban superpuestos y parecían 2). Entra un
   dron y el siguiente por ese extremo recién aparece cuando el anterior ya avanzó
   `SEPARACION_SPAWN` metros (= `ANCHO_M / DRONES_ACTIVOS` = 1500 m), de modo que los 4
   drones quedan **repartidos a lo ancho** de la pantalla y no amontonados en "dúos"
   junto a los bordes (lo que dejaba grandes zonas sin drones ni misiles y hacía el
   juego demasiado fácil). Se le suma una pequeña espera al azar (`ESPERA_SPAWN_MIN`–
   `MAX` ticks) para que el instante exacto no sea perfectamente regular. Si el extremo
   elegido está ocupado se prueba el otro, y si ambos lo están se reintenta el próximo
   tick (`Juego.controlarSpawnDrones` + `entradaLibre`).

9. **Tope de niveles = 15 (extensión).** La consigna no fija un final: sobrevivir avanza
   de nivel indefinidamente. Se agregó un tope `NIVEL_MAX = 15`: al superar el nivel 15
   el juego termina con **victoria** (`EstadoJuego.VICTORIA` → pantalla "¡GANASTE!"). El
   HUD muestra el progreso como `Nivel: n/15`.

---

## Constantes ajustables (`Model/Config.java`)

| Constante | Valor | Para qué |
|---|---|---|
| `ANCHO_PX` / `ALTO_PX` | 1000 / 780 | Tamaño de ventana |
| `METROS_POR_PIXEL` | 6.0 | "Zoom" (menos = objetos más grandes) |
| `MS_POR_TICK` | 30 | Duración del tick (~33 fps) |
| `COOLDOWN_DISPARO_TICKS` | 33 (=1 s) | Cooldown base entre misiles (nivel 1); se divide por el multiplicador del nivel |
| `ALTITUD_MIN/MAX` | 1000 / 5000 | Rango de altitud del avión (consigna) |
| `DETONACION_MIN/MAX` | 1200 / 4500 | Altitud de detonación (consigna) |
| `RADIO_DANIO / _ALTO / _CRITICO` | 150 / 80 / 20 | Distancias de daño (consigna) |
| `ESCALA_RADIO_EXPLOSION` | 1.0 | 1.0 = consigna exacta |
| `RADIO_AVION` | 200 | Radio del cuerpo de la nave: se resta para medir el daño al cuerpo y define el choque |
| `RADIO_MISIL` | 40 | Radio del cuerpo del misil para el choque con la nave |
| `ESCALA_VISUAL_EXPLOSION` | 2.5 | Tamaño visual de la explosión (no daña) |

Tamaños de **dibujo** de los objetos (solo visual, no afectan el daño) en
`Views/PanelJuego.java`: `DRON_PX=96`, `NAVE_PX=96`, `MISIL_W=36`, `MISIL_H=66`.

Otros (en `Juego.java`): `TAM_ESCUADRON=10`, `NIVEL_MAX=15` (tope de niveles; al
superar el nivel 15 se gana el juego → `EstadoJuego.VICTORIA`), `DRONES_ACTIVOS=4`,
`SEPARACION_SPAWN=ANCHO_M/DRONES_ACTIVOS` (=1500 m; reparte los drones a lo ancho
y evita los "dúos"), `ESPERA_SPAWN_MIN=10`/`ESPERA_SPAWN_MAX=40` (jitter aleatorio
en ticks entre un dron y el siguiente), `DRON_VEL_BASE=22`, `MISIL_VEL_BASE=32`,
`FREC_DISPARO_BASE=0.04` (velocidades/frecuencia base del nivel 1; valores propios,
escalan +15% por nivel), `PUNTOS_NIVEL=300`. Dificultad por nivel en
`Nivel.INCREMENTO_POR_NIVEL=0.15`. Vidas iniciales 3 (`ControladorJuego.nuevaPartida`).

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
- Detonación y choque decididos por el propio `Misil` (`haDetonado` / `impactaA`, Information Expert).
- Daño/detonación coherente: energía ↔ vidas conectadas.
- Constantes nombradas en `Config` (menos "números mágicos").
- La Vista no tiene reglas; el Modelo no hace I/O de presentación.
- Cada función tiene un comentario breve que explica qué hace.

Puntos aún mejorables (si se pide más rigor): varios setters públicos rompen
encapsulamiento; `getDrones()`/`getMisilesActivos()` exponen las listas internas;
`ObjetoVolador` no define un contrato polimórfico (cada subclase tiene su método de
movimiento). No son bloqueantes para el funcionamiento.
