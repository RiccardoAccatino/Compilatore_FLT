package parser;

import scanner.Scanner;
import scanner.LexicalException;
import token.Token;
import token.TokenType;

public class Parser {
    private Scanner scanner;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public void parse() throws SyntacticException {
        this.parsePrg();
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

    private void parsePrg() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TYFLOAT:
            case TYINT:
            case ID:
            case PRINT:
            case EOF:
                // 0. Prg -> DSs $
                parseDSs();
                match(TokenType.EOF);
                return;
            default:
                throw new SyntacticException(
                    "Token " + tk.getType() + " alla riga " + tk.getRiga() + " non e' inizio di programma"
                );
        }
    }

    private void parseDSs() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TYFLOAT:
            case TYINT:
                // 1. DSs -> Dcl DSs
                parseDcl();
                parseDSs();
                return;
            case ID:
            case PRINT:
                // 2. DSs -> Stm DSs
                parseStm();
                parseDSs();
                return;
            case EOF:
                // 3. DSs -> eps
                return;
            default:
                throw new SyntacticException(
                    "Errore Sintattico: token inatteso " + tk.getType() + " alla riga " + tk.getRiga()
                );
        }
    }

    private void parseDcl() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TYFLOAT:
            case TYINT:
                // 4. Dcl -> Ty id DclP
                parseTy();
                match(TokenType.ID);
                parseDclP();
                return;
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private void parseTy() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TYFLOAT:
                // 17. Ty -> float
                match(TokenType.TYFLOAT);
                return;
            case TYINT:
                // 18. Ty -> int
                match(TokenType.TYINT);
                return;
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private void parseDclP() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case SEMI:
                // 5. DclP -> ;
                match(TokenType.SEMI);
                return;
            case ASSIGN:
                // 6. DclP -> = Exp;
                match(TokenType.ASSIGN);
                parseExp();
                match(TokenType.SEMI);
                return;
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga() + ": atteso ';' o '='");
        }
    }

    private void parseStm() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case ID:
                // 7. Stm -> id Op Exp;
                match(TokenType.ID);
                parseOp();
                parseExp();
                match(TokenType.SEMI);
                return;
            case PRINT:
                // 8. Stm -> print id;
                match(TokenType.PRINT);
                match(TokenType.ID);
                match(TokenType.SEMI);
                return;
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private void parseExp() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case ID:
            case FLOAT:
            case INT:
                // 9. Exp -> Tr ExpP
                parseTr();
                parseExpP();
                return;
            default:
                throw new SyntacticException("Errore Sintattico nell'espressione alla riga " + tk.getRiga());
        }
    }

    private void parseExpP() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case PLUS:
                // 10. ExpP -> + Tr ExpP
                match(TokenType.PLUS);
                parseTr();
                parseExpP();
                return;
            case MINUS:
                // 11. ExpP -> - Tr ExpP
                match(TokenType.MINUS);
                parseTr();
                parseExpP();
                return;
            case SEMI:
                // 12. ExpP -> eps
                return;
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private void parseTr() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case ID:
            case FLOAT:
            case INT:
                // 13. Tr -> Val TrP
                parseVal();
                parseTrP();
                return;
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private void parseTrP() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case TIMES:
                // 14. TrP -> * Val TrP
                match(TokenType.TIMES);
                parseVal();
                parseTrP();
                return;
            case DIVIDE:
                // 15. TrP -> / Val TrP
                match(TokenType.DIVIDE);
                parseVal();
                parseTrP();
                return;
            case MINUS:
            case PLUS:
            case SEMI:
                // 16. TrP -> eps
                return;
            default:
                throw new SyntacticException("Errore Sintattico alla riga " + tk.getRiga());
        }
    }

    private void parseVal() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case INT:
                // 19. Val -> intVal
                match(TokenType.INT);
                return;
            case FLOAT:
                // 20. Val -> floatVal
                match(TokenType.FLOAT);
                return;
            case ID:
                // 21. Val -> id
                match(TokenType.ID);
                return;
            default:
                throw new SyntacticException("Valore non valido alla riga " + tk.getRiga());
        }
    }

    private void parseOp() throws SyntacticException {
        Token tk = peekTokenWrapper();
        switch (tk.getType()) {
            case ASSIGN:
                // 22. Op -> =
                match(TokenType.ASSIGN);
                return;
            case OP_ASSIGN:
                // 23. Op -> opAss
                match(TokenType.OP_ASSIGN);
                return;
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