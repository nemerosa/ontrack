package net.nemerosa.ontrack.extension.gitlab.client;

import net.nemerosa.ontrack.common.BaseException;

import java.io.IOException;

public class OntrackGitLabClientException extends BaseException {
    public OntrackGitLabClientException(IOException e) {
        super(e, "Error while accessing GitLab: %s", e);
    }
}
