package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

@Data
public class SVNLocation {

    private final String path;
    private final long revision;

    public SVNLocation withRevision(long revision) {
        return new SVNLocation(path, revision);
    }
}
