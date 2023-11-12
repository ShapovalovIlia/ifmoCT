package expression.generic;

import expression.Evaluators.CommonEvaluator;


public abstract class GenericBinaryOperations<T> implements GenericCommonExpressions<T> {
    private final GenericCommonExpressions<T> first;
    private final GenericCommonExpressions<T> second;
    protected final CommonEvaluator<T> evaluator;

    protected abstract T result(T first, T second);

    protected GenericBinaryOperations(GenericCommonExpressions<T> first, GenericCommonExpressions<T> second, CommonEvaluator<T> evaluator) {
        this.first = first;
        this.second = second;
        this.evaluator = evaluator;
    }


    @Override
    public T evaluate(T x) {
        return result(first.evaluate(x), second.evaluate(x));
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return result(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }


}
