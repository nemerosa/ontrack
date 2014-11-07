package net.nemerosa.ontrack.client;

public class OTHttpClientSSLSetupException extends ClientException {
    public OTHttpClientSSLSetupException(Exception ex) {
        super(ex, "Cannot setup SSL client");
    }
}


