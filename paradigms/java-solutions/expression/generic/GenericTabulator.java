package expression.generic;

import expression.Evaluators.*;
import expression.exceptions.OverflowException;

import java.util.HashMap;


public class GenericTabulator implements Tabulator {

    public final static HashMap<String, CommonEvaluator<?>> MODES = new HashMap<>();

    static {
        MODES.put("i", new IntEvaluator(true));
        MODES.put("u", new IntEvaluator(false));
        MODES.put("bi", new BigIntegerEvaluator());
        MODES.put("d", new DoubleEvaluator());
        MODES.put("f", new FloatEvaluator());
        MODES.put("s", new ShortEvaluator());
    }




    private <T> Object[][][] calculateResult(CommonEvaluator<T> evaluator, String expression,
                                             int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        GenericTripleParser<T> parser = new GenericParser<>(evaluator);
        GenericCommonExpressions<T> expr = parser.parse(expression);

        Object[][][] result = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    try {
                        result[i - x1][j - y1][k - z1] = expr.evaluate(
                                evaluator.intCaster(i),
                                evaluator.intCaster(j),
                                evaluator.intCaster(k));
                    } catch (OverflowException | ArithmeticException e) {
                        result[i - x1][j - y1][k - z1] = null;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        if (!MODES.containsKey(mode)) {
            throw new IllegalArgumentException("Unknown mode: " + mode);
        }
        CommonEvaluator<?> evaluator = MODES.get(mode);
        return calculateResult(evaluator, expression, x1, x2, y1, y2, z1, z2);
    }
}