package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

@Data
public class SVNChangeLogFileChange {

    private final SVNRevisionInfo revisionInfo;
    private final SVNChangeLogFileChangeType changeType;
    private final String url;

}
