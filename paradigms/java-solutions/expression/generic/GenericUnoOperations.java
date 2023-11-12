package expression.generic;

import expression.Evaluators.CommonEvaluator;


public abstract class GenericUnoOperations<T> implements GenericCommonExpressions<T> {
    private final GenericCommonExpressions<T> in;
    protected final CommonEvaluator<T> evaluator;

    protected abstract T result(T in);

    protected GenericUnoOperations(GenericCommonExpressions<T> in, CommonEvaluator<T> evaluator) {
        this.in = in;
        this.evaluator = evaluator;
    }


    @Override
    public T evaluate(T x) {
        return result(in.evaluate(x));
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return result(in.evaluate(x, y, z));
    }


}
