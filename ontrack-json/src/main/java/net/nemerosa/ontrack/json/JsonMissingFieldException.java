package net.nemerosa.ontrack.json;

public class JsonMissingFieldException extends RuntimeException {
    public JsonMissingFieldException(String field) {
        super("Required field missing: " + field);
    }
}
