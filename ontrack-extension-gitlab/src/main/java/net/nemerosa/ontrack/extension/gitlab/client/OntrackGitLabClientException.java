package net.nemerosa.ontrack.extension.gitlab.client;

import net.nemerosa.ontrack.common.BaseException;

public class OntrackGitLabClientException extends BaseException {
    public OntrackGitLabClientException(Exception e) {
        super(e, "Error while accessing GitLab: %s", e);
    }
}
