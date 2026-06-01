package token;

public class Token {

	private TokenType type;
	private String valore;
	private int riga;
	
	public Token() {
		super();
	}
	
	public Token(TokenType type, int riga) {
		super();
		this.type = type;
		this.riga = riga;
	}
	
	public Token(TokenType type, String valore, int riga) {
		super();
		this.type = type;
		this.valore = valore;
		this.riga = riga;
	}

	public TokenType getType() {
		return type;
	}
	
	public String getValore() {
		return valore;
	}
	
	public int getRiga() {
		return riga;
	}
	
	@Override
	public String toString() {
		if (valore == null) {
			return "<" + type + ",r:" + riga + ">";
		} else {
			return "<" + type + ",r:" + riga + "," + valore + ">";
		}
	}
}