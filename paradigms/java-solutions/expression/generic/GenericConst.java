package expression.generic;

public class GenericConst<T> implements GenericCommonExpressions<T> {
    private final T value;

    public GenericConst(T value) {
        this.value = value;
    }

    @Override
    public T evaluate(T in) {
        return value;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return value;
    }
}
