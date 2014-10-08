package net.nemerosa.ontrack.acceptance.browser;

import net.nemerosa.ontrack.common.BaseException;

public class CannotBuildPageException extends BaseException {
    public <P extends Page> CannotBuildPageException(Class<P> pageClass, Exception e) {
        super(e, "Cannot build page %s", pageClass);
    }
}
