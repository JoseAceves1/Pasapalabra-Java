package pasapalabra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

/**
 * Aplicación en consola que permite jugar a una versión simplificada
 * del juego Pasapalabra.
 *
 * El programa:
 * - Carga preguntas desde ficheros de texto según el nivel elegido
 * - Genera un rosco de 26 letras (A-Z)
 * - Gestiona las respuestas del usuario
 * - Guarda las estadísticas finales en un fichero
 */
public class main {

    /**
     * Carga las preguntas desde un fichero y genera el rosco del juego.
     *
     * Estructura del rosco (26 x 4):
     * 0 -> Letra
     * 1 -> Pregunta
     * 2 -> Respuesta
     * 3 -> Estado
     *      0: no planteada
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
     * Guarda las estadísticas finales de la partida en un fichero.
     *
     * Formato:
     * correo;aciertos;fallos;pasapalabras;nivel
     *
     * @param correo correo del jugador
     * @param aciertos número de aciertos
     * @param fallos número de fallos
     * @param pasapalabras número de pasapalabras
     * @param nivel nivel jugado
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
            System.out.println("Error guardando las estadísticas");
        }
    }

    /**
     * Método principal del programa.
     *
     * Controla:
     * - Registro del usuario
     * - Selección del nivel
     * - Ejecución del juego
     * - Mostrar resultados
     * - Guardar estadísticas
     *
     * @param args argumentos de línea de comandos
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

        int aciertos = 0, fallos = 0, pasapalabras = 0;
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

            if (seguirJugando.equals("n")) {
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
