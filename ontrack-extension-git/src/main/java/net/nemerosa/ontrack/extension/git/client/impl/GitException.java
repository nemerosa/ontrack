package net.nemerosa.ontrack.extension.git.client.impl;

import net.nemerosa.ontrack.common.BaseException;

public class GitException extends BaseException {
    public GitException(Exception e) {
        super(e, "Git exception");
    }
}
