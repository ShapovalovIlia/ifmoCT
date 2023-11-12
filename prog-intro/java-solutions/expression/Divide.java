package expression;

public class Divide extends AbstractOperation {

    public Divide(RandomExpression first, RandomExpression second) {
        super(first, second, "/");
    }

    @Override
    protected int calculate(int left, int right) { return left / right; }
}
