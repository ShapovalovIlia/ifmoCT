package expression.Evaluators;

public class FloatEvaluator implements CommonEvaluator<Float>{
    @Override
    public Float subtract(Float first, Float second) {
        return first - second;
    }

    @Override
    public Float add(Float first, Float second) {
        return first + second;
    }

    @Override
    public Float multiply(Float first, Float second) {
        return first * second;
    }

    @Override
    public Float divide(Float first, Float second) {
        return first / second;
    }

    @Override
    public Float variable(Float in) {
        return in;
    }

    @Override
    public Float negate(Float in) {
        return -in;
    }

    @Override
    public Float intCaster(int in) {
        return (float) in;
    }


    @Override
    public Float caster(String in) {
        return Float.parseFloat(in);
    }
}
