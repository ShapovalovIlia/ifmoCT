package expression;

public class Multiply extends AbstractOperation {

    public Multiply(RandomExpression first, RandomExpression second) {
        super(first, second, "*");
    }

    @Override
    protected int calculate(int left, int right) { return left * right; }
}
