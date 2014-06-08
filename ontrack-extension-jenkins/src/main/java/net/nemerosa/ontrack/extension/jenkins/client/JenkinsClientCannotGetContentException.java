package net.nemerosa.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientCannotGetContentException extends JenkinsClientException {
    public JenkinsClientCannotGetContentException(AbstractHttpMessage request, Exception e) {
        super(e, "Cannot get any response from Jenkins at %s: %s", request, e);
    }
}
