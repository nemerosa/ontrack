package net.nemerosa.ontrack.extension.git.client.impl;

import net.nemerosa.ontrack.common.BaseException;

public class GitIOException extends BaseException {
    public GitIOException(Exception ex) {
        super(ex, "Git IO exception");
    }
}
