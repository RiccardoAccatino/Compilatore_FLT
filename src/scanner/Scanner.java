package scanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

import token.*;

public class Scanner {
	final char EOF = (char) -1; 
	private int riga;
	private PushbackReader buffer;

	// skpChars: insieme caratteri di skip (include EOF) e inizializzazione
	
	// letters: insieme lettere 
	// digits: cifre 

	// operTkType: mapping fra caratteri '+', '-', '*', '/'  e il TokenType corrispondente
	// delimTkType: mapping fra caratteri '=', ';' e il e il TokenType corrispondente

	// keyWordsTkType: mapping fra le stringhe "print", "float", "int" e il TokenType  corrispondente

	public Scanner(String fileName) throws FileNotFoundException {

		this.buffer = new PushbackReader(new FileReader(fileName));
		riga = 1;
		// inizializzare campi che non hanno inizializzazione
	}
	
  // nextToken ritorna il prossimo token nel file di input e legge 
  // i caratteri del token ritornato (avanzando fino al carattere
  // successivo all'ultimo carattere del token)
	public Token nextToken()  {

		// nextChar contiene il prossimo carattere dell'input (non consumato).
		char nextChar = peekChar(); //Catturate l'eccezione IOException e 
		       // ritornate una LexicalException che la contiene

		// Avanza nel buffer leggendo i carattere in skipChars
		// incrementando riga se leggi '\n'.
		// Se raggiungi la fine del file ritorna il Token EOF


		// Se nextChar e' in letters
		// return scanId()
		// che deve generare o un Token ID o parola chiave

		// Se nextChar e' o in operators oppure delimitatore
		// ritorna il Token associato con l'operatore o il delimitatore
		// Attenzione agli operatori di assegnamento!

		// Se nextChar e' ; o = 
		// ritorna il Token associato
		

		// Se nextChar e' in numbers
		// return scanNumber()
		// che legge sia un intero che un float e ritorna il Token INUM o FNUM
		// i caratteri che leggete devono essere accumulati in una stringa
		// che verra' assegnata al campo valore del Token

		// Altrimenti il carattere NON E' UN CARATTERE LEGALE sollevate una
		// eccezione lessicale dicendo la riga e il carattere che la hanno
		// provocata. 


	}

	// private Token scanId()
	
	// private Token scanOperator()
		
	// private Token scanNumber()

	

	private char readChar() throws IOException {
		return ((char) this.buffer.read());
	}

	private char peekChar() throws IOException {
		char c = (char) buffer.read();
		buffer.unread(c);
		return c;
	}
}
