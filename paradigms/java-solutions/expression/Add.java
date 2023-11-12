package expression;

public class Add extends AbstractBinaryOperations {

    public Add(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "+";
    }

    @Override
    public int result(int first, int second) {
        return first + second;
    }
}
