package expression.Evaluators;

public class DoubleEvaluator implements CommonEvaluator<Double> {
    @Override
    public Double subtract(Double first, Double second) {
        return first - second;
    }

    @Override
    public Double add(Double first, Double second) {
        return first + second;
    }

    @Override
    public Double multiply(Double first, Double second) {
        return first * second;
    }

    @Override
    public Double divide(Double first, Double second) {
        return first / second;
    }

    @Override
    public Double variable(Double in) {
        return in;
    }

    @Override
    public Double negate(Double in) {
        return -in;
    }

    @Override
    public Double intCaster(int in) {
        return (double) in;
    }


    @Override
    public Double caster(String in) {
        return Double.parseDouble(in);
    }
}
