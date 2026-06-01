package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import token.Token;
import token.TokenType;

class TestToken {

	@Test
	void testCostruttoriEToString() {
		Token t1 = new Token(TokenType.TYINT, 1);
		assertEquals("<TYINT,r:1>", t1.toString());
		
		Token t2 = new Token(TokenType.ID, "tempa", 1);
		assertEquals("<ID,r:1,tempa>", t2.toString());
		
		Token t3 = new Token(TokenType.INT, "5", 1);
		assertEquals("<INT,r:1,5>", t3.toString());
	}
}