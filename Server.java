package Tris;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList; // import the ArrayList class



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
				
			}
			
			
		}catch (Exception e) {
            System.err.println("Errore del server: " + e.getMessage());
            e.printStackTrace();
        }        
	}
	//defionisco i thread che gestiscono la partita
	
}
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
		/*
		 * 0 attesa AVVERSARIO
		 * 1 Griglia di gioco
		 * 2 partita finita
		 * */
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
