package expression.Evaluators;

import java.math.BigInteger;

public class BigIntegerEvaluator implements CommonEvaluator<BigInteger> {
    @Override
    public BigInteger subtract(BigInteger first, BigInteger second) {
        return first.subtract(second);
    }

    @Override
    public BigInteger add(BigInteger first, BigInteger second) {
        return first.add(second);
    }

    @Override
    public BigInteger multiply(BigInteger first, BigInteger second) {
        return first.multiply(second);
    }

    @Override
    public BigInteger divide(BigInteger first, BigInteger second) {
        return first.divide(second);
    }

    @Override
    public BigInteger variable(BigInteger in) {
        return in;
    }


    @Override
    public BigInteger negate(BigInteger in) {
        return in.negate();
    }

    @Override
    public BigInteger intCaster(int in) {
        return BigInteger.valueOf(in);
    }


    @Override
    public BigInteger caster(String in) {
        return new BigInteger(in);
    }
}
