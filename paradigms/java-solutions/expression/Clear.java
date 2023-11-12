package expression;

public class Clear extends AbstractBinaryOperations {

    public Clear(CommonExpressions first, CommonExpressions second) {
        super(first, second);
    }

    @Override
    protected String getSymbol() {
        return "clear";
    }

    @Override
    public int result(int first, int second) {
        return first & ~(1 << second) ;
    }
}
