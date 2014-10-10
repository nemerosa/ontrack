package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class ExpressionNotStringException extends InputException {
    public ExpressionNotStringException(String expression) {
        super("Expression does not resolve into a string: %s", expression);
    }
}
