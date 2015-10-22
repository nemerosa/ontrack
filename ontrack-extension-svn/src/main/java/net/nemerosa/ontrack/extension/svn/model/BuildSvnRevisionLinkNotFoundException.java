package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.common.BaseException;

public class BuildSvnRevisionLinkNotFoundException extends BaseException {
    public BuildSvnRevisionLinkNotFoundException(String id) {
        super("Build SVN Revision link not found: %s", id);
    }
}
