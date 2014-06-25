package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

@Data
public class SVNRevisionPath {

    private final String path;
    private final SVNChangeLogFileChangeType changeType;

}
