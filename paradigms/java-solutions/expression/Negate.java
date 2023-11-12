package expression;

public class Negate implements CommonExpressions {

    CommonExpressions first;

    public Negate(CommonExpressions first) {
        this.first =  first;
    }

    public int evaluate(int x) {
        return -1 * first.evaluate(x);
    }

    public int evaluate(int x, int y, int z) {
        return -1 * first.evaluate(x, y, z);
    }

    @Override
    public String toString() {
        return "-(" + first.toString() + ")";
    }
}