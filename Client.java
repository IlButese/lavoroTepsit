package Tris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Scanner scanner;

    public Client(String serverAddress, int port) {
        try {//giusta
            //connessione con il server
            socket = new Socket(serverAddress, port);
            System.out.println("Connesso al server " + serverAddress + ":" + port);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);

        } catch (IOException e) {
            System.err.println("Errore durante la connessione al server: " + e.getMessage());
        }
    }
    @Override
    public void run() {//da camviare usare lo switch
    	boolean partitaFinita = false;
    	String msgCod;
    	//leggi la grihlia se sei x la leggerai una volta e ti verra chiesta la mossa,
    	//se sei O la leggerai 2 volte
    	
    	//while che contrine ntutto !partita finita ovvero messaggio 2
    	//dove lkeggo il codice con uno switch
    	//switch per controll;are messaggio
    	//a seconda del messaggio agisco di conseguenza
    	
        try {
            //invio messaggi al server
            while (!partitaFinita) {
                System.out.print("Inserisci la tua mossa (formato: x,y): ");
                msgCod = reader.readLine();// Leggo il codice  
                switch (msgCod) {
                case "0":
                    System.out.println("fai mossa");
                    String input = scanner.nextLine();
                    writer.println(input);
                    break;

                case "1":
                	 // Rimuovi eventuali spazi extra e dividi la stringa usando "||" come delimitatore
                    
                    String inputMatrix = reader.readLine();
                    String[] valori = inputMatrix.split("\\|\\|");
                    // Crea la matrice 3x3
                    String[][] matrice = new String[3][3];
                    System.out.println();
                    // Assegna i valori alla matrice
                    int index = 0;
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            // Rimuovi gli spazi vuoti e assegna il valore alla matrice
                            matrice[i][j] = valori[index].trim();
                            index++;
                        }
                    }

                    // Stampa la matrice come un gioco di tris
                    stampaMatriceTris(matrice);
                    //stampaGriglia(grigliaGioco);
                    break;
                    
                case "2":
                    System.out.println("partita terminata");
                    String chiusuraPartita = reader.readLine();// Leggo la griglia
                    System.out.println("partita terminata" + chiusuraPartita);
                    partitaFinita = true;
                    break;

                default:
                    System.out.println("Opzione non valida. Riprova.");
                    break;
                }
            }
            //chiudo la connessione
            close();

        } catch (Exception e) {
            System.err.println("Errore durante la comunicazione con il server: " + e.getMessage());
        }
    }
    /*
     *  case "1":
                    System.out.println("griglia");
                    //arariva la griglia
                    String messaggioRicevuto = reader.readLine();// Leggo la griglia
                    //deframmentazione del messaggio 
            		String separator = "\\|\\|";
            		int index = 0, dim =  3;
            		String [][]grigliaGioco = new String [dim][dim];
            		// Conversione della stringa in un array
                    String[] elements = messaggioRicevuto.split(separator);
                    for (int i = 0; i < dim; i++) {
                        for (int j = 0; j < dim; j++) {
                            grigliaGioco[i][j] = elements[index++];
                        }
                    }
                    stampaGriglia(grigliaGioco);
                    break;*/
    public static void stampaMatriceTris(String[][] matrice) {
        for (int i = 0; i < matrice.length; i++) {
            for (int j = 0; j < matrice[i].length; j++) {
                // Stampa il valore della cella
                System.out.print(matrice[i][j]);
                if (j < matrice[i].length - 1) {
                    System.out.print(" | "); // Separatore tra le colonne
                }
            }
            System.out.println();
            if (i < matrice.length - 1) {
                System.out.println("---------"); // Separatore tra le righe
            }
        }
    }
    private static void stampaGriglia(String[][] griglia) {
        System.out.println("   0   1   2");
        for (int i = 0; i < griglia.length; i++) {
            System.out.print(i + " "); // Indice riga
            for (int j = 0; j < griglia[i].length; j++) {
                System.out.print(" " + (griglia[i][j].equals(" ") ? " " : griglia[i][j]) + " ");
                if (j < griglia[i].length - 1) {
                    System.out.print("|"); // Separatore di colonna
                }
            }
            System.out.println();
            if (i < griglia.length - 1) {
                System.out.println("  ---+---+---"); // Separatore di riga
            }
        }
    }

    private void close() {//giusta comoda
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (scanner != null) scanner.close();
            System.out.println("Connessione chiusa.");
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }

    public static void main(String[] args) {//giusto
        String serverAddress = "localhost";
        int port = 12345;

        // Creazione client e avvio thread
        Client client = new Client(serverAddress, port);
        Thread clientThread = new Thread(client);
        clientThread.start();
    }

}