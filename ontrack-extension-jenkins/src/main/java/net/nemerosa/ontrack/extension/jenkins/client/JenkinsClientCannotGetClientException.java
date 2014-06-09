package net.nemerosa.ontrack.extension.jenkins.client;

import java.util.concurrent.ExecutionException;

public class JenkinsClientCannotGetClientException extends JenkinsClientException {
    public JenkinsClientCannotGetClientException(ExecutionException e, String url) {
        super(e, "Cannot get a Jenkins client for %s: %s", url, e.getMessage());
    }
}
