package net.nemerosa.ontrack.client;


import java.io.IOException;

public class ClientIOException extends ClientMessageException {

    public ClientIOException(Object request, IOException ex) {
        super(ex, String.format("IO error while executing %s: %s", request, ex));
    }

}
