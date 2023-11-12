package expression;

public class Variable implements RandomExpression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public int evaluate(int value) {
        return value;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (name) {
            case "x": return x;
            case "y": return y;
            case "z": return z;
        }
        throw new IllegalArgumentException("Don't have such variable");
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int res = 0;
        for (int i = 0; i < name.length(); i++) {
            res += 31 * res + ((int) name.charAt(i));
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == this.getClass() && name.equals(((Variable) o).name);
    }
}
