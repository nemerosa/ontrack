package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SVNRevisionInfo {

    private final long revision;
    private final String author;
    private final LocalDateTime dateTime;
    private final String path;
    private final String message;
    private final String revisionUrl;

    public SVNLocation toLocation() {
        return new SVNLocation(path, revision);
    }
}
