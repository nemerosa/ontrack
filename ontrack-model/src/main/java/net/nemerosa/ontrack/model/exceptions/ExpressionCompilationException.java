package net.nemerosa.ontrack.model.exceptions;

public class ExpressionCompilationException extends InputException {
    public ExpressionCompilationException(String expression, String message) {
        super("Expression \"%s\" cannot be compiled: %s", expression, message);
    }
}
