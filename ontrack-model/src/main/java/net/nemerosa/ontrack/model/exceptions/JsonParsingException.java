package net.nemerosa.ontrack.model.exceptions;

public class JsonParsingException extends BaseException {
    public JsonParsingException(String json, Exception ex) {
        super(ex, "Cannot parse configuration");
    }
}
