package expression.Evaluators;

public interface CommonEvaluator<T> {
    T subtract(T first, T second);

    T add(T first, T second);

    T multiply(T first, T second);

    T divide(T first, T second);

    T variable(T in);

    T negate(T in);

    T intCaster(int in);
    T caster(String in);

}
