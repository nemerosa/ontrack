package net.nemerosa.ontrack.extension.issues.export;

import net.nemerosa.ontrack.common.BaseException;

class IssueExportServiceNotFoundException extends BaseException {

    public IssueExportServiceNotFoundException(String format) {
        super("Export service not found: %s", format);
    }

}
