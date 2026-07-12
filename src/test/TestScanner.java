package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scanner.LexicalException;
import scanner.Scanner;
import token.Token;
import token.TokenType;

class TestScanner {

	private Scanner scanner;
	
	@BeforeEach
	void setUp() throws Exception {
		scanner = new Scanner("src/test/data/testScanner/testGenerale.txt");
	}

	@Test
	void testNextToken() throws Exception {
		assertEquals(TokenType.TYINT, scanner.nextToken().getType());
		
		Token t1 = scanner.nextToken();
		assertEquals(TokenType.ID, t1.getType());
		assertEquals("temp", t1.getValore());
		assertEquals(1, t1.getRiga()); 
		
		assertEquals(TokenType.SEMI, scanner.nextToken().getType());
		
		assertEquals(TokenType.TYINT, scanner.nextToken().getType());
		
		Token t2 = scanner.nextToken();
		assertEquals(TokenType.ID, t2.getType());
		assertEquals("temp1", t2.getValore());
		
		assertEquals(TokenType.SEMI, scanner.nextToken().getType());
		
		assertEquals(TokenType.ID, scanner.nextToken().getType()); // temp
		
		Token t3 = scanner.nextToken();
		assertEquals(TokenType.OP_ASSIGN, t3.getType()); 
		assertEquals("+=", t3.getValore());
		
		Token t4 = scanner.nextToken();
		assertEquals(TokenType.FLOAT, t4.getType()); 
		assertEquals("5.", t4.getValore());
		
		assertEquals(TokenType.SEMI, scanner.nextToken().getType());
		
		assertEquals(TokenType.TYFLOAT, scanner.nextToken().getType());
		assertEquals(TokenType.ID, scanner.nextToken().getType()); // b
		assertEquals(TokenType.SEMI, scanner.nextToken().getType());
		
		assertEquals(TokenType.ID, scanner.nextToken().getType()); // b
		assertEquals(TokenType.ASSIGN, scanner.nextToken().getType()); // =
		assertEquals(TokenType.ID, scanner.nextToken().getType()); // temp1
		assertEquals(TokenType.PLUS, scanner.nextToken().getType()); // +
		
		Token t5 = scanner.nextToken();
		assertEquals(TokenType.FLOAT, t5.getType()); 
		assertEquals("3.2", t5.getValore());
		
		assertEquals(TokenType.SEMI, scanner.nextToken().getType());
		
		assertEquals(TokenType.PRINT, scanner.nextToken().getType());
		assertEquals(TokenType.ID, scanner.nextToken().getType()); // b
		assertEquals(TokenType.SEMI, scanner.nextToken().getType());
		
		assertEquals(TokenType.EOF, scanner.nextToken().getType());
	}
}