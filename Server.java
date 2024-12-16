package Tris;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList; // import the ArrayList class

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
			server = new ServerSocket(port);//crea il socket server, entita che serve per creare le socket lato server la connessione
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Server avviato. In attesa di connessioni...");
		System.out.println("Server in ascolto su "+server.getLocalSocketAddress());
		System.out.println("Server in ascolto su "+server.getInetAddress()+":"+server.getLocalPort());
		try {
			//ArrayList<Socket> links = new ArrayList<Socket>();//creo un array list di socket
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
                thread.add(partitaThread); // Aggiunge il thread alla lista (facoltativo)
                partitaThread.start(); // Avvia il thread
				
			}
			
			
		}catch (Exception e) {
            System.err.println("Errore del server: " + e.getMessage());
            e.printStackTrace();
        }        
	}
	//defionisco i thread che gestiscono la partita
	
}
class GestoreAvvio implements Runnable {
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
    public void run() {
    	System.out.print("ciao");
    	//String msgCode; nn serve secondo me 
    	try {
            reader1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            writer1 = new PrintWriter(s1.getOutputStream(), true);
            reader2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
            writer2 = new PrintWriter(s2.getOutputStream(), true);

            // Fase di inizializzazione: invio messaggio di inizio partita ai client
            writer1.println("Benvenuto nel gioco di Tris! Sarai il giocatore X.");
            writer2.println("Benvenuto nel gioco di Tris! Sarai il giocatore O.");
            //invio griglia
            //chiedo la mossa 
            //ricevo mossa mossa
            //controllo la validita'
            //aggiorno la griglia
            // Faccio giocare il g1 (giocatore X)
            
            // Creazione della griglia di gioco vuota
            int dim = 3;
            //char x;
            String[][] grigliaGioco = new String[dim][dim];
            for (String[] r : grigliaGioco) {
                for (int j = 0; j < r.length; j++) {
                    r[j] = "n"; // Imposto le caselle vuote (n sta per 'nessuna mossa')
                }
            }
            inviaGriglia(grigliaGioco);//invia ad entrambi ,falla che invia solo ad una
            // Variabili per la gestione del turno e dello stato del gioco
            //fai della leogica per far si che mandi la griglia achi serve non a tutti
            
            
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
                    	writer1.println("2");
                    	writer1.println("Hai vinto!");//manda mess 2
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

                    // Cambio turno da sistemare
                    giocatoreCorrente = 'O'; // Passa il turno al giocatore O
                } else {
                    writer1.println("Mossa non valida! Riprova.");
                }

                // Partita finita? (Condizione già verificata nel ciclo)
                if (partitaFinita) break;

                // Faccio giocare il g2 (giocatore O)
                writer2.println("Tuo turno! Inserisci la mossa (formato: x,y): ");
                
                writer2.println("0");
                String mossa2 = reader2.readLine();  // Leggo la mossa del giocatore O

                // Verifica della mossa del giocatore 2
                if (isMossaValida(mossa2, grigliaGioco)) {
                    aggiornaGriglia(mossa2, grigliaGioco, 'O'); // Aggiorno la griglia con la mossa di O
                    inviaGriglia(grigliaGioco);  // Invia la griglia aggiornata a entrambi i giocatori

                    // Controllo se il giocatore O ha vinto
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

                    // Cambio turno
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
    private void inviaGriglia(String[][] grigliaGioco) {//fai per chapire
    	String messaggioDaInvio = "||";
		for (int i = 0; i < grigliaGioco.length; i++) {
            for (int j = 0; j < grigliaGioco[i].length; j++) {
                messaggioDaInvio += grigliaGioco[i][j];

                // Aggiungi il separatore solo se non è l'ultimo elemento
                if (i != grigliaGioco.length - 1 || j != grigliaGioco[i].length - 1) {
                	messaggioDaInvio += "||";
                }
            }
		}
		writer1.println("1");//codice di invio griglia
		writer2.println("1");//codice di invio griglia
		writer1.println(messaggioDaInvio);//manda la griglia
		writer2.println(messaggioDaInvio);//manda la griglia
        
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

    // verifica se la partita è finita in pareggio (tutte le celle sono occupate senza vittoria)
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
/*
// ROBBA MIA
class GestoreAvvio implements Runnable{
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
	public void run() {
		// TODO Auto-generated method stub
		 // Esegui il Runnable come thread
 
		try {
			
			reader1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));//buffer reader bufferizza input strean reader, quest'ultimo crea un stream ma ha bisogno di una canale che ce l'ho da la socket
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			 writer1 = new PrintWriter(s1.getOutputStream(), true);//print writer crea uno strument oche ci permette di  scrivere in un canaloe che ci viene dato dalla socket
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			 reader2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			 writer2 = new PrintWriter(s2.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//gestione di gioco
		
		int dim = 3;
		String [][]grigliaGioco = new String [dim][dim];
		for(String[] r : grigliaGioco) {//per ogni riga di tipo string array, in griglia//for each
			for(String elemento : r ) {//array r per ogni elemento di tipo string in r
				elemento = "n";//sarebbe come tutta la tabella a null
			}
		}
		String messaggioDaInvio = "1||";
		for (int i = 0; i < grigliaGioco.length; i++) {
            for (int j = 0; j < grigliaGioco[i].length; j++) {
                messaggioDaInvio += grigliaGioco[i][j];

                // Aggiungi il separatore solo se non è l'ultimo elemento
                if (i != grigliaGioco.length - 1 || j != grigliaGioco[i].length - 1) {
                	messaggioDaInvio += "||";
                }
            }
		}
		//deframmentazione del messaggio
		String messaggioRicevuto = messaggioDaInvio;
		String separator = "\\|\\|";
		int index = 0;
		// Conversione della stringa in un array
        String[] elements = messaggioRicevuto.split(separator);
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                grigliaGioco[i][j] = elements[index++];
            }
        }
		
		//faccio giocare il g1
        
		//partita finita?
        
		//faccio giocare il g2
        
		//partita finita?
		//...
		
		writer1.println(messaggioDaInvio);		
		//message = reader.readLine();//riceve mossa 
		//switch per riconoscere la mossa
		//inserisci la mossa
		//controllo se vinto
		//madno grtiglia aggiornata all'altro giocatore
		
	}
	//fau metodo di crea mesaggio 
	//verififca vittoria metodo da fare
	public void messaggioInArrivo() {
		
	}

	public void messaggioInInvio() {
			
	}

}
*/
