package Tris;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList; 

/**
 * Gioco Tris Multiplayer Locale
 * 
 * @author Giulio Scarpellini
 * @author Raffaele Restifo
 * @author Flavio Alushi
 * 
 * /
 * 
 * 
//protocollo di comunicazione 
/* 0 --> inserisci mossa
 * 1 --> invio tabella
 * 0 --> partita finita + esito
*/

public class Server {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 12345; // Porta su cui il server ascolta 
		ServerSocket server;
		try {
			server = new ServerSocket(port);//crea il socket server, entita che serve per creare le socket lato server per la connessione
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Server avviato. In attesa di connessioni...");
		System.out.println("Server in ascolto su "+server.getLocalSocketAddress());
		System.out.println("Server in ascolto su "+server.getInetAddress()+":"+server.getLocalPort());
		try {
			Socket s1 = null;
			Socket s2 = null;
			ArrayList<Thread> thread = new ArrayList<Thread>();//creo un array list di socket
			int counter = 0;
			//----accetta le connessioni e le da in pasto ai thread
			while(true) {//attesa connessione
				s1 = server.accept();
				s2 = server.accept();
				thread.add(new Thread(new GestoreAvvio(s1 ,s2)));
				Thread partitaThread = new Thread(new GestoreAvvio(s1, s2));
                thread.add(partitaThread); // Aggiunge il thread alla lista 
                partitaThread.start(); // Avvia il thread
				
			}
			
			
		}catch (Exception e) {
            System.err.println("Errore del server: " + e.getMessage());
            e.printStackTrace();
        }        
	}
	
	
}
class GestoreAvvio implements Runnable {//gestore avvio partita
    Socket s1;
    Socket s2;
    BufferedReader reader1;
    PrintWriter writer1;
    BufferedReader reader2;
    PrintWriter writer2;
    
