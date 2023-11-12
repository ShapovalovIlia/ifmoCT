package expression;

import java.util.Objects;

public class Variable implements CommonExpressions {
    private final String name;


    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return switch (name) {
            case ("x") -> x;
            case ("y") -> y;
            case ("z") -> z;
            default -> -1;
        };
    }

    @Override
    public boolean equals(Object obj) {
        return (obj != null && obj.getClass() == this.getClass() && Objects.equals(name, ((Variable) obj).name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

