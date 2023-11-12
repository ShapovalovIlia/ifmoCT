package expression.exceptions;


import expression.CommonExpressions;

public class CheckedNegate extends AbstractCheckUnoOperations {

    protected CheckedNegate(CommonExpressions in) {
        super(in);
    }


    @Override
    protected String getSymbol() {
        return "-";
    }

    @Override
    protected int result(int in) {
        return checkNegateOverflow(in);
    }
    public static int checkNegateOverflow(int in) {
        if (in == Integer.MIN_VALUE) {
            throw new OverflowException("overflow");
        }
        return -in;
    }
}