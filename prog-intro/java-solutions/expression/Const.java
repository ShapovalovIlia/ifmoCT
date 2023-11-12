package expression;

public class Const implements RandomExpression {
    private final int value;

    public Const(int num) {
        value = num;
    }

    @Override
    public int evaluate(int x) { return value; }

    @Override
    public int evaluate(int x, int y, int z) { return value; }

    @Override
    public String toString() { return Integer.toString(value); }

    @Override
    public int hashCode() { return value; }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == this.getClass() && value == ((Const) o).value;
    }
}
