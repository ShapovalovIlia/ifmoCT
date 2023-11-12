package expression.generic;

import expression.Evaluators.CommonEvaluator;

public class GenericVariable<T> implements GenericCommonExpressions<T> {
    private final String name;
    private final CommonEvaluator<T> evaluator;

    public GenericVariable(String name, CommonEvaluator<T> evaluator) {
        this.name = name;
        this.evaluator = evaluator;
    }

    @Override
    public T evaluate(T in) {
        return evaluator.variable(in);
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return switch(name) {
            case("x") -> evaluator.variable(x);
            case("y") -> evaluator.variable(y);
            case("z") -> evaluator.variable(z);
            default -> null;
        };
    }
}
