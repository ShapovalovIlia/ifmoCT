package expression.exceptions;

import expression.AbstractBinaryOperations;
import expression.CommonExpressions;

public class CheckedSubtract extends AbstractBinaryOperations {
    protected CheckedSubtract(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "-";
    }

    @Override
    protected int result(int first, int second) {
        return checkSubtractOverflow(first, second);
    }
    public static int checkSubtractOverflow(int first, int second) {
        int result = first - second;
        if (second > 0 && first < Integer.MIN_VALUE + second) {
            throw new OverflowException("overflow");
        } else if (second < 0 && first > Integer.MAX_VALUE + second) {
            throw new OverflowException("overflow");
        }
        return result;
    }
}
