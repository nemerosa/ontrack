package net.nemerosa.ontrack.client;

import java.net.MalformedURLException;

public class ClientURLException extends ClientException {
    public ClientURLException(MalformedURLException e) {
        super(e, "Malformed URL");
    }
}
