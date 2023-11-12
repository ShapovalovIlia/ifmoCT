package expression.generic;

import expression.Evaluators.CommonEvaluator;

public class GenericAdd<T> extends GenericBinaryOperations<T> {

    protected GenericAdd(GenericCommonExpressions<T> first, GenericCommonExpressions<T> second, CommonEvaluator<T> evaluator) {
        super(first, second, evaluator);
    }

    @Override
    protected T result(T first, T second) {
        return evaluator.add(first, second);
    }
}
