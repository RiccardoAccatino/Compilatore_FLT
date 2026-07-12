package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ast.NodeProgram;
import parser.Parser;
import scanner.Scanner;
import symbolTable.SymbolTable;
import visitor.TipoTD;
import visitor.TypeCheckingVisitor;
import visitor.TypeDescriptor;

public class TestTypeChecking {


    @Test
    public void testDicRipetute() throws Exception {
        SymbolTable.init();
        Scanner scanner = new Scanner("src/test/data/testTypeChecking/1_dicRipetute.txt");
        Parser parser = new Parser(scanner);
        NodeProgram nP = parser.parse();

        TypeCheckingVisitor tcVisit = new TypeCheckingVisitor();
        nP.accept(tcVisit);
        TypeDescriptor res = tcVisit.getResType();

        assertEquals(TipoTD.ERROR, res.getTipo(), "Dovrebbe dare errore per dichiarazione ripetuta");
    }

    @Test
    public void testIdNonDec1() throws Exception {
        SymbolTable.init();
        Scanner scanner = new Scanner("src/test/data/testTypeChecking/2_idNonDec.txt");
        Parser parser = new Parser(scanner);
        NodeProgram nP = parser.parse();

        TypeCheckingVisitor tcVisit = new TypeCheckingVisitor();
        nP.accept(tcVisit);
        TypeDescriptor res = tcVisit.getResType();

        assertEquals(TipoTD.ERROR, res.getTipo(), "Dovrebbe dare errore per variabile non dichiarata nella print");
    }

    @Test
    public void testIdNonDec2() throws Exception {
        SymbolTable.init();
        Scanner scanner = new Scanner("src/test/data/testTypeChecking/3_idNonDec"); 
        Parser parser = new Parser(scanner);
        NodeProgram nP = parser.parse();

        TypeCheckingVisitor tcVisit = new TypeCheckingVisitor();
        nP.accept(tcVisit);
        TypeDescriptor res = tcVisit.getResType();

        assertEquals(TipoTD.ERROR, res.getTipo(), "Dovrebbe dare errore per variabile 'c' usata ma non dichiarata");
    }

    @Test
    public void testTipoNonCompatibile() throws Exception {
        SymbolTable.init();
        Scanner scanner = new Scanner("src/test/data/testTypeChecking/4_tipoNonCompatibile.txt");
        Parser parser = new Parser(scanner);
        NodeProgram nP = parser.parse();

        TypeCheckingVisitor tcVisit = new TypeCheckingVisitor();
        nP.accept(tcVisit);
        TypeDescriptor res = tcVisit.getResType();

        assertEquals(TipoTD.ERROR, res.getTipo(), "Dovrebbe dare errore di incompatibilità di tipo");
    }


    @Test
    public void testCorretto1() throws Exception {
        SymbolTable.init();
        Scanner scanner = new Scanner("src/test/data/testTypeChecking/5_corretto.txt");
        Parser parser = new Parser(scanner);
        NodeProgram nP = parser.parse();

        TypeCheckingVisitor tcVisit = new TypeCheckingVisitor();
        nP.accept(tcVisit);
        TypeDescriptor res = tcVisit.getResType();

        assertEquals(TipoTD.OK, res.getTipo(), "Il Type Checking dovrebbe dare esito OK. Errore: " + res.getMsg());
    }

    @Test
    public void testCorretto2() throws Exception {
        SymbolTable.init();
        Scanner scanner = new Scanner("src/test/data/testTypeChecking/6_corretto.txt");
        Parser parser = new Parser(scanner);
        NodeProgram nP = parser.parse();

        TypeCheckingVisitor tcVisit = new TypeCheckingVisitor();
        nP.accept(tcVisit);
        TypeDescriptor res = tcVisit.getResType();

        assertEquals(TipoTD.OK, res.getTipo(), "Il Type Checking dovrebbe dare esito OK. Errore: " + res.getMsg());
    }

    @Test
    public void testCorretto3() throws Exception {
        SymbolTable.init();
        Scanner scanner = new Scanner("src/test/data/testTypeChecking/7_corretto.txt");
        Parser parser = new Parser(scanner);
        NodeProgram nP = parser.parse();

        TypeCheckingVisitor tcVisit = new TypeCheckingVisitor();
        nP.accept(tcVisit);
        TypeDescriptor res = tcVisit.getResType();

        assertEquals(TipoTD.OK, res.getTipo(), "Il Type Checking dovrebbe dare esito OK. Errore: " + res.getMsg());
    }
}