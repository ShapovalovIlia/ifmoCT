package expression;

public class Divide extends AbstractBinaryOperations{
    public Divide(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "/";
    }

    @Override
    public int result(int first, int second) {
        return first / second;
    }
}
