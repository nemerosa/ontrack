package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType;

@Data
public class SVNChangeLogFileChange {

    private final SVNRevisionInfo revisionInfo;
    private final SCMChangeLogFileChangeType changeType;
    private final String url;

}
