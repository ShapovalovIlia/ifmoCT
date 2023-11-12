package expression.generic;

import expression.Evaluators.CommonEvaluator;

public class GenericSubtract<T> extends GenericBinaryOperations<T>{

    protected GenericSubtract(GenericCommonExpressions<T> first, GenericCommonExpressions<T> second, CommonEvaluator<T> evaluator) {
        super(first, second, evaluator);
    }

    @Override
    protected T result(T first, T second) {
        return evaluator.subtract(first, second);
    }
}
