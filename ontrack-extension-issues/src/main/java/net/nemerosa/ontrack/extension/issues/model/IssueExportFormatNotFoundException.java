package net.nemerosa.ontrack.extension.issues.model;

import net.nemerosa.ontrack.common.BaseException;

public class IssueExportFormatNotFoundException extends BaseException {

    public IssueExportFormatNotFoundException(String format) {
        super("The format %s for the issues is not supported.", format);
    }
}
