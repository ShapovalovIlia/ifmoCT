package expression.exceptions;

import expression.CommonExpressions;

import java.util.Objects;

public abstract class AbstractCheckUnoOperations implements CommonExpressions {
    private final CommonExpressions in;

    protected AbstractCheckUnoOperations(CommonExpressions in) {
        this.in = in;
    }

    protected abstract String getSymbol();

    protected abstract int result(int first);


    @Override
    public String toString() {


        return getSymbol() + "(" + in + ")";
    }

    @Override
    public int evaluate(int x) {
        return result(in.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return result(in.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass() &&
                Objects.equals(in, ((AbstractCheckUnoOperations) obj).in);
    }

    @Override
    public int hashCode() {
        return Objects.hash(in, getSymbol());
    }

}
