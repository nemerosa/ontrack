package net.nemerosa.ontrack.dsl.http;

public class OTForbiddenClientException extends OTMessageClientException {

    public OTForbiddenClientException() {
        super("Forbidden");
    }
}
