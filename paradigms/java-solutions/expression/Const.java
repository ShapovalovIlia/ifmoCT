package expression;

public class Const implements CommonExpressions {
    private final int value;

    public Const(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public int evaluate(int x) {
        return value;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj != null && obj.getClass() == this.getClass() && value == ((Const) obj).value);
    }

    @Override
    public int hashCode() {
        return value;
    }
}
