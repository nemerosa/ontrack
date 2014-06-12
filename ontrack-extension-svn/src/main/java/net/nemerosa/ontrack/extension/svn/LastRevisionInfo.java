package net.nemerosa.ontrack.extension.svn;

import lombok.Data;

@Data
public class LastRevisionInfo {

    private final long revision;
    private final String message;
    private final long repositoryRevision;

}
