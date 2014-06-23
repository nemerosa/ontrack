package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.common.BaseException;

public class UnknownBuildPathExpression extends BaseException {

    public UnknownBuildPathExpression(String expression) {
        super("Unknown build path expression: %s", expression);
    }

}
