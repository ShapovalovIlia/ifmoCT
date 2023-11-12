package expression.generic;

import expression.Evaluators.CommonEvaluator;

public class GenericNegate<T> extends GenericUnoOperations<T>{
    protected GenericNegate(GenericCommonExpressions<T> in, CommonEvaluator<T> evaluator) {
        super(in, evaluator);
    }

    @Override
    protected T result(T in) {
        return evaluator.negate(in);
    }
}
