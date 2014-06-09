package net.nemerosa.ontrack.client;

import java.io.IOException;

public class JsonClientParsingException extends ClientException {
    public JsonClientParsingException(IOException e) {
        super(e, "Cannot parse JSON");
    }
}