    public GestoreAvvio(Socket s1, Socket s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    @Override
    public void run() {//ogni thread partita gestisce, la partita tra i due client
    	System.out.println("Thread Server partita avviato");
    	
    	try {
            reader1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            writer1 = new PrintWriter(s1.getOutputStream(), true);
            reader2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
            writer2 = new PrintWriter(s2.getOutputStream(), true);

            // Fase di inizializzazione: invio messaggio di inizio partita ai client
            writer1.println("Benvenuto nel gioco di Tris! Sarai il giocatore X.");
            writer2.println("Benvenuto nel gioco di Tris! Sarai il giocatore O.");
                        
            // Creazione della griglia di gioco vuota
            int dim = 3;
            //char x;
            String[][] grigliaGioco = new String[dim][dim];
            for (String[] r : grigliaGioco) {
                for (int j = 0; j < r.length; j++) {
                    r[j] = "n"; // Imposta le caselle vuote (n sta per 'nessuna mossa')
                }
            }
            inviaGriglia(grigliaGioco);
            
            char giocatoreCorrente = 'X'; // Giocatore X inizia
            boolean partitaFinita = false;
           
            while (!partitaFinita) {
                writer1.println("Tuo turno! Inserisci la mossa (formato: x,y): ");
                writer1.println("0");//codice di richiesta mossa
                String mossa1 = reader1.readLine();// Leggo la mossa del giocatore 
                
                // Verifica della mossa del giocatore 1
                if (isMossaValida(mossa1, grigliaGioco)) {//controlla la validita' della mossa, se valida allora:
                    aggiornaGriglia(mossa1, grigliaGioco, 'X'); // Aggiorno la griglia con la mossa di X
                    inviaGriglia(grigliaGioco);  // Invia la griglia aggiornata a entrambi i giocatori

                    // Controllo se il giocatore X ha vinto
                    if (verificaVittoria(grigliaGioco, 'X')) {
                    	writer1.println("2");//messaggio comunicazione
                    	writer1.println("Hai vinto!");
                    	writer2.println("2");
                    	writer2.println("Il giocatore X ha vinto!");
                        partitaFinita = true;
                        break;
                    }

                    // Verifica pareggio
                    if (isPareggio(grigliaGioco)) {
                    	writer1.println("2");
                        writer1.println("La partita è finita in pareggio.");
                        writer2.println("2");
                        writer2.println("La partita è finita in pareggio.");
                        partitaFinita = true;
                        break;
                    }

                    
                    giocatoreCorrente = 'O'; // Passa il turno al giocatore O
                } else {
                    writer1.println("Mossa non valida! Riprova.");
                }

                if (partitaFinita) break;

                // gioca il g2 (giocatore O)
                writer2.println("Tuo turno! Inserisci la mossa (formato: x,y): ");
                
                writer2.println("0");//mesaggio comunicazione
                String mossa2 = reader2.readLine();  // leggi la mossa del giocatore O

                // Verifica della mossa del giocatore 2
                if (isMossaValida(mossa2, grigliaGioco)) {
                    aggiornaGriglia(mossa2, grigliaGioco, 'O'); // Aggiorna la griglia con la mossa di O
                    inviaGriglia(grigliaGioco);  // Invia la griglia aggiornata a entrambi i giocatori

                    // Controlla se il giocatore O ha vinto
                    if (verificaVittoria(grigliaGioco, 'O')) {
                    	writer2.println("2");
                    	writer2.println("Hai vinto!");
                        writer1.println("2");
                        writer1.println("Il giocatore O ha vinto!");
                        partitaFinita = true;
                        break;
                    }

                    // Verifica pareggio
                    if (isPareggio(grigliaGioco)) {
                    	writer1.println("2");
                        writer1.println("La partita è finita in pareggio.");
                        writer2.println("2");
                        writer2.println("La partita è finita in pareggio.");
                        partitaFinita = true;
                        break;
                    }

                    // Cambia turno
                    giocatoreCorrente = 'X'; // Passa il turno al giocatore X
                } else {
                    writer2.println("Mossa non valida! Riprova.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // invia la griglia di gioco aggiornata a entrambi i client
    private void inviaGriglia(String[][] grigliaGioco) {
        StringBuilder messaggioDaInvio = new StringBuilder();
        for (int i = 0; i < grigliaGioco.length; i++) {
            for (int j = 0; j < grigliaGioco[i].length; j++) {
                messaggioDaInvio.append(grigliaGioco[i][j]);

                // Aggiunge il separatore solo se non è l'ultima cella della riga
                if (j < grigliaGioco[i].length - 1) {
                    messaggioDaInvio.append("||");
                }
            }
            // Aggiunge il separatore tra le righe (solo se non è l'ultima riga)
            if (i < grigliaGioco.length - 1) {
                messaggioDaInvio.append("||");
            }
        }

        // Invia dei dati ai client
        writer1.println("1"); //codice di invio griglia, comunicazione
        writer2.println("1"); //codice di invio griglia
        writer1.println(messaggioDaInvio.toString()); //invia la griglia al primo client
        writer2.println(messaggioDaInvio.toString()); //invia la griglia al secondo client
    }

    private boolean isMossaValida(String mossa, String[][] grigliaGioco) {
        try {
            String[] coordinate = mossa.split(","); // "0,1" ==> [0, 1] usa la virgola come separatore di tocken e poi li mette come valori di unu array
            int x = Integer.parseInt(coordinate[0]);//trasforma da stringa ad intero
            int y = Integer.parseInt(coordinate[1]);//trasforma da stringa ad intero

            // Verifica se la mossa è valida
            if (x >= 0 && x < 3 && y >= 0 && y < 3 && grigliaGioco[x][y].equals("n")) {//una matriche controlla che oil valore ci stiae che la cella selezionata sia libera
            	
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {//eccezione per non compatibilita di tipo 
            return false;
        }
    }

    // aggiorna la griglia con la mossa del giocatore
    private void aggiornaGriglia(String mossa, String[][] grigliaGioco, char giocatore) {
        String[] coordinate = mossa.split(",");
        int x = Integer.parseInt(coordinate[0]);
        int y = Integer.parseInt(coordinate[1]);
        grigliaGioco[x][y] = String.valueOf(giocatore);//essendo una matrice dis stringhe lui vede solo quelle quindi con value off lo andiamo trasfirmare
    }

    // verifica se un giocatore ha vinto
    private boolean verificaVittoria(String[][] grigliaGioco, char giocatore) {
        // Controlla righe, colonne e diagonali
        for (int i = 0; i < 3; i++) {
            if (grigliaGioco[i][0].equals(String.valueOf(giocatore)) && grigliaGioco[i][1].equals(String.valueOf(giocatore)) && grigliaGioco[i][2].equals(String.valueOf(giocatore))) {
                return true;
            }
            if (grigliaGioco[0][i].equals(String.valueOf(giocatore)) && grigliaGioco[1][i].equals(String.valueOf(giocatore)) && grigliaGioco[2][i].equals(String.valueOf(giocatore))) {
                return true;
            }
        }
        if (grigliaGioco[0][0].equals(String.valueOf(giocatore)) && grigliaGioco[1][1].equals(String.valueOf(giocatore)) && grigliaGioco[2][2].equals(String.valueOf(giocatore))) {
            return true;
        }
        if (grigliaGioco[0][2].equals(String.valueOf(giocatore)) && grigliaGioco[1][1].equals(String.valueOf(giocatore)) && grigliaGioco[2][0].equals(String.valueOf(giocatore))) {
            return true;
        }
        return false;
    }

    // verifica se la partita è finita in pareggio, tutte le celle sono occupate senza vittoria
    private boolean isPareggio(String[][] grigliaGioco) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grigliaGioco[i][j].equals("n")) {
                    return false;
                }
            }
        }
        return true;
    }


}