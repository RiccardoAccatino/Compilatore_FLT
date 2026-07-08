package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import parser.Parser;
import parser.SyntacticException;
import scanner.Scanner;

public class TestParser {

    private static final String FOLDER_PATH = "src/test/data/testParser/";


    @Test
    void testParserCorretto1() {
        assertDoesNotThrow(() -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserCorretto1.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Il parser non dovrebbe lanciare eccezioni per testParserCorretto1.txt");
    }

    @Test
    void testParserCorretto2() {
        assertDoesNotThrow(() -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserCorretto2.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Il parser non dovrebbe lanciare eccezioni per testParserCorretto2.txt");
    }

    @Test
    void testSoloDich() {
        assertDoesNotThrow(() -> {
            Scanner s = new Scanner(FOLDER_PATH + "testSoloDich.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Il parser non dovrebbe lanciare eccezioni per testSoloDich.txt (contiene solo dichiarazioni corrette)");
    }

    @Test
    void testSoloDichPrint() {
        assertDoesNotThrow(() -> {
            Scanner s = new Scanner(FOLDER_PATH + "testSoloDichPrint.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Il parser non dovrebbe lanciare eccezioni per testSoloDichPrint.txt (la sintassi della print è corretta, l'errore semantico verrà catturato dal type checker)");
    }

    @Test
    void testParserEcc_0() {
        // Contenuto: " a ;"
        assertThrows(SyntacticException.class, () -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserEcc_0.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Deve fallire: un programma non può iniziare direttamente con un ID senza tipo o operatore.");
    }

    @Test
    void testParserEcc_1() {
        // Contenuto: "int a;\r\na = 5 + * 3;"
        assertThrows(SyntacticException.class, () -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserEcc_1.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Deve fallire: ci sono due operatori consecutivi (+ e *) nell'espressione.");
    }

    @Test
    void testParserEcc_2() {
        // Contenuto: "\r\na = 5;\r\n1 a;"
        assertThrows(SyntacticException.class, () -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserEcc_2.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Deve fallire: un'istruzione (Statement) non può iniziare con un numero (1).");
    }

    @Test
    void testParserEcc_3() {
        // Contenuto: "int a;\r\na + 5;"
        assertThrows(SyntacticException.class, () -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserEcc_3.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Deve fallire: manca l'operatore di assegnamento (es. '=' o '+=') dopo l'identificatore 'a'.");
    }

    @Test
    void testParserEcc_4() {
        // Contenuto: "int a;\r\nprint 1;"
        assertThrows(SyntacticException.class, () -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserEcc_4.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Deve fallire: l'istruzione 'print' deve essere seguita da un identificatore (ID), non da un numero.");
    }

    @Test
    void testParserEcc_5() {
        // Contenuto: "int a;\r\na = 5;\r\nfloat 4;"
        assertThrows(SyntacticException.class, () -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserEcc_5.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Deve fallire: la parola chiave del tipo 'float' deve essere seguita da un identificatore (ID), non da un numero (4).");
    }

    @Test
    void testParserEcc_6() {
        // Contenuto: "int float;"
        assertThrows(SyntacticException.class, () -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserEcc_6.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Deve fallire: non si può usare una parola chiave ('float') come nome per un identificatore.");
    }

    @Test
    void testParserEcc_7() {
        // Contenuto: "float = 3.5;"
        assertThrows(SyntacticException.class, () -> {
            Scanner s = new Scanner(FOLDER_PATH + "testParserEcc_7.txt");
            Parser p = new Parser(s);
            p.parse();
        }, "Deve fallire: manca l'identificatore (ID) della variabile tra la dichiarazione del tipo 'float' e l'operatore di assegnamento '='.");
    }
}