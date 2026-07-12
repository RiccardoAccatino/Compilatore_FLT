package visitor;

public class Registri {
    private static char currentRegister = 'a';

    public static char newRegister() {
        return currentRegister++;
    }

    public static void reset() {
        currentRegister = 'a';
    }
}