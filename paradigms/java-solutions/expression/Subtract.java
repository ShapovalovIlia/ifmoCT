package expression;

public class Subtract extends AbstractBinaryOperations {
    public Subtract(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "-";
    }

    @Override
    public int result(int first, int second) {
        return first - second;
    }
}
