package net.nemerosa.ontrack.client;

import java.net.MalformedURLException;

public class ClientURLException extends ClientException {
    public ClientURLException(String url, MalformedURLException e) {
        super(e, "Malformed URL: %s", url);
    }
}
