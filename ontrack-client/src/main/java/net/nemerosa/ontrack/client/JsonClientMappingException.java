package net.nemerosa.ontrack.client;

import java.io.IOException;

public class JsonClientMappingException extends ClientException {
    public JsonClientMappingException(IOException e) {
        super(e, "Cannot parse JSON");
    }
}
