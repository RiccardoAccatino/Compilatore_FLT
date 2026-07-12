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

    private CodeGeneratorVisitor runCodeGeneratorPipeline(String filePath) throws Exception {
        SymbolTable.init();
        
        Registri.reset(); 

        Scanner scanner = new Scanner(filePath);
        Parser parser = new Parser(scanner);
        NodeProgram ast = parser.parse();

        TypeCheckingVisitor typeChecker = new TypeCheckingVisitor();
        ast.accept(typeChecker);

        assertEquals(TipoTD.OK, typeChecker.getResType().getTipo(), "Non ci devono essere errori di type checking prima della generazione del codice.");

        CodeGeneratorVisitor codeGenerator = new CodeGeneratorVisitor();
        ast.accept(codeGenerator);

        return codeGenerator;
    }

    @Test
    public void testAssign() throws Exception {
        String file = "src/test/data/testCodeGenerator/1_assign.txt";
        CodeGeneratorVisitor cgv = runCodeGeneratorPipeline(file);
        
        assertEquals("", cgv.getLog());
        
        String expectedCode = "1 6 / sa la p P";
        assertEquals(expectedCode, cgv.getCodiceDc());
    }

    @Test
    public void testDivisioni() throws Exception {
        String file = "src/test/data/testCodeGenerator/2_divsioni.txt";
        CodeGeneratorVisitor cgv = runCodeGeneratorPipeline(file);
        
        assertEquals("", cgv.getLog());
        
        String expectedCode = "0 sa la 1 + sa 6 sb 1.0 6 5 k / 0 k la lb / + sc la p P lb p P lc p P";
        assertEquals(expectedCode, cgv.getCodiceDc());
    }

    @Test
    public void testGenerale() throws Exception {
        String file = "src/test/data/testCodeGenerator/3_generale.txt";
        CodeGeneratorVisitor cgv = runCodeGeneratorPipeline(file);
        
        assertEquals("", cgv.getLog());
        
        String expectedCode = "5 3 + sa la 0.5 + sb la p P lb 4 5 k / 0 k sb lb p P lb 1 - sc lc lb * sc lc p P";
        assertEquals(expectedCode, cgv.getCodiceDc());
    }

    @Test
    public void testRegistriFiniti() throws Exception {
        String file = "src/test/data/testCodeGenerator/4_registriFiniti.txt";
        CodeGeneratorVisitor cgv = runCodeGeneratorPipeline(file);

        assertNotEquals("", cgv.getLog(), "Mi aspetto un errore per esaurimento dei registri.");
        assertTrue(cgv.getLog().contains("Errore") || cgv.getLog().toLowerCase().contains("registri"));
        
        String expectedCode = "6 2 / sa la p P";
        assertEquals(expectedCode, cgv.getCodiceDc());
    }
}