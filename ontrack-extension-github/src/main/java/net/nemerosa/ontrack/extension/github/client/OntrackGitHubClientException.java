package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.common.BaseException;

import java.io.IOException;

public class OntrackGitHubClientException extends BaseException {
    public OntrackGitHubClientException(IOException e) {
        super(e, "Error while accessing GitHub: %s", e);
    }
}
