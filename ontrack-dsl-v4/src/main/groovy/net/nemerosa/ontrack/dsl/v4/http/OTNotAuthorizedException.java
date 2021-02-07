package net.nemerosa.ontrack.dsl.v4.http;

public class OTNotAuthorizedException extends OTMessageClientException {

    public OTNotAuthorizedException() {
        super("Not authorised");
    }
}
