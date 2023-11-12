package expression;

public class Add extends AbstractOperation {

    public Add(RandomExpression first, RandomExpression second) {
        super(first, second, "+");
    }

    @Override
    protected int calculate(int left, int right) { return left + right; }
}
