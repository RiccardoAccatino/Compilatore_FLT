package ast;
import visitor.IVisitor;
import java.util.ArrayList;

public class NodeProgram extends NodeAST {
    private ArrayList<NodeAST> decSts;

    public NodeProgram(ArrayList<NodeAST> decSts) {
        this.decSts = decSts;
    }

    public ArrayList<NodeAST> getDecSts() {
        return decSts;
    }

    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NodeProgram:\n");
        for (NodeAST node : decSts) {
            sb.append("  ").append(node.toString()).append("\n");
        }
        return sb.toString();
    }
}