package expression.Evaluators;

public class ShortEvaluator implements CommonEvaluator<Short>{
    @Override
    public Short subtract(Short first, Short second) {
        return (short) (first - second);
    }

    @Override
    public Short add(Short first, Short second) {
        return (short) (first + second);
    }

    @Override
    public Short multiply(Short first, Short second) {
        return (short) (first * second);
    }

    @Override
    public Short divide(Short first, Short second) {
        return (short) (first / second);
    }

    @Override
    public Short variable(Short in) {
        return in;
    }

    @Override
    public Short negate(Short in) {
        return (short) -in;
    }

    @Override
    public Short intCaster(int in) {
        return (short) in;
    }


    @Override
    public Short caster(String in) {
        return Short.parseShort(in);
    }
}
