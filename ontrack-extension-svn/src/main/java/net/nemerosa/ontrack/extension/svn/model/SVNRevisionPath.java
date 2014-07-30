package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType;

@Data
public class SVNRevisionPath {

    private final String path;
    private final SCMChangeLogFileChangeType changeType;

}
