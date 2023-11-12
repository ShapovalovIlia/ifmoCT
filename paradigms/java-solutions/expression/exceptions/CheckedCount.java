package expression.exceptions;


import expression.CommonExpressions;

public class CheckedCount extends AbstractCheckUnoOperations{
    protected CheckedCount(CommonExpressions in) {
        super(in);
    }

    @Override
    protected String getSymbol() {
        return "count";
    }

    @Override
    protected int result(int in) {
        return Integer.bitCount(in);
    }
}
