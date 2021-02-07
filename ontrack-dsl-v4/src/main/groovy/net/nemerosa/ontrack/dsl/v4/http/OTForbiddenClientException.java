package net.nemerosa.ontrack.dsl.v4.http;

public class OTForbiddenClientException extends OTMessageClientException {

    public OTForbiddenClientException() {
        super("Forbidden");
    }
}
