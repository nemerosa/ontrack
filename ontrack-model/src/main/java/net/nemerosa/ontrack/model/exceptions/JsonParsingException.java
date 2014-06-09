package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class JsonParsingException extends BaseException {
    public JsonParsingException(String json, Exception ex) {
        super(ex, "Cannot parse configuration");
    }
}
