package net.nemerosa.ontrack.dsl.http;

public class OTNotAuthorizedException extends OTMessageClientException {

    public OTNotAuthorizedException() {
        super("Not authorised");
    }
}
