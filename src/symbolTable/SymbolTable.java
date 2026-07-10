package symbolTable;

import ast.LangType;
import java.util.HashMap;

public class SymbolTable {

    public static class Attributes {
        private LangType tipo;

        public Attributes(LangType tipo) {
            this.tipo = tipo;
        }

        public LangType getTipo() {
            return tipo;
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
        return table.toString();
    }

    public static int size() {
        return table.size();
    }
}