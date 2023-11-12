package expression;

public class Multiply extends AbstractBinaryOperations {
    public Multiply(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "*";
    }

    @Override
    public int result(int first, int second) {
        return first * second;
    }
}
