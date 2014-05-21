package net.nemerosa.ontrack.model.exceptions;

public class JsonWritingException extends BaseException {
    public JsonWritingException(Exception ex) {
        super(ex, "Cannot save configuration");
    }
}
