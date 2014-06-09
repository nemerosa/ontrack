package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class JsonWritingException extends BaseException {
    public JsonWritingException(Exception ex) {
        super(ex, "Cannot save configuration");
    }
}
