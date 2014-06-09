package net.nemerosa.ontrack.client;


public class ClientGeneralException extends ClientMessageException {

    public ClientGeneralException(Object request, Exception ex) {
        super(ex, String.format("Error while executing %s: %s", request, ex));
    }

}
