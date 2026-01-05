package pasapalabra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

public class main {
    public static String [][] cargarDatos(String nombreFichero){
        String [][] rosco = new String[26][4];
        String [][] banco = new String[26][4];
        int [] contadores = new int [26];

        String[] letras = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        for(int i = 0; i < 26; i++){
            rosco[i][0] = letras [i];
            rosco[i][3] = "0";
            contadores[i] = 0;package pasapalabra;

            import java.io.BufferedReader;
            import java.io.BufferedWriter;
            import java.io.FileReader;
            import java.io.FileWriter;
            import java.util.Random;
            import java.util.Scanner;
            
            public class main {
                public static String [][] cargarDatos(String nombreFichero){
                    String [][] rosco = new String[26][4];
                    String [][] banco = new String[26][4];
                    int [] contadores = new int [26];
            
                    String[] letras = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
                        "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
            
                    for(int i = 0; i < 26; i++){
                        rosco[i][0] = letras [i];
                        rosco[i][3] = "0";
                        contadores[i] = 0;
                    }
            
                    String ruta = "/Users/joseaceves/Grupo-2_Pasapalabra_v2/pasapalabra-java/data/" + nombreFichero;
            
                    try{
                        BufferedReader lector = new BufferedReader(new FileReader(ruta));
                        String linea;
            
                        while ((linea = lector.readLine()) != null) {
                            linea = linea.trim();
                            if (!linea.isEmpty()) {
                                String [] partes = linea.split(";");
                                if (partes.length >= 3) {
                                    char letra = Character.toUpperCase(partes[0].charAt(0));
                                    
                                    int indice = -1;
            
                                    for(int i = 0; i < 26; i++){
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
                    } catch (Exception e){
                        System.out.println("Error no se pudo leer el archivo " + ruta);
                        System.out.println("Detalle: " + e.getMessage());
                    }
            
                    Random random = new Random();
            
                    for(int i = 0; i < 26; i++){
                        if (contadores[i] == 0) {
                            rosco[i][1] = "SIN PREGUNTA";
                            rosco[i][2] = "";
                        } else {
                            int posicion = random.nextInt(contadores[i]);
                            String [] partes = banco[i][posicion].split(";");
            
                            rosco[i][1] = partes[1].trim();
                            rosco[i][2] = partes[2].trim();
                        }
                    }
                    return rosco;
                }
            
                public static void guardarDatosPartidos(String ficheroResultados, String correo, int aciertos, int fallos, int pasapalabra, String nivel){
                    try {
                        BufferedWriter escritor = new BufferedWriter(new FileWriter("/Users/joseaceves/Grupo-2_Pasapalabra_v2/pasapalabra-java/data/" + ficheroResultados, true));
                        escritor.write(correo + ";" + aciertos + ";" + fallos + ";" + pasapalabra + ";" + nivel + "\n");
            
                        escritor.close();
                    } catch (Exception e){
                        System.out.println("Error escribiendo fichero");
                    }
                }
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
            
                   if (!nivel.equals("infantil") && !nivel.equals("facil") && !nivel.equals("medio") && !nivel.equals("avanzado")) {
                    System.out.println("Nivel incorrecto. Se usará el nivel predeterminado.");
                    nivel = "facil";
                   }
                   String archivo = "rosco_" + nivel + ".txt";
            
                   String [][] rosco = cargarDatos(archivo);
            
                   int aciertos = 0;
                   int fallos = 0;
                   int pasapalabra = 0;
            
                   boolean quedanPasadas;
            
                   do{
                    quedanPasadas = false;
                    
                    for(int i = 0; i < 26; i++){
                        if (rosco[i][3].equals("0") || rosco[i][3].equals("3")) {
                            if (rosco[i][1].equals("SIN PREGUNTA")) {
                            } else {
                                System.out.println("Letra " + rosco[i][0] + ": " + rosco[i][1]);
                                String respuesta = in.nextLine().trim();
            
                                if (respuesta.equalsIgnoreCase("Pasapalabra")) {
                                    rosco[i][3]= "3";
                                    pasapalabra++;
                                    quedanPasadas = true;
                                } else if(respuesta.equalsIgnoreCase(rosco[i][2])){
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
            
                   guardarDatosPartidos("estadisticas_usuario.txt", correo, aciertos, fallos, pasapalabra, nivel);
            
                   in.close();
                
                }
            }
            
        }

        String ruta = "/Users/joseaceves/Grupo-2_Pasapalabra_v2/pasapalabra-java/data/" + nombreFichero;

        try{
            BufferedReader lector = new BufferedReader(new FileReader(ruta));
            String linea;

            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String [] partes = linea.split(";");
                    if (partes.length >= 3) {
                        char letra = Character.toUpperCase(partes[0].charAt(0));
                        
                        int indice = -1;

                        for(int i = 0; i < 26; i++){
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
        } catch (Exception e){
            System.out.println("Error no se pudo leer el archivo " + ruta);
            System.out.println("Detalle: " + e.getMessage());
        }

        Random random = new Random();

        for(int i = 0; i < 26; i++){
            if (contadores[i] == 0) {
                rosco[i][1] = "SIN PREGUNTA";
                rosco[i][2] = "";
            } else {
                int posicion = random.nextInt(contadores[i]);
                String [] partes = banco[i][posicion].split(";");

                rosco[i][1] = partes[1].trim();
                rosco[i][2] = partes[2].trim();
            }
        }
        return rosco;
    }

    public static void guardarDatosPartidos(String ficheroResultados, String correo, int aciertos, int fallos, int pasapalabra, String nivel){
        try {
            BufferedWriter escritor = new BufferedWriter(new FileWriter("/Users/joseaceves/Grupo-2_Pasapalabra_v2/pasapalabra-java/data/" + ficheroResultados, true));
            escritor.write(correo + ";" + aciertos + ";" + fallos + ";" + pasapalabra + ";" + nivel + "\n");

            escritor.close();
        } catch (Exception e){
            System.out.println("Error escribiendo fichero");
        }
    }
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

       if (!nivel.equals("infantil") && !nivel.equals("facil") && !nivel.equals("medio") && !nivel.equals("avanzado")) {
        System.out.println("Nivel incorrecto. Se usará el nivel predeterminado.");
        nivel = "facil";
       }
       String archivo = "rosco_" + nivel + ".txt";

       String [][] rosco = cargarDatos(archivo);

       int aciertos = 0;
       int fallos = 0;
       int pasapalabra = 0;

       boolean quedanPasadas;

       do{
        quedanPasadas = false;
        
        for(int i = 0; i < 26; i++){
            if (rosco[i][3].equals("0") || rosco[i][3].equals("3")) {
                if (rosco[i][1].equals("SIN PREGUNTA")) {
                } else {
                    System.out.println("Letra " + rosco[i][0] + ": " + rosco[i][1]);
                    String respuesta = in.nextLine().trim();

                    if (respuesta.equalsIgnoreCase("Pasapalabra")) {
                        rosco[i][3]= "3";
                        pasapalabra++;
                        quedanPasadas = true;
                    } else if(respuesta.equalsIgnoreCase(rosco[i][2])){
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

       guardarDatosPartidos("estadisticas_usuario.txt", correo, aciertos, fallos, pasapalabra, nivel);

       in.close();
    
    }
}
