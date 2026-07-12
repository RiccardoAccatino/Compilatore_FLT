package visitor;

import ast.*;
import symbolTable.SymbolTable;
import symbolTable.SymbolTable.Attributes;

public class CodeGeneratorVisitor implements IVisitor {
    private String codiceDc = ""; 
    private String log = ""; 

    public String getCodiceDc() {
        return codiceDc;
    }

    public String getLog() {
        return log;
    }

    @Override
    public void visit(NodeProgram node) {
        if (!log.isEmpty()) return; // Continua SOLO se il log è vuoto

        StringBuilder sb = new StringBuilder();
        
        for (NodeAST child : node.getDecSts()) {
            child.accept(this);
            
            if (!log.isEmpty()) {
                // ERRORE RILEVATO: prima di interrompere, salviamo 
                // tutto il codice generato con successo finora.
                codiceDc = sb.toString().trim();
                return; 
            }
            
            if (!codiceDc.isEmpty()) {
                sb.append(codiceDc).append(" ");
            }
        }
        
        // Se tutto è andato a buon fine, salva il codice finale
        codiceDc = sb.toString().trim();
    }

    @Override
    public void visit(NodeDecl node) {
        if (!log.isEmpty()) return;

        char reg = Registri.newRegister();
        
        if (reg > 'z') { 
            log = "Errore: registri esauriti.";
            return;
        }
        Attributes attr = SymbolTable.lookup(node.getId().getName());
        if (attr != null) {
            attr.setRegistro(reg);
        }
        if (node.getInit() != null) {
            node.getInit().accept(this);
            if (!log.isEmpty()) return;
            
            String exprCode = codiceDc;
            codiceDc = exprCode + " s" + reg;
        } else {
            codiceDc = "";
        }
    }

    @Override
    public void visit(NodeAssign node) {
        if (!log.isEmpty()) return;
        node.getExpr().accept(this);
        if (!log.isEmpty()) return;
        String exprCode = codiceDc;
        Attributes attr = SymbolTable.lookup(node.getId().getName());
        char reg = attr.getRegistro();

        codiceDc = exprCode + " s" + reg;
    }

    @Override
    public void visit(NodePrint node) {
        if (!log.isEmpty()) return;
        Attributes attr = SymbolTable.lookup(node.getId().getName());
        char reg = attr.getRegistro();
        codiceDc = "l" + reg + " p P";
    }

    @Override
    public void visit(NodeBinOp node) {
        if (!log.isEmpty()) return;
        node.getLeft().accept(this);
        String leftCodice = codiceDc; 

        
        node.getRight().accept(this);
        String rightCodice = codiceDc;

    
        if (!log.isEmpty()) return; 


        String opCode = "";
        switch (node.getOp()) {
            case PLUS: opCode = "+"; break;
            case MINUS: opCode = "-"; break;
            case TIMES: opCode = "*"; break;
            case DIVIDE: opCode = "/"; break;
            case DIV_FLOAT: opCode = "5 k / 0 k"; break; 
        }

        codiceDc = leftCodice + " " + rightCodice + " " + opCode;
    }

    @Override
    public void visit(NodeDeref node) {
        if (!log.isEmpty()) return;

        Attributes attr = SymbolTable.lookup(node.getId().getName());
        char reg = attr.getRegistro();

        codiceDc = "l" + reg;
    }

    @Override
    public void visit(NodeCost node) {
        if (!log.isEmpty()) return;

        
        codiceDc = node.getValue(); 
    }

    @Override
    public void visit(NodeId node) {
        if (!log.isEmpty()) return;
        codiceDc = ""; 
    }
}