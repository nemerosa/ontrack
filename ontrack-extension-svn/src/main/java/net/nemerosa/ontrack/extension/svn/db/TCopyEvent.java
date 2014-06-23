package net.nemerosa.ontrack.extension.svn.db;

import lombok.Data;

@Data
public class TCopyEvent {

    private final int repository;
    private final long revision;
    private final String copyFromPath;
    private final long copyFromRevision;
    private final String copyToPath;

}
