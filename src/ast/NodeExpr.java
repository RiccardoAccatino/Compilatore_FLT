package ast;

public abstract class NodeExpr extends NodeAST {
}
@Override
public void accept(IVisitor visitor) {
    visitor.visit(this);
}