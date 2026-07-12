package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import scanner.Scanner;
import parser.Parser;
import ast.NodeProgram;
import visitor.TypeCheckingVisitor;
import visitor.CodeGeneratorVisitor;
import visitor.Registri;
import symbolTable.SymbolTable;
import visitor.TipoTD;

public class TestCodeGenerator {

    /**
     * Metodo di supporto per eseguire l'intera pipeline di compilazione
     * e restituire il CodeGeneratorVisitor con i risultati.
     */
	/**
     * Metodo di supporto per eseguire l'intera pipeline di compilazione
     * e restituire il CodeGeneratorVisitor con i risultati.
     */
    private CodeGeneratorVisitor runCodeGeneratorPipeline(String filePath) throws Exception {
        // Inizializza la Symbol Table
        SymbolTable.init();
        
        // Resetta il contatore dei registri ('a') per avere risultati deterministici ad ogni test
        Registri.reset(); 

        // Genera i token e crea l'AST
        Scanner scanner = new Scanner(filePath);
        Parser parser = new Parser(scanner);
        NodeProgram ast = parser.parse();

        // 1. Esegue il Type Checking (fondamentale per mappare DIV in DIV_FLOAT dove necessario)
        TypeCheckingVisitor typeChecker = new TypeCheckingVisitor();
        ast.accept(typeChecker);

        // Verifica che non ci siano errori di tipo prima di generare il codice
        assertEquals(TipoTD.OK, typeChecker.getResType().getTipo(), "Non ci devono essere errori di type checking prima della generazione del codice.");

        // 2. Esegue la generazione del codice dc
        CodeGeneratorVisitor codeGenerator = new CodeGeneratorVisitor();
        ast.accept(codeGenerator);

        return codeGenerator;
    }

    @Test
    public void testAssign() throws Exception {
        String file = "src/test/data/testCodeGenerator/1_assign.txt";
        CodeGeneratorVisitor cgv = runCodeGeneratorPipeline(file);
        
        // Verifica che non ci siano errori
        assertEquals("", cgv.getLog());
        
        // Verifica il codice dc generato
        String expectedCode = "1 6 / sa la p P";
        assertEquals(expectedCode, cgv.getCodiceDc());
    }

    @Test
    public void testDivisioni() throws Exception {
        String file = "src/test/data/testCodeGenerator/2_divsioni.txt";
        CodeGeneratorVisitor cgv = runCodeGeneratorPipeline(file);
        
        assertEquals("", cgv.getLog());
        
        // Verifico l'output esatto richiesto per 2_divisioni.txt specificato nelle slide
        String expectedCode = "0 sa la 1 + sa 6 sb 1.0 6 5 k / 0 k la lb / + sc la p P lb p P lc p P";
        assertEquals(expectedCode, cgv.getCodiceDc());
    }

    @Test
    public void testGenerale() throws Exception {
        String file = "src/test/data/testCodeGenerator/3_generale.txt";
        CodeGeneratorVisitor cgv = runCodeGeneratorPipeline(file);
        
        assertEquals("", cgv.getLog());
        
        // Output atteso tracciando le variabili e i registri (i=a, f=b, flo=c)
        String expectedCode = "5 3 + sa la 0.5 + sb la p P lb 4 5 k / 0 k sb lb p P lb 1 - sc lc lb * sc lc p P";
        assertEquals(expectedCode, cgv.getCodiceDc());
    }

    @Test
    public void testRegistriFiniti() throws Exception {
        String file = "src/test/data/testCodeGenerator/4_registriFiniti.txt";
        CodeGeneratorVisitor cgv = runCodeGeneratorPipeline(file);
        
        // In questo caso, DOVREMMO avere un log di errore valorizzato (registri > 'z')
        assertNotEquals("", cgv.getLog(), "Mi aspetto un errore per esaurimento dei registri.");
        assertTrue(cgv.getLog().contains("Errore") || cgv.getLog().toLowerCase().contains("registri"));
        
        // Il codice generato deve contenere solo le istruzioni visitate PRIMA di finire i registri
        String expectedCode = "6 2 / sa la p P";
        assertEquals(expectedCode, cgv.getCodiceDc());
    }
}