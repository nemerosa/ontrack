package net.nemerosa.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientCannotParseContentException extends JenkinsClientException {
    public JenkinsClientCannotParseContentException(AbstractHttpMessage request, Exception e) {
        super(e, "Cannot parse the content from Jenkins at %s", request);
    }
}
