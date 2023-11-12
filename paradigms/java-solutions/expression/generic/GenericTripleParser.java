package expression.generic;

public interface GenericTripleParser<T> {
    GenericCommonExpressions<T> parse(String expression);
}
