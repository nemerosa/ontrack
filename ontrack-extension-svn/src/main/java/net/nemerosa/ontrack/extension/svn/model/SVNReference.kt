package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SVNReference {

    private final String path;
    private final String url;
    private final long revision;
    private final LocalDateTime time;

    public SVNLocation toLocation() {
        return new SVNLocation(path, revision);
    }
}
