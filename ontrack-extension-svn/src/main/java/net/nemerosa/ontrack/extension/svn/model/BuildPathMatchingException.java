package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.common.BaseException;

public class BuildPathMatchingException extends BaseException {

    public BuildPathMatchingException(String name, String expression) {
        super("Build name [%s] does not match expression: %s", name, expression);
    }

}
