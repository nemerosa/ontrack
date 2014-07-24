package net.nemerosa.ontrack.extension.git.client.impl;

import net.nemerosa.ontrack.common.BaseException;

import java.io.File;

public class GitCannotCloneException extends BaseException {
    public GitCannotCloneException(File wd) {
        super("Cloning of repository seems to have failed at %s.", wd.getAbsolutePath());
    }
}
