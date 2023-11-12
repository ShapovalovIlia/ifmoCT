package expression.generic;

import expression.Evaluators.CommonEvaluator;

public class GenericMultiply<T> extends GenericBinaryOperations<T> {
    protected GenericMultiply(GenericCommonExpressions<T> first, GenericCommonExpressions<T> second, CommonEvaluator<T> evaluator) {
        super(first, second, evaluator);
    }

    @Override
    protected T result(T first, T second) {
        return evaluator.multiply(first, second);
    }
}
