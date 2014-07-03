package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.common.BaseException;

public class NoBasePathForRevisionPatternException extends BaseException {

    public NoBasePathForRevisionPatternException(String pattern) {
        super("Cannot get a base path for a revision-based pattern: %s", pattern);
    }

}
