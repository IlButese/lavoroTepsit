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
        try {
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
    public void run() {
    	boolean partitaFinita = false;
    	String msgCod;
    	
        try {
            //invio messaggi al server
            while (!partitaFinita) {
                msgCod = reader.readLine();// Legge il codice  
                switch (msgCod) {
                case "0"://messaggio di comunicazione '0' la mossa
                    System.out.println("fai mossa");
                    String input = scanner.nextLine();
                    writer.println(input);
                    break;

                case "1"://messaggio di comunicazione '1' la griglia
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

                    stampaMatriceTris(matrice);
                    System.out.print("Inserisci la tua mossa (formato: x,y): ");

                    break;
                    
                case "2"://messaggio di comunicazione '2' terminazione partita
                    System.out.println("partita terminata ");
                    String chiusuraPartita = reader.readLine();// Legge la griglia
                    System.out.println("partita terminata" + chiusuraPartita);
                    partitaFinita = true;
                    break;

                default:
                    break;
                }
            }
            //chiude la connessione
            close();

        } catch (Exception e) {
            System.err.println("Errore durante la comunicazione con il server: " + e.getMessage());
        }
    }
    public static void stampaMatriceTris(String[][] matrice) {
        System.out.println("   0   1   2"); 
        for (int i = 0; i < matrice.length; i++) {
            System.out.print(i + " "); 

            for (int j = 0; j < matrice[i].length; j++) {
                //stampa del valore della cella, sostituendo lo spazio vuoto con un trattino o un simbolo per migliorare la leggibilità
                System.out.print(" " + (matrice[i][j].equals(" ") ? " " : matrice[i][j]) + " ");
                if (j < matrice[i].length - 1) {
                    System.out.print("|"); 
                }
            }
            System.out.println(); 

            if (i < matrice.length - 1) {
                System.out.println("  ---+---+---"); 
            }
        }
        System.out.println(); // Riga finale per separare la griglia dal prompt successivo
    }


    private void close() {//chiusura della comunicazione 
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

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 12345;

        // Creazione client e avvio thread
        Client client = new Client(serverAddress, port);
        Thread clientThread = new Thread(client);
        clientThread.start();
    }


}