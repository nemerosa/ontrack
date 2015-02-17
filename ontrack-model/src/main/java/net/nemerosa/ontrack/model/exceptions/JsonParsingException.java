package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class JsonParsingException extends BaseException {
    public JsonParsingException(Exception ex) {
        super(ex, "Cannot parse json");
    }
}
