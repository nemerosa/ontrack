package net.nemerosa.ontrack.model.exceptions;

public class ExpressionNotStringException extends InputException {
    public ExpressionNotStringException(String expression) {
        super("Expression does not resolve into a string: %s", expression);
    }
}
