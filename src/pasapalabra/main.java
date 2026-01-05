package pasapalabra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

/**
 * Aplicación en consola que permite jugar a una versión del juego Pasapalabra.
 * El programa carga preguntas desde ficheros según el nivel de dificultad,
 * genera un rosco de 26 letras, gestiona las respuestas del usuario y guarda
 * las estadísticas finales de la partida en un fichero.
 */
public class main {

    /**
     * Carga las preguntas desde un fichero de texto y genera el rosco del juego.
     * Para cada letra del abecedario (A-Z) se selecciona aleatoriamente una pregunta.
     * La información se almacena en una matriz de 26 filas y 4 columnas.
     *
     * Columnas de la matriz:
     * 0 -> Letra
     * 1 -> Pregunta
     * 2 -> Respuesta
     * 3 -> Estado de la pregunta
     *      (0: no planteada, 1: acertada, 2: fallada, 3: pasapalabra)
     *
     * @param nombreFichero nombre del fichero que contiene las preguntas
     * @return matriz bidimensional con el rosco completo del juego
     */
    public static String[][] cargarDatos(String nombreFichero) {

        String[][] rosco = new String[26][4];
        String[][] banco = new String[26][4];
        int[] contadores = new int[26];

        String[] letras = {
            "A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
        };

        for (int i = 0; i < 26; i++) {
            rosco[i][0] = letras[i];
            rosco[i][3] = "0";
            contadores[i] = 0;
        }

        String directorioActual = System.getProperty("user.dir");
        String ruta = "";
        BufferedReader lector = null;
        
        // Intentar diferentes rutas posibles
        String[] rutasPosibles = {
            directorioActual + "/pasapalabra-java/data/" + nombreFichero,
            directorioActual + "/data/" + nombreFichero,
            "data/" + nombreFichero
        };
        
        boolean archivoEncontrado = false;
        for (int i = 0; i < rutasPosibles.length && !archivoEncontrado; i++) {
            try {
                ruta = rutasPosibles[i];
                lector = new BufferedReader(new FileReader(ruta));
                archivoEncontrado = true;
            } catch (Exception e) {
                // Intentar siguiente ruta
            }
        }

        if (archivoEncontrado) {
            try {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        String[] partes = linea.split(";");
                        if (partes.length >= 3) {
                            char letra = Character.toUpperCase(partes[0].charAt(0));
                            int indice = -1;

                            for (int i = 0; i < 26; i++) {
                                if (letras[i].charAt(0) == letra) {
                                    indice = i;
                                    break;
                                }
                            }

                            if (indice != -1 && contadores[indice] < 4) {
                                banco[indice][contadores[indice]] = linea;
                                contadores[indice]++;
                            }
                        }
                    }
                }
                lector.close();
            } catch (Exception e) {
                System.out.println("Error no se pudo leer el archivo " + ruta);
                System.out.println("Detalle: " + e.getMessage());
            }
        } else {
            System.out.println("Error no se pudo leer el archivo " + nombreFichero);
            System.out.println("No se encontró en ninguna ubicación posible");
        }

        Random random = new Random();

        for (int i = 0; i < 26; i++) {
            if (contadores[i] == 0) {
                rosco[i][1] = "SIN PREGUNTA";
                rosco[i][2] = "";
            } else {
                int posicion = random.nextInt(contadores[i]);
                String[] partes = banco[i][posicion].split(";");
                rosco[i][1] = partes[1].trim();
                rosco[i][2] = partes[2].trim();
            }
        }

        return rosco;
    }

    /**
     * Guarda en un fichero los resultados finales de una partida.
     * Los datos se almacenan en una línea con el siguiente formato:
     * correo;aciertos;fallos;pasapalabras;nivel
     *
     * @param ficheroResultados nombre del fichero donde se guardan los resultados
     * @param correo correo electrónico del usuario
     * @param aciertos número de respuestas correctas
     * @param fallos número de respuestas incorrectas
     * @param pasapalabra número de preguntas no respondidas
     * @param nivel nivel de dificultad jugado
     */
    public static void guardarDatosPartida(String ficheroResultados, String correo,
                                            int aciertos, int fallos, int pasapalabra, String nivel) {
        String directorioActual = System.getProperty("user.dir");
        String[] rutasPosibles = {
            directorioActual + "/pasapalabra-java/data/" + ficheroResultados,
            directorioActual + "/data/" + ficheroResultados,
            "data/" + ficheroResultados
        };
        
        boolean archivoGuardado = false;
        for (int i = 0; i < rutasPosibles.length && !archivoGuardado; i++) {
            try {
                BufferedWriter escritor = new BufferedWriter(
                    new FileWriter(rutasPosibles[i], true)
                );
                escritor.write(correo + ";" + aciertos + ";" + fallos + ";" + pasapalabra + ";" + nivel + "\n");
                escritor.close();
                archivoGuardado = true;
            } catch (Exception e) {
                // Intentar siguiente ruta
            }
        }
        
        if (!archivoGuardado) {
            System.out.println("Error escribiendo fichero");
        }
    }

    /**
     * Método principal del programa.
     * Controla el flujo general de la aplicación: registro del usuario,
     * selección del nivel, ejecución del juego, visualización de resultados
     * y guardado de las estadísticas finales.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.print("Introduce tu nombre: ");
        String nombre = in.nextLine();

        String correo = "";
        boolean correoValido = false;

        while (!correoValido) {
            System.out.print("Introduce tu correo: ");
            correo = in.nextLine();

            if (correo.contains("@") && correo.contains(".")) {
                correoValido = true;
            } else {
                System.out.println("ERROR: Correo no válido. Debe contener '@' y '.'.");
            }
        }

        System.out.println("Introduce el nivel en el que deseas jugar (infantil, facil, medio, avanzado): ");
        String nivel = in.nextLine();

        if (!nivel.equals("infantil") && !nivel.equals("facil") &&
            !nivel.equals("medio") && !nivel.equals("avanzado")) {
            System.out.println("Nivel incorrecto. Se usará el nivel predeterminado.");
            nivel = "facil";
        }

        String archivo = "rosco_" + nivel + ".txt";
        String[][] rosco = cargarDatos(archivo);

        int aciertos = 0;
        int fallos = 0;
        int pasapalabra = 0;
        boolean quedanPasadas;

        do {
            quedanPasadas = false;

            for (int i = 0; i < 26; i++) {
                if (rosco[i][3].equals("0") || rosco[i][3].equals("3")) {
                    if (!rosco[i][1].equals("SIN PREGUNTA")) {

                        System.out.println("Letra " + rosco[i][0] + ": " + rosco[i][1]);
                        String respuesta = in.nextLine().trim();

                        if (respuesta.equalsIgnoreCase("Pasapalabra")) {
                            rosco[i][3] = "3";
                            pasapalabra++;
                            quedanPasadas = true;
                        } else if (respuesta.equalsIgnoreCase(rosco[i][2])) {
                            rosco[i][3] = "1";
                            aciertos++;
                            System.out.println("Correcto");
                        } else {
                            rosco[i][3] = "2";
                            fallos++;
                            System.out.println("Incorrecto, la respuesta era: " + rosco[i][2]);
                        }
                    }
                }
            }

            if (quedanPasadas) {
                System.out.print("¿Quieres seguir con los pasapalabras? (s/n): ");
                String seguir = in.nextLine();
                if (!seguir.equals("s")) {
                    quedanPasadas = false;
                }
            }

        } while (quedanPasadas);

        System.out.println("---- RESULTADOS FINALES ----");
        System.out.println("Aciertos: " + aciertos);
        System.out.println("Fallos: " + fallos);
        System.out.println("Pasapalabras: " + pasapalabra);

        guardarDatosPartida("estadisticas_usuario.txt", correo, aciertos, fallos, pasapalabra, nivel);

        in.close();
    }
}
