package expression.exceptions;

import expression.AbstractBinaryOperations;
import expression.CommonExpressions;

public class CheckedMultiply extends AbstractBinaryOperations{
    protected CheckedMultiply(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "*";
    }

    @Override
    protected int result(int first, int second) {
        return checkMultiplyOverflow(first, second);
    }
    public static int checkMultiplyOverflow(int x, int y) {
        int maximum = Integer.signum(x) == Integer.signum(y) ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        if ((x == -1 && y == Integer.MIN_VALUE)
                || (x != -1 && x != 0 && ((y > 0 && y > maximum / x)
                || (y < 0 && y < maximum / x )))) {
            throw new OverflowException("Multiplication overflow: " + x + "*" + y);
        }
        return x * y;
    }
}
