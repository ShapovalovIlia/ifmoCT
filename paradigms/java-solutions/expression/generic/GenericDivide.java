package expression.generic;

import expression.Evaluators.CommonEvaluator;

public class GenericDivide<T> extends GenericBinaryOperations<T>{
    protected GenericDivide(GenericCommonExpressions<T> first, GenericCommonExpressions<T> second, CommonEvaluator<T> evaluator) {
        super(first, second, evaluator);
    }

    @Override
    protected T result(T first, T second) {
        return evaluator.divide(first, second);
    }
}
