package expression.Evaluators;

import expression.exceptions.OverflowException;

import static expression.exceptions.CheckedAdd.checkAddOverflow;
import static expression.exceptions.CheckedDivide.checkDivideOverflow;
import static expression.exceptions.CheckedMultiply.checkMultiplyOverflow;
import static expression.exceptions.CheckedNegate.checkNegateOverflow;
import static expression.exceptions.CheckedSubtract.checkSubtractOverflow;


public class IntEvaluator implements CommonEvaluator<Integer> {
    private boolean checkOverflow;

    public IntEvaluator(boolean checkOverflow) {
        this.checkOverflow = checkOverflow;
    }

    @Override
    public Integer subtract(Integer first, Integer second) {
        if (checkOverflow) return checkSubtractOverflow(first, second);
        return first - second;
    }

    @Override
    public Integer add(Integer first, Integer second) {
        if (checkOverflow) return checkAddOverflow(first, second);
        return first + second;
    }

    @Override
    public Integer multiply(Integer first, Integer second) {
        if (checkOverflow) return checkMultiplyOverflow(first, second);
        return first * second;
    }

    @Override
    public Integer divide(Integer first, Integer second) {
        if (checkOverflow) return checkDivideOverflow(first, second);
        return first / second;
    }

    @Override
    public Integer variable(Integer in) {
        return in;
    }


    @Override
    public Integer negate(Integer in) {
        if (checkOverflow) return checkNegateOverflow(in);
        return -in;
    }

    @Override
    public Integer intCaster(int in) {
        return in;
    }


    @Override
    public Integer caster(String in) {
        return Integer.parseInt(in);
    }

}