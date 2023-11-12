package expression;

public class Subtract extends AbstractOperation {

    public Subtract(RandomExpression first, RandomExpression second) {
        super(first, second, "-");
    }

    @Override
    protected int calculate(int left, int right) { return left - right; }
}
