package scanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import token.*;

public class Scanner {
	final char EOF = (char) -1; 
	private int riga;
	private PushbackReader buffer;
	private Token nextTk; // Campo aggiunto per la gestione di peekToken()

	// skpChars: insieme caratteri di skip (include EOF) e inizializzazione
	private Set<Character> skpChars;
	
	// letters: insieme lettere 
	private Set<Character> letters;
	// digits: cifre 
	private Set<Character> digits;

	// operTkType: mapping fra caratteri '+', '-', '*', '/'  e il TokenType corrispondente
	private Map<Character, TokenType> operTkType;
	// delimTkType: mapping fra caratteri '=', ';' e il e il TokenType corrispondente
	private Map<Character, TokenType> delimTkType;

	// keyWordsTkType: mapping fra le stringhe "print", "float", "int" e il TokenType corrispondente
	private Map<String, TokenType> keyWordsTkType;

	public Scanner(String fileName) throws FileNotFoundException {

		this.buffer = new PushbackReader(new FileReader(fileName));
		riga = 1;
		this.nextTk = null;
		
		skpChars = new HashSet<>();
		skpChars.add(' ');
		skpChars.add('\n');
		skpChars.add('\t');
		skpChars.add('\r');
		skpChars.add(EOF);

		letters = new HashSet<>();
		for (char c = 'a'; c <= 'z'; c++) {
			letters.add(c);
		}

		digits = new HashSet<>();
		for (char c = '0'; c <= '9'; c++) {
			digits.add(c);
		}

		operTkType = new HashMap<>();
		operTkType.put('+', TokenType.PLUS);
		operTkType.put('-', TokenType.MINUS);
		operTkType.put('*', TokenType.TIMES);
		operTkType.put('/', TokenType.DIVIDE);

		delimTkType = new HashMap<>();
		delimTkType.put('=', TokenType.ASSIGN);
		delimTkType.put(';', TokenType.SEMI);

		keyWordsTkType = new HashMap<>();
		keyWordsTkType.put("print", TokenType.PRINT);
		keyWordsTkType.put("float", TokenType.TYFLOAT);
		keyWordsTkType.put("int", TokenType.TYINT);
	}
	
	public Token nextToken() throws LexicalException {
		if (nextTk != null) {
			Token token = nextTk;
			nextTk = null;
			return token;
		}

		try {
     			char nextChar = peekChar(); 

	
			while (skpChars.contains(nextChar) && nextChar != EOF) {
				char consumed = readChar();
				if (consumed == '\n') {
					riga++;
				}
				nextChar = peekChar();
			}
			if (nextChar == EOF) {
				return new Token(TokenType.EOF, riga);
			}

			if (letters.contains(nextChar)) {
				return scanId();
			}
			if (operTkType.containsKey(nextChar) || delimTkType.containsKey(nextChar)) {
				return scanOperator();
			}

			if (digits.contains(nextChar)) {
				return scanNumber();
			}

			char illegalChar = readChar(); 
			throw new LexicalException("Carattere non legale '" + illegalChar + "' trovato alla riga " + riga);
			
		} catch (IOException e) {
		
			throw new LexicalException("Errore di I/O durante la lettura: " + e.getMessage());
		}
	}

	public Token peekToken() throws LexicalException {
		if (nextTk == null) {
			nextTk = nextToken();
		}
		return nextTk;
	}

	private Token scanId() throws IOException {
		StringBuilder sb = new StringBuilder();
		char c = peekChar();
		
		while (letters.contains(c) || digits.contains(c)) {
			sb.append(readChar());
			c = peekChar();
		}
		
		String value = sb.toString();
		if (keyWordsTkType.containsKey(value)) {
			return new Token(keyWordsTkType.get(value), riga);
		}
		
		return new Token(TokenType.ID, value, riga);
	}
	
	private Token scanOperator() throws IOException {
		char c = readChar(); 
		
		if (operTkType.containsKey(c)) {
			if (peekChar() == '=') {
				readChar(); 
				return new Token(TokenType.OP_ASSIGN, String.valueOf(c) + "=", riga);
			}
			return new Token(operTkType.get(c), riga);
		}
		
		if (delimTkType.containsKey(c)) {
			return new Token(delimTkType.get(c), riga);
		}
		
		return null;
	}
		
	private Token scanNumber() throws IOException, LexicalException {
		StringBuilder sb = new StringBuilder();
		char c = peekChar();
		
		while (digits.contains(c)) {
			sb.append(readChar());
			c = peekChar();
		}
		if (c == '.') {
			sb.append(readChar()); 
			c = peekChar();
			int decimalCount = 0;
			
			while (digits.contains(c)) {
				sb.append(readChar());
				decimalCount++;
				c = peekChar();
			}
			
			if (decimalCount > 5) {
				throw new LexicalException("Errore alla riga " + riga + ": float con piu' di 5 cifre decimali");
			}
			
			return new Token(TokenType.FLOAT, sb.toString(), riga);
		}
		
		return new Token(TokenType.INT, sb.toString(), riga);
	}

	private char readChar() throws IOException {
		return ((char) this.buffer.read());
	}

	private char peekChar() throws IOException {
		char c = (char) buffer.read();
		buffer.unread(c);
		return c;
	}
}