package parser;

public class SyntacticException extends Exception {

    /**
     * Costruttore per errori puramente sintattici (es. token inatteso).
     * @param message Messaggio che indica la riga dell'errore e la causa.
     */
    public SyntacticException(String message) {
        super(message);
    }

    /**
     * Costruttore per il chaining delle eccezioni.
     * Utilizzato quando si cattura una LexicalException sollevata dallo Scanner.
     * @param message Messaggio descrittivo dell'errore.
     * @param cause L'eccezione originale catturata (es. LexicalException).
     */
    public SyntacticException(String message, Throwable cause) {
        super(message, cause);
    }
}