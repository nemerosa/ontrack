package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

import java.io.File;

public class CannotCreateWorkingDirException extends BaseException {
    public CannotCreateWorkingDirException(File dir, Exception ex) {
        super(ex, "Cannot create working directory at %s", dir);
    }
}
