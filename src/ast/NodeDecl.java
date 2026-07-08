package ast;

public class NodeDecl extends NodeStm {
    private NodeId id;
    private LangType type;
    private NodeExpr init; // Può essere null se non inizializzata

    public NodeDecl(NodeId id, LangType type, NodeExpr init) {
        this.id = id;
        this.type = type;
        this.init = init;
    }

    public NodeId getId() {
        return id;
    }

    public LangType getType() {
        return type;
    }

    public NodeExpr getInit() {
        return init;
    }

    @Override
    public String toString() {
        return "NodeDecl(id=" + id + ", type=" + type + ", init=" + (init != null ? init : "null") + ")";
    }
}