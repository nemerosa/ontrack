package net.nemerosa.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientCallException extends JenkinsClientException {
    public JenkinsClientCallException(AbstractHttpMessage request, Exception e) {
        super(e, "Error while connecting to Jenkins at %s: %s", request, e);
    }
}
