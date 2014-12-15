package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.model.structure.ID;

public class GitProjectNotConfiguredException extends BaseException {
    public GitProjectNotConfiguredException(ID projectId) {
        super("Project %s is not configured for Git.", projectId);
    }
}
