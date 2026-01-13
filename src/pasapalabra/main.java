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
 * Este programa se utiliza como práctica de:
 * - Uso de matrices
 * - Lectura y escritura de ficheros
 * - Estructuras de control
 * - Entrada de datos por teclado
 *
 * El juego carga preguntas por nivel de dificultad y guarda las estadísticas
 * finales de cada partida en un fichero de texto.
 */
public class main {

    /**
     * Carga las preguntas desde un fichero de texto y genera el rosco del juego.
     *
     * Cada línea del fichero debe tener el formato:
     * LETRA;PREGUNTA;RESPUESTA
     *
     * El rosco se representa mediante una matriz de 26 filas (A-Z) y 4 columnas:
     * 0 -> Letra asociada
     * 1 -> Pregunta
     * 2 -> Respuesta correcta
     * 3 -> Estado de la letra:
     *      0 = no planteada
     *      1 = acertada
     *      2 = fallada
     *      3 = pasapalabra
     *
     * @param nombreFichero nombre del fichero que contiene las preguntas
     * @return matriz que representa el rosco completo del juego
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
     * Guarda las estadísticas finales de una partida en un fichero de texto.
     *
     * El formato de cada línea es:
     * correo;aciertos;fallos;pasapalabras;nivel
     *
     * @param correo correo electrónico del jugador
     * @param aciertos número de respuestas correctas
     * @param fallos número de respuestas incorrectas
     * @param pasapalabras número de pasapalabras utilizados
     * @param nivel nivel de dificultad jugado
     */
    public static void guardarDatosPartida(
            String correo, int aciertos, int fallos, int pasapalabras, String nivel) {

        File fichero = new File("pasapalabra-java/data/estadisticas_usuario.txt");

        try {
            BufferedWriter escritor = new BufferedWriter(new FileWriter(fichero, true));
            escritor.write(correo + ";" + aciertos + ";" + fallos + ";" + pasapalabras + ";" + nivel);
            escritor.newLine();
            escritor.close();
        } catch (Exception e) {
            System.out.println("Error guardando las estadísticas.");
        }
    }

    /**
     * Punto de entrada de la aplicación.
     *
     * Se encarga de:
     * - Solicitar los datos del usuario
     * - Validar el correo electrónico
     * - Seleccionar el nivel de dificultad
     * - Ejecutar el bucle principal del juego
     * - Mostrar los resultados finales
     * - Guardar las estadísticas de la partida
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.print("Introduce tu nombre: ");
        in.nextLine(); // solo informativo

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

        System.out.println("Aciertos: " + aciertos);
        System.out.println("Fallos: " + fallos);
        System.out.println("Pasapalabras: " + pasapalabras);

        guardarDatosPartida(correo, aciertos, fallos, pasapalabras, nivel);
        in.close();
    }
}
