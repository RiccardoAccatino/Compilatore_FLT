package parser;

import java.util.ArrayList;

import ast.*;
import scanner.LexicalException;
import scanner.Scanner;
import token.Token;
import token.TokenType;

public class Parser {
    private Scanner scanner;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public NodeProgram parse() throws SyntacticException {
        return this.parsePrg();
    }

    private Token match(TokenType type) throws SyntacticException {
        Token tk = peekTokenWrapper();
        if (type.equals(tk.getType())) {
            return nextTokenWrapper();
        } else {
            throw new SyntacticException(
                "Aspettato token " + type + ", trovato token " + tk.getType() + " alla riga " + tk.getRiga()
            );
        }
    }

    private NodeProgram parsePrg() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TYFLOAT:
            case TYINT:
            case ID:
            case PRINT:
            case EOF:
                // 0. Prg -> DSs $
                ArrayList<NodeAST> decSts = parseDSs();
                match(TokenType.EOF);
                return new NodeProgram(decSts);
            default:
                throw new SyntacticException(
                    "Token " + tk.getType() + " alla riga " + tk.getRiga() + " non e' inizio di programma"
                );
        }
    }

    private ArrayList<NodeAST> parseDSs() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TYFLOAT:
            case TYINT:
                // 1. DSs -> Dcl DSs
                NodeDecl decl = parseDcl();
                ArrayList<NodeAST> listDcl = parseDSs();
                listDcl.add(0, decl); // Inserisco in testa per mantenere l'ordine delle istruzioni
                return listDcl;
            case ID:
            case PRINT:
                // 2. DSs -> Stm DSs
                NodeStm stm = parseStm();
                ArrayList<NodeAST> listStm = parseDSs();
                listStm.add(0, stm);
                return listStm;
            case EOF:
                // 3. DSs -> eps
                return new ArrayList<>(); // Ritorno lista vuota alla fine delle dichiarazioni/istruzioni
            default:
                throw new SyntacticException(
                    "Errore Sintattico: token inatteso " + tk.getType() + " alla riga " + tk.getRiga()
                );
        }
    }

    private NodeDecl parseDcl() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TYFLOAT:
            case TYINT:
                // 4. Dcl -> Ty id DclP
                LangType type = parseTy();
                Token idTk = match(TokenType.ID);
                NodeId id = new NodeId(idTk.getVal());
                NodeExpr init = parseDclP();
                return new NodeDecl(id, type, init);
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private LangType parseTy() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TYFLOAT:
                // 17. Ty -> float
                match(TokenType.TYFLOAT);
                return LangType.FLOAT;
            case TYINT:
                // 18. Ty -> int
                match(TokenType.TYINT);
                return LangType.INT;
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private NodeExpr parseDclP() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case SEMI:
                // 5. DclP -> ;
                match(TokenType.SEMI);
                return null; // Nessuna espressione di inizializzazione
            case ASSIGN:
                // 6. DclP -> = Exp;
                match(TokenType.ASSIGN);
                NodeExpr exp = parseExp();
                match(TokenType.SEMI);
                return exp;
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga() + ": atteso ';' o '='");
        }
    }

    private NodeStm parseStm() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case ID:
                // 7. Stm -> id Op Exp;
                Token idTk = match(TokenType.ID);
                NodeId id = new NodeId(idTk.getVal());
                Token opTk = parseOp(); 
                NodeExpr exp = parseExp();
                match(TokenType.SEMI);
                
                // Se è un operatore opAss (+=, -=, *=, /=), creiamo un'espressione binaria
                // In questo modo nell'albero traduciamo 'a += 5' in 'a = a + 5'
                if (opTk.getType() == TokenType.OP_ASSIGN) {
                    String opVal = opTk.getVal();
                    LangOper oper = null;
                    if (opVal.equals("+=")) oper = LangOper.PLUS;
                    else if (opVal.equals("-=")) oper = LangOper.MINUS;
                    else if (opVal.equals("*=")) oper = LangOper.TIMES;
                    else if (opVal.equals("/=")) oper = LangOper.DIVIDE;
                    
                    NodeBinOp binOp = new NodeBinOp(oper, new NodeDeref(id), exp);
                    return new NodeAssign(id, binOp);
                } else {
                    // Assegnamento standard: '='
                    return new NodeAssign(id, exp);
                }
            case PRINT:
                // 8. Stm -> print id;
                match(TokenType.PRINT);
                Token printIdTk = match(TokenType.ID);
                match(TokenType.SEMI);
                return new NodePrint(new NodeId(printIdTk.getVal()));
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private NodeExpr parseExp() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case ID:
            case FLOAT:
            case INT:
                // 9. Exp -> Tr ExpP
                NodeExpr left = parseTr();
                return parseExpP(left);
            default:
                throw new SyntacticException("Errore Sintattico nell'espressione alla riga " + tk.getRiga());
        }
    }

    private NodeExpr parseExpP(NodeExpr left) throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case PLUS:
                // 10. ExpP -> + Tr ExpP
                match(TokenType.PLUS);
                NodeExpr rightPlus = parseTr();
                NodeBinOp binOpPlus = new NodeBinOp(LangOper.PLUS, left, rightPlus);
                return parseExpP(binOpPlus);
            case MINUS:
                // 11. ExpP -> - Tr ExpP
                match(TokenType.MINUS);
                NodeExpr rightMinus = parseTr();
                NodeBinOp binOpMinus = new NodeBinOp(LangOper.MINUS, left, rightMinus);
                return parseExpP(binOpMinus);
            case SEMI:
                // 12. ExpP -> eps
                return left; 
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private NodeExpr parseTr() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case ID:
            case FLOAT:
            case INT:
                // 13. Tr -> Val TrP
                NodeExpr left = parseVal();
                return parseTrP(left);
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private NodeExpr parseTrP(NodeExpr left) throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TIMES:
                // 14. TrP -> * Val TrP
                match(TokenType.TIMES);
                NodeExpr rightTimes = parseVal();
                NodeBinOp binOpTimes = new NodeBinOp(LangOper.TIMES, left, rightTimes);
                return parseTrP(binOpTimes);
            case DIVIDE:
                // 15. TrP -> / Val TrP
                match(TokenType.DIVIDE);
                NodeExpr rightDivide = parseVal();
                NodeBinOp binOpDivide = new NodeBinOp(LangOper.DIVIDE, left, rightDivide);
                return parseTrP(binOpDivide);
            case MINUS:
            case PLUS:
            case SEMI:
                // 16. TrP -> eps
                return left; 
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private NodeExpr parseVal() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case INT:
                // 19. Val -> intVal
                Token intTk = match(TokenType.INT);
                return new NodeCost(intTk.getVal(), LangType.INT);
            case FLOAT:
                // 20. Val -> floatVal
                Token floatTk = match(TokenType.FLOAT);
                return new NodeCost(floatTk.getVal(), LangType.FLOAT);
            case ID:
                // 21. Val -> id
                Token idTk = match(TokenType.ID);
                return new NodeDeref(new NodeId(idTk.getVal()));
            default:
                throw new SyntacticException("Valore non valido alla riga " + tk.getRiga());
        }
    }

    private Token parseOp() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case ASSIGN:
                // 22. Op -> =
                return match(TokenType.ASSIGN);
            case OP_ASSIGN:
                // 23. Op -> opAss
                return match(TokenType.OP_ASSIGN);
            default:
                throw new SyntacticException("Operatore non valido alla riga " + tk.getRiga());
        }
    }

    // --- METODI DI SUPPORTO PER LA GESTIONE DELLE ECCEZIONI ---
    
    private Token peekTokenWrapper() throws SyntacticException {
        try {
            return scanner.peekToken();
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale intercettato durante il parsing", e);
        }
    }

    private Token nextTokenWrapper() throws SyntacticException {
        try {
            return scanner.nextToken();
        } catch (LexicalException e) {
            throw new SyntacticException("Errore lessicale intercettato durante il parsing", e);
        }
    }
}