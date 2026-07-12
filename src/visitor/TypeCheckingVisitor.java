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
        for (NodeAST decSt : node.getDecSts()) {
            decSt.accept(this);
            if (resType.getTipo() == TipoTD.ERROR) {
                return;
            }
        }
    }

    @Override
    public void visit(NodeDecl node) {
        String id = node.getId().getName();
        
        if (SymbolTable.lookup(id) != null) {
            setError("Errore: Variabile '" + id + "' gia' definita.");
            return;
        }

        TypeDescriptor tipoDichiarato = new TypeDescriptor(
            node.getType() == LangType.INT ? TipoTD.INT : TipoTD.FLOAT
        );

        if (node.getInit() != null) {
            node.getInit().accept(this);
            TypeDescriptor tipoInit = resType;

            if (tipoInit.getTipo() == TipoTD.ERROR) {
                return;
            }

            if (!tipoDichiarato.compatibile(tipoInit)) {
                setError("Errore: Inizializzazione incompatibile per '" + id + "'.");
                return;
            }
        }

        SymbolTable.enter(id, new Attributes(node.getType()));
        resType = new TypeDescriptor(TipoTD.OK);
    }

    @Override
    public void visit(NodeAssign node) {
        String id = node.getId().getName();
        Attributes attr = SymbolTable.lookup(id);

        if (attr == null) {
            setError("Errore: Variabile '" + id + "' non dichiarata.");
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
            setError("Errore: Assegnamento di tipo incompatibile per '" + id + "'.");
            return;
        }

        resType = new TypeDescriptor(TipoTD.OK);
    }

    @Override
    public void visit(NodePrint node) {
        String id = node.getId().getName();
        
        if (SymbolTable.lookup(id) == null) {
            setError("Errore: Variabile '" + id + "' non dichiarata nella stampa.");
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
            resType = new TypeDescriptor(TipoTD.INT);
        } else {
            resType = new TypeDescriptor(TipoTD.FLOAT);
            if (node.getOp() == LangOper.DIVIDE) {
                node.setOp(LangOper.DIV_FLOAT);
            }
        }
    }

    @Override
    public void visit(NodeDeref node) {
        node.getId().accept(this); 
    }

    @Override
    public void visit(NodeCost node) {
        resType = new TypeDescriptor(
            node.getType() == LangType.INT ? TipoTD.INT : TipoTD.FLOAT
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