package expression;

import java.util.Objects;

public abstract class AbstractBinaryOperations implements CommonExpressions {
    private final CommonExpressions first, second;

    protected abstract String getSymbol();

    protected abstract int result(int first, int second);

    protected AbstractBinaryOperations(CommonExpressions first, CommonExpressions second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {


        return "(" + first + " " +
                getSymbol() +
                " " + second + ")";
    }

    @Override
    public int evaluate(int x) {
        return result(first.evaluate(x), second.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return result(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass() &&
                Objects.equals(first, ((AbstractBinaryOperations) obj).first) &&
                Objects.equals(second, ((AbstractBinaryOperations) obj).second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, getSymbol());
    }

}
