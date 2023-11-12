package expression;

public class Set extends AbstractBinaryOperations {

    public Set(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "set";
    }

    @Override
    public int result(int first, int second) {
        return first | (1 << second);
    }
}
