package net.nemerosa.ontrack.json;

public class JsonParseException extends RuntimeException {
    public JsonParseException(Exception e) {
        super("Cannot parse JSON", e);
    }
    public JsonParseException(String message) {
        super(message);
    }
}
