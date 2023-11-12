package expression.generic;

public interface GenericCommonExpressions<T> {
    T evaluate(T x);

    T evaluate(T x, T y, T z);
}
