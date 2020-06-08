package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public abstract class InputException extends BaseException {
    public InputException(String pattern, Object... parameters) {
        super(pattern, parameters);
    }
}
