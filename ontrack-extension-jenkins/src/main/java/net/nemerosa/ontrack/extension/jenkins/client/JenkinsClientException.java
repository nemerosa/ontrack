package net.nemerosa.ontrack.extension.jenkins.client;

import net.nemerosa.ontrack.common.BaseException;

public abstract class JenkinsClientException extends BaseException {

    public JenkinsClientException(String message, Object... params) {
        super(message, params);
    }

    public JenkinsClientException(Exception error, String message, Object... params) {
        super(error, message, params);
    }
}
