package net.nemerosa.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientNotOKException extends JenkinsClientException {
    public JenkinsClientNotOKException(AbstractHttpMessage request, int statusCode) {
        super("Error while accessing Jenkins at %s: %d", request, statusCode);
    }
}
