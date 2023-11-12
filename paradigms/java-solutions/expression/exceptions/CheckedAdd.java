package expression.exceptions;

import expression.AbstractBinaryOperations;
import expression.CommonExpressions;

public class CheckedAdd extends AbstractBinaryOperations {
    protected CheckedAdd(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "+";
    }

    @Override
    protected int result(int first, int second) {
        return checkAddOverflow(first, second);
    }
    public static int checkAddOverflow(int first, int second) {
        int result = first + second;
        if (second > 0 && Integer.MAX_VALUE - second < first || second < 0 && Integer.MIN_VALUE - second > first) {
            throw new OverflowException("overflow");
        }
        return result;
    }
}
