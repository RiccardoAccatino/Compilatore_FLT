package symbolTable;

import ast.LangType;
import java.util.HashMap;

public class SymbolTable {

    public static class Attributes {
        private LangType tipo;
        private char registro;

        public Attributes(LangType tipo) {
            this.tipo = tipo;
        }

        public LangType getTipo() {
            return tipo;
        }

        public void setTipo(LangType tipo) {
            this.tipo = tipo;
        }
        public char getRegistro() {
            return registro;
        }

        public void setRegistro(char registro) {
            this.registro = registro;
        }
    }

    private static HashMap<String, Attributes> table;

    public static void init() {
        table = new HashMap<>();
    }

    public static boolean enter(String id, Attributes entry) {
        if (table.containsKey(id)) {
            return false;
        }
        table.put(id, entry);
        return true;
    }

    public static Attributes lookup(String id) {
        return table.get(id);
    }

    public static String toStr() {
        StringBuilder sb = new StringBuilder();
        for (String id : table.keySet()) {
            Attributes attr = table.get(id);
            sb.append("ID: ").append(id)
              .append(" -> Tipo: ").append(attr.getTipo())
              .append(", Registro: ").append(attr.getRegistro()).append("\n");
        }
        return sb.toString();
    }

    public static int size() {
        if (table == null) return 0;
        return table.size();
    }
}