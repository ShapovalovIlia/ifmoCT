package expression.exceptions;

public class ParseException extends Exception  {
// Как лучше в реализации extends Exception или RunTimeException?
// Кажется, что при 1 случае работает чуточку быстрее
    public ParseException(final String message) {
        super(message);
    }
}