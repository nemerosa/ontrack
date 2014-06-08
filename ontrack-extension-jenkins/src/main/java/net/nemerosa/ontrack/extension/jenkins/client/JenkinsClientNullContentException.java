package net.nemerosa.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientNullContentException extends JenkinsClientException {
    public JenkinsClientNullContentException(AbstractHttpMessage request) {
        super("No content returned from Jenkins at %s", request);
    }
}
