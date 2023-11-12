package expression;

import java.util.Objects;

public abstract class AbstractOperation implements RandomExpression{
    protected final RandomExpression first;
    protected final RandomExpression second;
    private final String sign;

    protected abstract int calculate(int left,  int  right);

    @Override
    public int evaluate(int x) {
        return calculate(first.evaluate(x), second.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return calculate(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }

    public AbstractOperation(RandomExpression first, RandomExpression second, String sign) {
        this.first = first;
        this.second = second;
        this.sign = sign;
    }

    @Override
    public String toString() {
        return '(' + first.toString() + ' ' + sign + ' '  + second.toString() + ')';
    }

    @Override
    public int hashCode() {
        int res = first.hashCode();
        res = 31 * res + second.hashCode();
        return 31 * res + sign.hashCode();
    }

    //:note: wrong equals
    @Override
    public boolean equals (Object o) {
        return o != null && this.getClass() == o.getClass() &&
                Objects.equals(first, ((AbstractOperation) o).first) &&
                Objects.equals(second, ((AbstractOperation) o).second);
    }
}

