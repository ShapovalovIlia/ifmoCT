package expression.exceptions;

import expression.AbstractBinaryOperations;
import expression.CommonExpressions;

public class CheckedDivide extends AbstractBinaryOperations {
    protected CheckedDivide(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "/";
    }

    @Override
    protected int result(int first, int second) {
        return checkDivideOverflow(first, second);
    }
    public static int checkDivideOverflow(int first, int second) {
        if ((second == 0) || (first == Integer.MIN_VALUE && second == -1)) {
            throw new OverflowException("Dividing by zero");
        }
        return first / second;
    }
}
