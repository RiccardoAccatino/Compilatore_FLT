package visitor;

import ast.*;
import symbolTable.SymbolTable;
import symbolTable.SymbolTable.Attributes;

public class TypeCheckingVisitor implements IVisitor {
    
    private TypeDescriptor resType; 

    public TypeDescriptor getResType() {
        return resType;
    }

    private void setError(String msg) {
        this.resType = new TypeDescriptor(TipoTD.ERROR, msg);
    }

    @Override
    public void visit(NodeProgram node) {
        resType = new TypeDescriptor(TipoTD.OK);
        // Si itera su tutti i figli del programma [cite: 180, 181]
        for (NodeAST decSt : node.getDecSts()) {
            decSt.accept(this);
            if (resType.getTipo() == TipoTD.ERROR) {
                return; // Propaga l'errore e interrompi [cite: 167, 175]
            }
        }
    }

    @Override
    public void visit(NodeDecl node) {
        String id = node.getId().getName();
        
        if (SymbolTable.lookup(id) != null) {
            setError("Errore: Variabile '" + id + "' gia' definita."); // [cite: 166, 167]
            return;
        }

        TypeDescriptor tipoDichiarato = new TypeDescriptor(
            node.getType() == LangType.INT ? TipoTD.INT : TipoTD.FLOAT
        );

        if (node.getInit() != null) {
            node.getInit().accept(this);
            TypeDescriptor tipoInit = resType;

            if (tipoInit.getTipo() == TipoTD.ERROR) {
                return; // Propaga l'errore
            }

            if (!tipoDichiarato.compatibile(tipoInit)) {
                setError("Errore: Inizializzazione incompatibile per '" + id + "'."); // [cite: 167]
                return;
            }
        }

        // Inserimento degli attributi nella Symbol Table [cite: 165]
        SymbolTable.enter(id, new Attributes(node.getType()));
        resType = new TypeDescriptor(TipoTD.OK);
    }

    @Override
    public void visit(NodeAssign node) {
        String id = node.getId().getName();
        Attributes attr = SymbolTable.lookup(id);

        if (attr == null) {
            setError("Errore: Variabile '" + id + "' non dichiarata."); // [cite: 169, 170]
            return;
        }

        TypeDescriptor tipoVar = new TypeDescriptor(
            attr.getTipo() == LangType.INT ? TipoTD.INT : TipoTD.FLOAT
        );

        node.getExpr().accept(this);
        TypeDescriptor tipoExpr = resType;

        if (tipoExpr.getTipo() == TipoTD.ERROR) {
            return;
        }

        if (!tipoVar.compatibile(tipoExpr)) {
            setError("Errore: Assegnamento di tipo incompatibile per '" + id + "'."); // [cite: 169, 170]
            return;
        }

        resType = new TypeDescriptor(TipoTD.OK);
    }

    @Override
    public void visit(NodePrint node) {
        String id = node.getId().getName();
        
        if (SymbolTable.lookup(id) == null) {
            setError("Errore: Variabile '" + id + "' non dichiarata nella stampa."); // [cite: 168, 170]
            return;
        }
        resType = new TypeDescriptor(TipoTD.OK);
    }

    @Override
    public void visit(NodeBinOp node) {
        node.getLeft().accept(this);
        TypeDescriptor leftTD = resType;

        node.getRight().accept(this);
        TypeDescriptor rightTD = resType;

        if (leftTD.getTipo() == TipoTD.ERROR || rightTD.getTipo() == TipoTD.ERROR) {
            setError("Errore negli operandi dell'espressione.");
            return;
        }

        if (leftTD.getTipo() == TipoTD.INT && rightTD.getTipo() == TipoTD.INT) {
            resType = new TypeDescriptor(TipoTD.INT); // [cite: 175]
        } else {
            resType = new TypeDescriptor(TipoTD.FLOAT); // [cite: 175]
            if (node.getOp() == LangOper.DIVIDE) {
                node.setOp(LangOper.DIV_FLOAT); // Modifica l'operatore se e' divisione mista [cite: 175]
            }
        }
    }

    @Override
    public void visit(NodeDeref node) {
        // Il tipo di un NodeDeref e' uguale a quello del NodeId contenuto [cite: 174]
        node.getId().accept(this); 
    }

    @Override
    public void visit(NodeCost node) {
        resType = new TypeDescriptor(
            node.getType() == LangType.INT ? TipoTD.INT : TipoTD.FLOAT // [cite: 174]
        );
    }

    @Override
    public void visit(NodeId node) {
        String id = node.getName();
        Attributes attr = SymbolTable.lookup(id);
        if (attr == null) {
            setError("Errore: Variabile '" + id + "' non dichiarata.");
            return;
        }
        resType = new TypeDescriptor(
            attr.getTipo() == LangType.INT ? TipoTD.INT : TipoTD.FLOAT
        );
    }
}