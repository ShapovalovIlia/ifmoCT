package expression.generic;

import static expression.generic.GenericTabulator.MODES;

public class Main {

    public static Object[][][] evaluate(String mode, String expression) throws Exception {
        Tabulator tabulator = new GenericTabulator();
        return tabulator.tabulate(mode, expression, -2, 2, -2, 2, -2, 2);
    }

    public static void dump(final Object[][][] res) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    System.out.println("x: " + (i - 2) + " y: " + (j - 2) + " z: " + (k - 2) + " = " + res[i][j][k]);
                }
            }
        }
    }

    public static void main(String[] args) {

        if (!MODES.containsKey(args[0])) {
            throw new IllegalArgumentException("Unknown mode: " + args[0]);
        }
        try {
            dump(evaluate(args[0], args[1]));
        } catch (Exception e) {
            System.out.println((e.getMessage()));
        }
    }
}