package pasapalabra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

/**
 * Aplicación de consola que implementa una versión simplificada del juego
 * Pasapalabra.
 *
 * Este programa permite:
 * - Jugar una partida de Pasapalabra por consola
 * - Cargar preguntas desde ficheros según el nivel
 * - Gestionar respuestas y estados del rosco
 * - Guardar estadísticas de cada partida
 * - Mostrar estadísticas globales y el TOP 3 de jugadores
 */
public class main {

    /**
     * Carga las preguntas desde un fichero de texto y genera el rosco del juego.
     *
     * El fichero debe tener líneas con el formato:
     * LETRA;PREGUNTA;RESPUESTA
     *
     * Para cada letra se elige una pregunta aleatoria.
     *
     * El rosco se guarda en una matriz de 26x4:
     * 0 -> letra
     * 1 -> pregunta
     * 2 -> respuesta correcta
     * 3 -> estado de la letra
     *      0: no usada
     *      1: acertada
     *      2: fallada
     *      3: pasapalabra
     *
     * @param nombreFichero nombre del fichero de preguntas
     * @return matriz con el rosco completo
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

        File fichero = new File("pasapalabra-java/data/" + nombreFichero);

        try {
            BufferedReader lector = new BufferedReader(new FileReader(fichero));
            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.split(";");

                if (partes.length >= 3) {
                    char letra = Character.toUpperCase(partes[0].charAt(0));
                    int indice = letra - 'A';

                    if (indice >= 0 && indice < 26 && contadores[indice] < 4) {
                        banco[indice][contadores[indice]] = linea;
                        contadores[indice]++;
                    }
                }
            }
            lector.close();

        } catch (Exception e) {
            System.out.println("Error leyendo el fichero de preguntas.");
        }

        Random random = new Random();

        for (int i = 0; i < 26; i++) {
            if (contadores[i] == 0) {
                rosco[i][1] = "SIN PREGUNTA";
                rosco[i][2] = "";
            } else {
                int pos = random.nextInt(contadores[i]);
                String[] partes = banco[i][pos].split(";");
                rosco[i][1] = partes[1].trim();
                rosco[i][2] = partes[2].trim();
            }
        }

        return rosco;
    }

    /**
     * Guarda las estadísticas de una partida en un fichero de texto.
     *
     * Cada línea del fichero tiene el formato:
     * correo;aciertos;fallos;pasapalabras;nivel
     *
     * Los datos se añaden al final del fichero.
     *
     * @param correo correo del jugador
     * @param aciertos número de respuestas correctas
     * @param fallos número de respuestas incorrectas
     * @param pasapalabras número de pasapalabras usados
     * @param nivel nivel jugado
     */
    public static void guardarDatosPartida(
            String correo, int aciertos, int fallos, int pasapalabras, String nivel) {

        File fichero = new File("pasapalabra-java/data/estadisticas_usuario.txt");

        try {
            BufferedWriter escritor = new BufferedWriter(new FileWriter(fichero, true));
            escritor.write(correo + ";" + aciertos + ";" + fallos + ";" +
                           pasapalabras + ";" + nivel);
            escritor.newLine();
            escritor.close();
        } catch (Exception e) {
            System.out.println("Error guardando las estadísticas.");
        }
    }

    /**
     * Lee el fichero de estadísticas y muestra:
     * - Estadísticas globales del juego
     * - El TOP 3 de jugadores según el número de aciertos
     *
     * Cada línea del fichero representa una partida distinta.
     * Se acumulan los datos para obtener totales.
     */
    public static void mostrarTop3yEstadisticas() {

        int partidasTotales = 0;
        int aciertosTotales = 0;
        int fallosTotales = 0;
        int pasapalabrasTotales = 0;

        String[] topCorreos = new String[3];
        int[] topAciertos = new int[3];

        File fichero = new File(
            "pasapalabra-java/data/estadisticas_usuario.txt"
        );

        try {
            BufferedReader lector = new BufferedReader(new FileReader(fichero));
            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.split(";");

                if (partes.length == 5) {
                    String correo = partes[0];
                    int a = Integer.parseInt(partes[1]);
                    int f = Integer.parseInt(partes[2]);
                    int pa = Integer.parseInt(partes[3]);

                    partidasTotales++;
                    aciertosTotales += a;
                    fallosTotales += f;
                    pasapalabrasTotales += pa;

                    for (int i = 0; i < 3; i++) {
                        if (a > topAciertos[i]) {
                            for (int j = 2; j > i; j--) {
                                topAciertos[j] = topAciertos[j - 1];
                                topCorreos[j] = topCorreos[j - 1];
                            }
                            topAciertos[i] = a;
                            topCorreos[i] = correo;
                            break;
                        }
                    }
                }
            }
            lector.close();

        } catch (Exception e) {
            System.out.println("Error al leer las estadísticas.");
        }

        System.out.println("\n===== ESTADÍSTICAS GENERALES =====");
        System.out.println("Partidas: " + partidasTotales);
        System.out.println("Aciertos: " + aciertosTotales);
        System.out.println("Fallos: " + fallosTotales);
        System.out.println("Pasapalabras: " + pasapalabrasTotales);

        System.out.println("\n===== TOP 3 JUGADORES =====");
        for (int i = 0; i < 3; i++) {
            if (topCorreos[i] != null) {
                System.out.println((i + 1) + ". " + topCorreos[i] +
                        " → " + topAciertos[i] + " aciertos");
            }
        }
    }

    /**
     * Método principal del programa.
     *
     * Se encarga de:
     * - Pedir los datos del usuario
     * - Elegir el nivel de dificultad
     * - Ejecutar el juego
     * - Guardar las estadísticas
     * - Mostrar estadísticas y ranking
     *
     * @param args argumentos del programa
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.print("Introduce tu nombre: ");
        in.nextLine();

        String correo;
        do {
            System.out.print("Introduce tu correo: ");
            correo = in.nextLine();
        } while (!correo.contains("@") || !correo.contains("."));

        System.out.print("Nivel (infantil, facil, medio, avanzado): ");
        String nivel = in.nextLine();

        if (!nivel.equals("infantil") && !nivel.equals("facil")
                && !nivel.equals("medio") && !nivel.equals("avanzado")) {
            nivel = "facil";
        }

        String[][] rosco = cargarDatos("rosco_" + nivel + ".txt");

        int aciertos = 0;
        int fallos = 0;
        int pasapalabras = 0;
        boolean quedanPasadas;

        do {
            quedanPasadas = false;

            for (int i = 0; i < 26; i++) {
                if (rosco[i][3].equals("0") || rosco[i][3].equals("3")) {

                    System.out.println("Letra " + rosco[i][0] + ": " + rosco[i][1]);
                    String respuesta = in.nextLine();

                    if (respuesta.equalsIgnoreCase("pasapalabra")) {
                        rosco[i][3] = "3";
                        pasapalabras++;
                        quedanPasadas = true;
                    } else if (respuesta.equalsIgnoreCase(rosco[i][2])) {
                        rosco[i][3] = "1";
                        aciertos++;
                        System.out.println("Correcto");
                    } else {
                        rosco[i][3] = "2";
                        fallos++;
                        System.out.println("Incorrecto");
                    }
                }
            }

            System.out.println("¿Quieres seguir jugando? (s/n)");
            String seguirJugando = in.nextLine();

            if (seguirJugando.equalsIgnoreCase("n")) {
                quedanPasadas = false;
            }

        } while (quedanPasadas);

        guardarDatosPartida(correo, aciertos, fallos, pasapalabras, nivel);
        mostrarTop3yEstadisticas();
        in.close();
    }
}

